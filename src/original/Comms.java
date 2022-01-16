package original;
import battlecode.common.*;

/**
 * Comms strategy
 * 
 * shared_arr[0][0] is set by the first robot of turnq to be equal to Info.round_num % 2
 * shared_arr[0][13:1] is set by the first robot of turnq to 0, and all other robots increment, in order to count turnq order
 *
 * shared_arr[1][16:0] is number of enemy attacker detections on last even turn, during an odd turn
 * shared_arr[2:6][12:6], shared_arr[2:6][6:0] are coords of 4 randomly chosen enemies on last even turn, during an odd turn
 * shared_arr[2:6][12]=1 indicates presence of 4 randomly chosen enemies on last even turn, during an odd turn
 * shared_arr[2:6][13]=1 indicates presence of 4 randomly chosen enemy attackers on last even turn, during an odd turn
 * shared_arr[6][15:0] is number of enemy/attacker detections on last odd turn, during an even turn
 * shared_arr[6][15]=1 indicates that shared_arr[6][15:0] refers to the count of attackers, rather than enemies
 * shared_arr[7:11][12:6], shared_arr[11:7][6:0] are coords of 4 randomly chosen enemies on last odd turn, during an even turn
 * shared_arr[7:11][12]=1 indicates presence of 4 randomly chosen enemies on last odd turn, during an even turn
 * shared_arr[7:11][13]=1 indicates presence of 4 randomly chosen enemy attackers on last odd turn, during an even turn
 * 
 * shared_arr[15:11][12:6], shared_arr[15:11][6:0] are the coords of the 4 archons
 *
 * shared_arr[15:19][14:0] are the 4 archons' build queue max urgencies
 * shared_arr[15:19][16:14] are the 4 archons' build queue required amounts (0=>40,1=>50,2=>75)
 * 
 * shared_arr[24+6*i+j][16:0] are the excess lead amounts of sector (i,j)
 */

public class Comms {
	public static final double WARZONE_SPEED = 10./RobotType.SOLDIER.movementCooldown;
	public static RobotController rc;
	public static int n_to_send = 0;
	public static int[] ind_to_send = new int[64];
	public static int[] shared_arr = new int[64];
	public static boolean[] sent = new boolean[64];

	public static int turnq_num;
	public static int round_parity;
	public static MapLocation[] sampled_enemies = new MapLocation[0];
	public static MapLocation[] sampled_attackers = new MapLocation[0];
	public static double closest_enemy_dx = 1000;
	public static double closest_enemy_dy = 0;
	public static double closest_enemy_dist = 1000;
	public static MapLocation closest_enemy_loc;
	public static double closest_attacker_dx = 1000;
	public static double closest_attacker_dy = 0;
	public static double closest_attacker_dist = 1000;
	public static MapLocation closest_attacker_loc;
	public static boolean enemy_seen_before = false;
	public static boolean attacker_seen_before = false;
	public static int enemy_seen_round = Integer.MAX_VALUE;
	public static int enemy_distribution_size = 0;
	public static int archon_number = 0;
	public static MapLocation[] send_locs = new MapLocation[4];
	public static int[] archon_build_urgencies = new int[4];
	public static int[] archon_build_lead_requests = new int[4];
	public static int max_other_build_urgencies = 0;
	public static double lead_dx = 0;
	public static double lead_dy = 0;
	public static double lead_dist = 0;
	public static int self_i = 0;
	public static int self_j = 0;
	public static boolean in_sector_center = false;
	public static double xoffset = 0;
	public static double yoffset = 0;
	public static double xinterval = 0;
	public static double yinterval = 0;

