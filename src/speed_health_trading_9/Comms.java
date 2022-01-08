package speed_health_trading_9;
import battlecode.common.*;

/**
 * Comms strategy
 * 
 * shared_arr[0][0] is set by the first robot of turnq to be equal to Info.round_num % 2
 * shared_arr[0][13:1] is set by the first robot of turnq to 0, and all other robots increment, in order to count turnq order
 *
 * shared_arr[1][16:0] is number of enemy detections on last even turn, during an odd turn
 * shared_arr[2:6][12:6], shared_arr[2:6][6:0] are coords of 4 randomly chosen enemies on last even turn, during an odd turn
 * shared_arr[2:6][12]=1 indicates presence of 4 randomly chosen enemies on last even turn, during an odd turn
 * shared_arr[6][16:0] is number of enemy detections on last odd turn, during an even turn
 * shared_arr[11:7][12:6], shared_arr[11:7][6:0] are coords of 4 randomly chosen enemies on last odd turn, during an even turn
 * shared_arr[11:7][12]=1 indicates presence of 4 randomly chosen enemies on last odd turn, during an even turn
 * 
 * shared_arr[15:11][12:6], shared_arr[15:11][6:0] are the coords of the 4 archons
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
	public static double closest_enemy_dx = 1000;
	public static double closest_enemy_dy = 0;
	public static double closest_enemy_dist = 1000;
	public static MapLocation closest_enemy_loc;
	public static boolean enemy_seen_before = false;
	public static int enemy_seen_round = Integer.MAX_VALUE;
	public static int enemy_distribution_size = 0;
	public static int archon_number = 0;
	public static MapLocation[] send_locs = new MapLocation[4];

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
		
		if (Info.type == RobotType.ARCHON && Info.round_num == 0) {
			archon_number = turnq_num;
		}

		int n_sampled_enemies = 0;
		for (int i=4; --i>=0;) {  // read sampled enemies
			n_sampled_enemies += (shared_arr[7+i-5*round_parity]>>12)%2;
		}
		enemy_seen_before = enemy_seen_before || n_sampled_enemies > 0;
		sampled_enemies = new MapLocation[n_sampled_enemies];
		n_sampled_enemies = 0;
		for (int i=4; --i>=0;) {  // read sampled enemies
			int sampled_enemy_data = shared_arr[7+i-5*round_parity];
			if ((sampled_enemy_data>>12)%2 == 1) {
				sampled_enemies[n_sampled_enemies] = new MapLocation((sampled_enemy_data>>6)%(1<<6), sampled_enemy_data%(1<<6));
				n_sampled_enemies++;
			}
		}
		enemy_distribution_size = Math.min(1<<16, enemy_distribution_size + Info.enemy_robots.length); // sample 4 enemy locations
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
	}
	public static void compute() throws GameActionException {
		set(0, (turnq_num<<1)+round_parity);
		
		set(1+5*round_parity, enemy_distribution_size);
		if (Info.enemy_robots.length > 0) {
			for (int i=4; --i>=0;) {
				if (Info.rng.nextDouble()*enemy_distribution_size < Info.enemy_robots.length) {
					MapLocation send_loc = Info.enemy_robots[Info.rng.nextInt(Info.enemy_robots.length)].location;
					set(2+i+5*round_parity, (send_loc.x<<6) + send_loc.y + (1<<12));
//					rc.setIndicatorLine(Info.loc, send_loc, 255, 0, 0);
				}
			}
		}
	}
	public static void set(int ind, int value) throws GameActionException {
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