	public static void recv() throws GameActionException {
		for (int i=64; --i>=0;) {
			shared_arr[i] = rc.readSharedArray(i);
		}
		round_parity = Info.round_num % 2;
		if (shared_arr[0] % 2 != round_parity) {  // reset turnq timer
			turnq_num = 0;
			enemy_distribution_size = 0;
		}
		else {
			turnq_num = (shared_arr[0]>>1)%(1<<12) + 1; // increment turnq order counter
			enemy_distribution_size = shared_arr[1+5*round_parity];
		}
		
		if (Info.type == RobotType.ARCHON && Info.round_num == 1) {
			archon_number = turnq_num;
			if (archon_number == 1) {
				for (int i=6; --i>=0;) {
					for (int j=6; --j>=0;) {
						set(24+6*i+j, 1);
					}
				}
			}
		}

		int n_sampled_enemies = 0;
		int n_sampled_attackers = 0;
		for (int i=4; --i>=0;) {  // read sampled enemies
			n_sampled_enemies += (shared_arr[7+i-5*round_parity]>>12)%2;
			n_sampled_attackers += (shared_arr[7+i-5*round_parity]>>13)%2;
		}
		enemy_seen_before = enemy_seen_before || n_sampled_enemies > 0;
		enemy_seen_before = enemy_seen_before || Info.enemy_robots.length > 0;
		attacker_seen_before = attacker_seen_before || (shared_arr[6]>>15)%2==1;
		attacker_seen_before = attacker_seen_before || Info.n_enemy_attackers > 0;
		sampled_enemies = new MapLocation[n_sampled_enemies];
		sampled_attackers = new MapLocation[n_sampled_attackers];
		n_sampled_enemies = 0;
		n_sampled_attackers = 0;
		for (int i=4; --i>=0;) {  // read sampled enemies
			int sampled_enemy_data = shared_arr[7+i-5*round_parity];
			if ((sampled_enemy_data>>12)%2 == 1) {
				sampled_enemies[n_sampled_enemies] = new MapLocation((sampled_enemy_data>>6)%(1<<6), sampled_enemy_data%(1<<6));
				n_sampled_enemies++;
			}
			if ((sampled_enemy_data>>13)%2 == 1) {
				sampled_attackers[n_sampled_attackers] = sampled_enemies[n_sampled_enemies-1];
				n_sampled_attackers++;
			}
		}
		if (!attacker_seen_before) {
			enemy_distribution_size = Math.min(1<<16, enemy_distribution_size + Info.enemy_robots.length);  // sample 4 enemy locations
			if (Info.enemy_robots.length > 0) {
				for (int i=4; --i>=0;) {
					if (Info.rng.nextDouble()*enemy_distribution_size < Info.enemy_robots.length) {
						MapLocation send_loc = Info.enemy_robots[Info.rng.nextInt(Info.enemy_robots.length)].location;
						send_locs[i] = send_loc;
						if (Info.loc.isWithinDistanceSquared(send_loc, (int)Math.ceil(closest_enemy_dist*closest_enemy_dist))) {
							closest_enemy_dx = send_loc.x - Info.x;
							closest_enemy_dy = send_loc.y - Info.y;
						}
					}
				}
				closest_enemy_loc = Info.loc.translate((int)closest_enemy_dx, (int)closest_enemy_dy);
				closest_enemy_dist = Math.sqrt(closest_enemy_dx*closest_enemy_dx+closest_enemy_dy*closest_enemy_dy);
			}
		}
		else {
			enemy_distribution_size = Math.min(1<<16, enemy_distribution_size + Info.n_enemy_attackers);  // sample 4 enemy locations
			if (Info.n_enemy_attackers > 0) {
				for (int i=4; --i>=0;) {
					if (Info.rng.nextDouble()*enemy_distribution_size < Info.n_enemy_attackers) {
						int idx = Info.rng.nextInt(Info.n_enemy_attackers);
						MapLocation send_loc = Info.get_enemy_attacker(idx).location;
						send_locs[i] = send_loc;
						if (Info.loc.isWithinDistanceSquared(send_loc, (int)Math.ceil(closest_attacker_dist*closest_attacker_dist))) {
							closest_attacker_dx = send_loc.x - Info.x;
							closest_attacker_dy = send_loc.y - Info.y;
						}
						if (Info.loc.isWithinDistanceSquared(send_loc, (int)Math.ceil(closest_enemy_dist*closest_enemy_dist))) {
							closest_enemy_dx = send_loc.x - Info.x;
							closest_enemy_dy = send_loc.y - Info.y;
						}
					}
				}
				closest_enemy_loc = Info.loc.translate((int)closest_enemy_dx, (int)closest_enemy_dy);
				closest_enemy_dist = Math.sqrt(closest_enemy_dx*closest_enemy_dx+closest_enemy_dy*closest_enemy_dy);
				closest_attacker_loc = Info.loc.translate((int)closest_attacker_dx, (int)closest_attacker_dy);
				closest_attacker_dist = Math.sqrt(closest_attacker_dx*closest_attacker_dx+closest_attacker_dy*closest_attacker_dy);
			}
		}

		if (enemy_seen_before) {  // estimate that the warzone is pushed towards the enemy at some rate over time
			double r = Math.sqrt(closest_enemy_dx*closest_enemy_dx+closest_enemy_dy*closest_enemy_dy);
			double new_r = r+WARZONE_SPEED/(1+rc.senseRubble(Info.loc)/10.);
			closest_enemy_dx = new_r*closest_enemy_dx/r;
			closest_enemy_dy = new_r*closest_enemy_dy/r;
			for (MapLocation enemy_loc:sampled_enemies) {
				if (Info.loc.isWithinDistanceSquared(enemy_loc, (int)Math.ceil(new_r*new_r))) {
					closest_enemy_dx = enemy_loc.x - Info.x;
					closest_enemy_dy = enemy_loc.y - Info.y;
				}
			}
			closest_enemy_loc = Info.loc.translate((int)closest_enemy_dx, (int)closest_enemy_dy);
			closest_enemy_dist = Math.sqrt(closest_enemy_dx*closest_enemy_dx+closest_enemy_dy*closest_enemy_dy);
			enemy_seen_round = Math.min(enemy_seen_round, Info.round_num);
		}
		if (attacker_seen_before) {  // estimate that the warzone is pushed towards the enemy at some rate over time
			double r = Math.sqrt(closest_attacker_dx*closest_attacker_dx+closest_attacker_dy*closest_attacker_dy);
			double new_r = r+WARZONE_SPEED/(1+rc.senseRubble(Info.loc)/10.);
			closest_attacker_dx = new_r*closest_attacker_dx/r;
			closest_attacker_dy = new_r*closest_attacker_dy/r;
			for (MapLocation attacker_loc:sampled_attackers) {
				if (Info.loc.isWithinDistanceSquared(attacker_loc, (int)Math.ceil(new_r*new_r))) {
					closest_attacker_dx = attacker_loc.x - Info.x;
					closest_attacker_dy = attacker_loc.y - Info.y;
				}
			}
			closest_attacker_loc = Info.loc.translate((int)closest_attacker_dx, (int)closest_attacker_dy);
			closest_attacker_dist = Math.sqrt(closest_attacker_dx*closest_attacker_dx+closest_attacker_dy*closest_attacker_dy);
		}
		
		// don't let warzone position estimate exit the map, and reset it if you can see no enemies there
		closest_enemy_dx = Math.min(Math.max(closest_enemy_dx, -Info.x), Info.MAP_WIDTH-Info.x);
		closest_enemy_dy = Math.min(Math.max(closest_enemy_dy, -Info.y), Info.MAP_HEIGHT-Info.y);
		closest_enemy_loc = Info.loc.translate((int)closest_enemy_dx, (int)closest_enemy_dy);
		closest_enemy_dist = Math.sqrt(closest_enemy_dx*closest_enemy_dx+closest_enemy_dy*closest_enemy_dy);
		if (closest_enemy_dist < Math.sqrt(Info.VISION_DIST2)-1 && Info.enemy_robots.length == 0) {
			closest_enemy_dx = closest_enemy_dx*1000 + 1;
			closest_enemy_dy = closest_enemy_dy*1000 + 1;
			closest_enemy_loc = Info.loc.translate((int)closest_enemy_dx, (int)closest_enemy_dy);
			closest_enemy_dist = Math.sqrt(closest_enemy_dx*closest_enemy_dx+closest_enemy_dy*closest_enemy_dy);
		}

		if (Info.type == RobotType.ARCHON) {
			Archon.lead_needed_before_building = 0;
			for (int i=4; --i>=0;) {
				archon_build_urgencies[i] = (i==archon_number)? 0 : shared_arr[15+i]%(1<<14);
				int[] lead_requests_table = new int[] {40, 50, 75};
				archon_build_lead_requests[i] = (i==archon_number)? 0 : lead_requests_table[shared_arr[15+i]>>14];
				if (archon_build_urgencies[i] > Archon.communicated_urgency && i > archon_number) {
					Archon.lead_needed_before_building += archon_build_lead_requests[i];
				}
			}
			Archon.lead_needed_before_building += Archon.desired_lead;
		}
		
		if (Info.type == RobotType.MINER) {
			xoffset = Info.MAP_WIDTH/12.;
			yoffset = Info.MAP_HEIGHT/12.;
			xinterval = Info.MAP_WIDTH/6.;
			yinterval = Info.MAP_HEIGHT/6.;
			self_i = 6*Info.x / Info.MAP_WIDTH;
			self_j = 6*Info.y / Info.MAP_HEIGHT;
			double self_xdiff = xoffset + self_i*xinterval - Info.x;
			double self_ydiff = yoffset + self_j*yinterval - Info.y;
			in_sector_center = self_xdiff*self_xdiff+self_ydiff*self_ydiff <= 5;
		}
	}
	
	public static void compute_lead_direction() {
		lead_dx = 0;
		lead_dy = 0;
		int sector_num = 24+6*self_i+self_j;
		double xoffset2 = ((xoffset - Info.x) / xinterval - 4);
		double yoffset2 = ((yoffset - Info.y) / yinterval);
		for (int ind=60; --ind>=sector_num+1;) {
			double xdiff = xoffset2 + ind/6;
			double ydiff = yoffset2 + ind%6;
			double r2 = xdiff*xdiff+ydiff*ydiff;
			lead_dx += shared_arr[ind] * xdiff / r2;
			lead_dy += shared_arr[ind] * ydiff / r2;
//			if (shared_arr[ind] == 0) {
//				rc.setIndicatorDot(new MapLocation((int)Math.floor(Info.x + xdiff*xinterval), (int)Math.floor(Info.y + ydiff*yinterval)), 255, 0, 0);
//			}
//			else if (shared_arr[ind] == 1) {
//				rc.setIndicatorDot(new MapLocation((int)Math.floor(Info.x + xdiff*xinterval), (int)Math.floor(Info.y + ydiff*yinterval)), 255, 255, 0);
//			}
//			else {
//				rc.setIndicatorDot(new MapLocation((int)Math.floor(Info.x + xdiff*xinterval), (int)Math.floor(Info.y + ydiff*yinterval)), 0, 255, 0);
//			}
		}
		for (int ind=sector_num; --ind>=24;) {
			double xdiff = xoffset2 + ind/6;
			double ydiff = yoffset2 + ind%6;
			double r2 = xdiff*xdiff+ydiff*ydiff;
			lead_dx += shared_arr[ind] * xdiff / r2;
			lead_dy += shared_arr[ind] * ydiff / r2;
//			if (shared_arr[ind] == 0) {
//				rc.setIndicatorDot(new MapLocation((int)Math.floor(Info.x + xdiff*xinterval), (int)Math.floor(Info.y + ydiff*yinterval)), 255, 0, 0);
//			}
//			else if (shared_arr[ind] == 1) {
//				rc.setIndicatorDot(new MapLocation((int)Math.floor(Info.x + xdiff*xinterval), (int)Math.floor(Info.y + ydiff*yinterval)), 255, 255, 0);
//			}
//			else {
//				rc.setIndicatorDot(new MapLocation((int)Math.floor(Info.x + xdiff*xinterval), (int)Math.floor(Info.y + ydiff*yinterval)), 0, 255, 0);
//			}
		}
//		for (int ind=60; --ind>=24;) {
//			if (ind!=sector_num) {
//				int xdiff = xoffset2 + ind/6;
//				int ydiff = yoffset2 + ind%6;
//				double r2 = xdiff*xdiff+ydiff*ydiff;
//				lead_dx += shared_arr[ind] * xdiff / r2;
//				lead_dy += shared_arr[ind] * ydiff / r2;
//			}
//		}
		lead_dist = Math.max(0.00001, Math.sqrt(lead_dx*lead_dx+lead_dy*lead_dy));
	}
	
	public static void compute() throws GameActionException {
		set(0, (turnq_num<<1)+round_parity);
		
		set(1+5*round_parity, enemy_distribution_size);
		if (Info.n_enemy_attackers > 0 && attacker_seen_before) {
			for (int i=4; --i>=0;) {
				if (Info.rng.nextDouble()*enemy_distribution_size < Info.n_enemy_attackers) {
					MapLocation send_loc = Info.get_enemy_attacker(Info.rng.nextInt(Info.n_enemy_attackers)).location;
					set(2+i+5*round_parity, (send_loc.x<<6) + send_loc.y + (1<<12) + (1<<13));
//					rc.setIndicatorLine(Info.loc, send_loc, 255, 0, 0);
				}
			}
		}
		if (Info.enemy_robots.length > 0 && !attacker_seen_before) {
			for (int i=4; --i>=0;) {
				if (Info.rng.nextDouble()*enemy_distribution_size < Info.enemy_robots.length) {
					MapLocation send_loc = Info.enemy_robots[Info.rng.nextInt(Info.enemy_robots.length)].location;
					set(2+i+5*round_parity, (send_loc.x<<6) + send_loc.y + (1<<12));
//					rc.setIndicatorLine(Info.loc, send_loc, 255, 0, 0);
				}
			}
		}
		
		if (Info.type == RobotType.ARCHON) {
			Archon.decide_what_to_build_next_turn();
			int build_type = (Archon.desired_robot==RobotType.BUILDER)? 0 : 
				((Archon.desired_robot==RobotType.MINER)? 1 : 2);
			set(15+archon_number, Math.min(Archon.communicated_urgency, (1<<14)-1) + (build_type<<14));
		}
		
		if (Info.type == RobotType.MINER) {
//			self_lead_excess = Math.min(Math.max(lead_excess[sector_i][sector_j], self_lead_excess), self_lead_excess+1);
			if (in_sector_center) {
				set(24+6*self_i+self_j, Math.min((1<<16)-1, Miner.self_lead_excess));
			}
		}
	}
	public static void set(int ind, int value) throws GameActionException {
		if (value > 65535) {
			throw new Error(String.valueOf(value));
		}
		if (shared_arr[ind] != value) {
			shared_arr[ind] = value;
			ind_to_send[n_to_send] = ind;
			n_to_send++;
		}
	}
	public static void send() throws GameActionException {
		for (int i=n_to_send; --i>=0;) {
			if (!sent[ind_to_send[i]]) {
				rc.writeSharedArray(ind_to_send[i], shared_arr[ind_to_send[i]]);
				sent[ind_to_send[i]] = true;
			}
		}
		for (int i=n_to_send; --i>=0;) {
			sent[ind_to_send[i]] = false;
		}
		n_to_send = 0;
	}
}
