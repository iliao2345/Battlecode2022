package debugging_16;
import battlecode.common.*;

/**
 * Comms strategy
 * 
 * shared_arr[0][0] is set by the first robot of turnq to be equal to Info.round_num % 2
 * shared_arr[0][13:1] is set by the first robot of turnq to 0, and all other robots increment, in order to count turnq order
 * shared_arr[0][13], shared_arr[0][14], shared_arr[0][15] indicates whether x, y, and xy symmetry are impossible, respectively
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
 * shared_arr[11:15][12:6], shared_arr[15:11][6:0] are the coords of the 4 archons
 *
 * shared_arr[15:19][13:0] are the 4 archons' build queue max urgencies
 * shared_arr[15:19][13] are 1 iff the 4 archons did indeed build, or 0 iff they are still reserving the amount
 * shared_arr[15:19][16:14] are the 4 archons' build queue required amounts (0=>40,1=>50,2=>75)
 * 
 * shared_arr[19][8:0] is set to 0 by last archon and is incremented by builders
 * shared_arr[19][16:8] is set to shared_arr[19][8:0] before shared_arr[19][8:0] gets reset. it serves as a builders count
 * 
 * shared_arr[20] is empty
 * 
 * shared_arr[21:25][11:0] are set by each archon to be equal to round number, to check whether they are alive or not
 * shared_arr[21:25][11]==1 denotes that each of the 4 archons is portable
 * 
 * shared_arr[25:29] is the amount of health of units in line to be healed by each archon
 * shared_arr[29] is the number of starting archons
 * 
 * shared_arr[30][0] is set to 0 by last archon and is set to 1 by builders
 * shared_arr[30][8:1] is set to 0 by last archon and is incremented by labs
 * shared_arr[30][15:8] is set to shared_arr[30][8:1] before shared_arr[30][8:1] gets reset. it serves as a lab count
 * 
 * shared_arr[31][16:12] is the number of things minus 2 in the most crowded lab's vision radius, and shared_arr[31][12:6], shared_arr[31][6:0] are the coords of the lab.
 * shared_arr[31][16:12] gets decremented by archon every turn, and more crowded labs take priority.
 * 
 * shared_arr[32:36][8:0] are the 4 archons' closest enemy distances, or 0 if the enemy hasn't been seen before
 * shared_arr[32:36][16:8] are the 4 archons' total lead seen / 5
 * 
 * shared_arr[36][16:0], shared_arr[37][16:0] is the soldier count on last even and odd turns, respectively.
 * 
 * shared_arr[38:42][8:0] is the miner count for each archon on last even turn
 * shared_arr[38:42][16:8] is the mining miner count for each archon on last even turn
 * shared_arr[42:46][8:0] is the miner count for each archon on last odd turn
 * shared_arr[42:46][16:8] is the mining miner count for each archon on last odd turn
 */

public class Comms {
	public static final double WARZONE_SPEED = 4*10./RobotType.SOLDIER.movementCooldown;
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
	public static int archon_number = 4;  // can be anything >3, will be reset to the correct number by anything that's actually an archon
	public static int[] archon_build_urgencies = new int[4];
	public static int[] archon_build_lead_requests = new int[4];
	public static int max_other_build_urgencies = 0;
	public static MapLocation[] archon_positions;
	public static boolean[] archons_alive;
	public static int last_recvd_round = -1;
	public static boolean x_symmetry_possible = true;
	public static boolean y_symmetry_possible = true;
	public static boolean xy_symmetry_possible = true;
	public static boolean symmetry_determined = false;
	public static MapLocation[] possible_enemy_archon_locs = new MapLocation[0];
	public static int total_miner_count = 0;
	public static int[] archon_healing_health;
	public static boolean[] archons_portable;
	public static boolean most_urgent_archon = false;
	public static int n_starting_archons = 0;
	public static boolean soldier_built = false;
	public static boolean builder_exists = false;
	public static int laboratory_count = 0;
	public static MapLocation crowded_lab_loc = null;
	public static boolean lab_crowded = false;
	public static int last_builder_did_not_exist_round = Integer.MIN_VALUE;
	public static boolean[] archon_enemy_seen_before;
	public static int[] archon_closest_enemy_dists;
	public static int[] archon_total_leads;
	public static int total_soldier_count = 0;
	public static int total_mining_miner_count = 0;
	public static int archon_miner_count = 0;
	public static int archon_mining_miner_count = 0;
	public static int total_builder_count = 0;
	
	public static void recv() throws GameActionException {
		if (last_recvd_round < Info.round_num) {
			last_recvd_round = Info.round_num;
			shared_arr[0] = rc.readSharedArray(0);
			shared_arr[1] = rc.readSharedArray(1);
			shared_arr[2] = rc.readSharedArray(2);
			shared_arr[3] = rc.readSharedArray(3);
			shared_arr[4] = rc.readSharedArray(4);
			shared_arr[5] = rc.readSharedArray(5);
			shared_arr[6] = rc.readSharedArray(6);
			shared_arr[7] = rc.readSharedArray(7);
			shared_arr[8] = rc.readSharedArray(8);
			shared_arr[9] = rc.readSharedArray(9);
			shared_arr[10] = rc.readSharedArray(10);
			shared_arr[11] = rc.readSharedArray(11);
			shared_arr[12] = rc.readSharedArray(12);
			shared_arr[13] = rc.readSharedArray(13);
			shared_arr[14] = rc.readSharedArray(14);
			shared_arr[15] = rc.readSharedArray(15);
			shared_arr[16] = rc.readSharedArray(16);
			shared_arr[17] = rc.readSharedArray(17);
			shared_arr[18] = rc.readSharedArray(18);
			shared_arr[19] = rc.readSharedArray(19);
//			shared_arr[20] = rc.readSharedArray(20);
			shared_arr[21] = rc.readSharedArray(21);
			shared_arr[22] = rc.readSharedArray(22);
			shared_arr[23] = rc.readSharedArray(23);
			shared_arr[24] = rc.readSharedArray(24);
			shared_arr[25] = rc.readSharedArray(25);
			shared_arr[26] = rc.readSharedArray(26);
			shared_arr[27] = rc.readSharedArray(27);
			shared_arr[28] = rc.readSharedArray(28);
			shared_arr[29] = rc.readSharedArray(29);
			shared_arr[30] = rc.readSharedArray(30);
			shared_arr[31] = rc.readSharedArray(31);
			shared_arr[32] = rc.readSharedArray(32);
			shared_arr[33] = rc.readSharedArray(33);
			shared_arr[34] = rc.readSharedArray(34);
			shared_arr[35] = rc.readSharedArray(35);
			shared_arr[36] = rc.readSharedArray(36);
			shared_arr[37] = rc.readSharedArray(37);
			shared_arr[38] = rc.readSharedArray(38);
			shared_arr[39] = rc.readSharedArray(39);
			shared_arr[40] = rc.readSharedArray(40);
			shared_arr[41] = rc.readSharedArray(41);
			shared_arr[42] = rc.readSharedArray(42);
			shared_arr[43] = rc.readSharedArray(43);
			shared_arr[44] = rc.readSharedArray(44);
			shared_arr[45] = rc.readSharedArray(45);
//			shared_arr[46] = rc.readSharedArray(46);
//			shared_arr[47] = rc.readSharedArray(47);
//			shared_arr[48] = rc.readSharedArray(48);
//			shared_arr[49] = rc.readSharedArray(49);
//			shared_arr[50] = rc.readSharedArray(50);
//			shared_arr[51] = rc.readSharedArray(51);
//			shared_arr[52] = rc.readSharedArray(52);
//			shared_arr[53] = rc.readSharedArray(53);
//			shared_arr[54] = rc.readSharedArray(54);
//			shared_arr[55] = rc.readSharedArray(55);
//			shared_arr[56] = rc.readSharedArray(56);
//			shared_arr[57] = rc.readSharedArray(57);
//			shared_arr[58] = rc.readSharedArray(58);
//			shared_arr[59] = rc.readSharedArray(59);
//			shared_arr[60] = rc.readSharedArray(60);
//			shared_arr[61] = rc.readSharedArray(61);
//			shared_arr[62] = rc.readSharedArray(62);
//			shared_arr[63] = rc.readSharedArray(63);
		}
	}
	
	public static void initialize() throws GameActionException {
		recv();
		if (rc.getRoundNum()==1) {
			n_starting_archons = rc.getArchonCount();
		}
		else {
			n_starting_archons = shared_arr[29];
		}
		archons_alive = new boolean[n_starting_archons];
		for (int i=archons_alive.length; --i>=0;) {
			archons_alive[i] = true;
		}
		archon_positions = new MapLocation[n_starting_archons];
		archon_healing_health = new int[n_starting_archons];
		archons_portable = new boolean[n_starting_archons];
		archon_enemy_seen_before = new boolean[n_starting_archons];
		archon_closest_enemy_dists = new int[n_starting_archons];
		archon_total_leads = new int[n_starting_archons];
	}

	public static void update() throws GameActionException {
		recv();
		round_parity = Info.round_num % 2;
		if (shared_arr[0] % 2 != round_parity) {  // reset turnq timer
			turnq_num = 0;
			enemy_distribution_size = 0;
			for (int i=4; --i>=0;) {
				set(2+i+5*round_parity, 0);
			}
		}
		else {
			turnq_num = (shared_arr[0]>>1)%(1<<12) + 1; // increment turnq order counter
			enemy_distribution_size = shared_arr[1+5*round_parity];
		}
		x_symmetry_possible = x_symmetry_possible && (shared_arr[0]>>13)%2==0;
		y_symmetry_possible = y_symmetry_possible && (shared_arr[0]>>14)%2==0;
		xy_symmetry_possible = xy_symmetry_possible && (shared_arr[0]>>15)%2==0;
		
		if (Info.type == RobotType.ARCHON && Info.round_num == 1) {  // count archons
			archon_number = turnq_num;
			if (archon_number == 0) {
				set(29, n_starting_archons);
			}
		}
		
		for (int i=archon_positions.length; --i>=0;) {  // track archon coords, aliveness, and portability
			int archon_data = shared_arr[11+i];
			archon_positions[i] = new MapLocation((archon_data>>6)%(1<<6), archon_data%(1<<6));
			archons_alive[i] = shared_arr[21+i]%(1<<11) == Info.round_num - ((archon_number < i)? 1 : 0);
			archons_portable[i] = (shared_arr[21+i]>>11)%2 == 1;
//			if (archons_alive[i]) {
//				rc.setIndicatorDot(archon_positions[i], 0, 255, 0);
//			}
//			else {
//				rc.setIndicatorDot(archon_positions[i], 255, 0, 0);
//			}
		}
		if (Info.type == RobotType.ARCHON && Info.round_num == 2) {  // archon symmetry detection
			for (int i=archon_positions.length; --i>=0;) {
				int enemy_opp_x = Info.MAP_WIDTH - 1 - archon_positions[i].x;
				int enemy_opp_y = Info.MAP_HEIGHT - 1 - archon_positions[i].y;
				int friendly_opp_x = Info.MAP_WIDTH - 1 - archon_positions[i].x;
				int friendly_opp_y = Info.MAP_HEIGHT - 1 - archon_positions[i].y;
				if (x_symmetry_possible) {
					MapLocation test_loc = new MapLocation(enemy_opp_x, archon_positions[i].y);
					if (rc.canSenseLocation(test_loc)) {
						RobotInfo robot = rc.senseRobotAtLocation(test_loc);
						if (robot == null || robot.type != RobotType.ARCHON || robot.team != Info.enemy) {
							x_symmetry_possible = false;
						}
					}
					test_loc = new MapLocation(friendly_opp_x, archon_positions[i].y);
					if (rc.canSenseLocation(test_loc)) {
						RobotInfo robot = rc.senseRobotAtLocation(test_loc);
						if (robot == null || robot.type != RobotType.ARCHON || robot.team != Info.friendly) {
							x_symmetry_possible = false;
						}
					}
				}
				if (y_symmetry_possible) {
					MapLocation test_loc = new MapLocation(archon_positions[i].x, enemy_opp_y);
					if (rc.canSenseLocation(test_loc)) {
						RobotInfo robot = rc.senseRobotAtLocation(test_loc);
						if (robot == null || robot.type != RobotType.ARCHON || robot.team != Info.enemy) {
							y_symmetry_possible = false;
						}
					}
					test_loc = new MapLocation(archon_positions[i].x, friendly_opp_y);
					if (rc.canSenseLocation(test_loc)) {
						RobotInfo robot = rc.senseRobotAtLocation(test_loc);
						if (robot == null || robot.type != RobotType.ARCHON || robot.team != Info.friendly) {
							x_symmetry_possible = false;
						}
					}
				}
				if (xy_symmetry_possible) {
					MapLocation test_loc = new MapLocation(enemy_opp_x, enemy_opp_y);
					if (rc.canSenseLocation(test_loc)) {
						RobotInfo robot = rc.senseRobotAtLocation(test_loc);
						if (robot == null || robot.type != RobotType.ARCHON || robot.team != Info.enemy) {
							xy_symmetry_possible = false;
						}
					}
					test_loc = new MapLocation(friendly_opp_x, friendly_opp_y);
					if (rc.canSenseLocation(test_loc)) {
						RobotInfo robot = rc.senseRobotAtLocation(test_loc);
						if (robot == null || robot.type != RobotType.ARCHON || robot.team != Info.friendly) {
							x_symmetry_possible = false;
						}
					}
				}
			}
		}
		// use other units to sense rubble and determine symmetry
		symmetry_determined = symmetry_determined || (x_symmetry_possible ^ y_symmetry_possible ^ xy_symmetry_possible) && !(x_symmetry_possible && y_symmetry_possible && xy_symmetry_possible);
		if (!symmetry_determined) {
			if (x_symmetry_possible) {
				int test_x = Info.x + (int)(Math.signum((Info.MAP_WIDTH-1)/2.-Info.x)*Math.floor(Math.sqrt(Info.VISION_DIST2)));
				if (Info.loc.isWithinDistanceSquared(new MapLocation(Info.MAP_WIDTH-1-test_x, Info.y), Info.VISION_DIST2)) {
					if (rc.senseRubble(new MapLocation(test_x, Info.y)) != rc.senseRubble(new MapLocation(Info.MAP_WIDTH-1-test_x, Info.y))) {
						x_symmetry_possible = false;
					}
				}
			}
			if (y_symmetry_possible) {
				int test_y = Info.y + (int)(Math.signum((Info.MAP_HEIGHT-1)/2.-Info.y)*Math.floor(Math.sqrt(Info.VISION_DIST2)));
				if (Info.loc.isWithinDistanceSquared(new MapLocation(Info.x, Info.MAP_HEIGHT-1-test_y), Info.VISION_DIST2)) {
					if (rc.senseRubble(new MapLocation(Info.x, test_y)) != rc.senseRubble(new MapLocation(Info.x, Info.MAP_HEIGHT-1-test_y))) {
						y_symmetry_possible = false;
					}
				}
			}
			if (xy_symmetry_possible) {
				if (Info.loc.isWithinDistanceSquared(new MapLocation(Info.MAP_WIDTH-1-Info.x, Info.MAP_HEIGHT-1-Info.y), Info.VISION_DIST2)) {
					if (rc.senseRubble(new MapLocation(Info.x, Info.y)) != rc.senseRubble(new MapLocation(Info.MAP_WIDTH-1-Info.x, Info.MAP_HEIGHT-1-Info.y))) {
						xy_symmetry_possible = false;
					}
				}
			}
		}
		
		if (Info.type == RobotType.ARCHON && (Info.round_num == 2 || Info.round_num == 3)) {  // enemy archon position evaluation
			int n_possible_enemy_archon_locs = archon_positions.length*((x_symmetry_possible?1:0)+(y_symmetry_possible?1:0)+(xy_symmetry_possible?1:0));
			possible_enemy_archon_locs = new MapLocation[n_possible_enemy_archon_locs];
			int counter = 0;
			for (int i=archon_positions.length; --i>=0;) {
				int opp_x = Info.MAP_WIDTH - 1 - archon_positions[i].x;
				int opp_y = Info.MAP_HEIGHT - 1 - archon_positions[i].y;
				if (x_symmetry_possible) {
					possible_enemy_archon_locs[counter] = new MapLocation(opp_x, archon_positions[i].y);
					counter++;
				}
				if (y_symmetry_possible) {
					possible_enemy_archon_locs[counter] = new MapLocation(archon_positions[i].x, opp_y);
					counter++;
				}
				if (xy_symmetry_possible) {
					possible_enemy_archon_locs[counter] = new MapLocation(opp_x, opp_y);
					counter++;
				}
			}
		}

		total_miner_count = 0;
		total_mining_miner_count = 0;
		for (int i=archon_positions.length; --i>=0;) {
			if (archons_alive[i]) {
				total_miner_count += shared_arr[42+i-4*(Info.round_num%2)]%256;
				total_mining_miner_count += shared_arr[42+i-4*(Info.round_num%2)]>>8;
			}
		}
		total_soldier_count = shared_arr[37-Info.round_num%2];
		total_builder_count = shared_arr[19]>>8;
		if (Info.type == RobotType.ARCHON) {  // count miners and track builder presence and count labs
			builder_exists = shared_arr[30]%2 == 1;  // builder presence check
			last_builder_did_not_exist_round = builder_exists? last_builder_did_not_exist_round : Info.round_num;
			boolean first_archon = true;
			for (int i=archon_number; --i>=0;) {
				if (archons_alive[i]) {
					first_archon = false;
				}
			}
			boolean last_archon = true;
			for (int i=archon_positions.length; --i>=archon_number+1;) {
				if (archons_alive[i]) {
					last_archon = false;
				}
			}
			archon_miner_count = shared_arr[42+archon_number-4*(Info.round_num%2)]%256;
			archon_mining_miner_count = shared_arr[42+archon_number-4*(Info.round_num%2)]>>8;
			set(38+archon_number+4*(Info.round_num%2), 0);  // miner counter, mining miner counter
			if (first_archon) {
				int builder_presence_flag = shared_arr[30] % 2;  // reset lab counter
				set(30, (((shared_arr[30]>>1)%(1<<7))<<8) + builder_presence_flag);
				set(19, ((shared_arr[19]%(1<<8))<<8));  // reset builder counter
			}
			if (last_archon) {
				set(36+Info.round_num%2, 0);  // soldier counter
				set(31, shared_arr[31] - ((shared_arr[31] >= (1<<12))? (1<<12) : 0));  // decrement lab crowdedness
			}
		}
		laboratory_count = (shared_arr[30]>>8)%(1<<7);
		if (Info.type == RobotType.MINER) {
			int write_index = 38+Info.spawn_archon_index+4*(Info.round_num%2);
			rc.setIndicatorString(String.valueOf(Info.spawn_archon_index));
			set(write_index, (Math.min(255, (shared_arr[write_index]>>8)+(Miner.employed? 1 : 0))<<8) + Math.min(255, shared_arr[write_index]%256+1));
		}
		if (Info.type == RobotType.SOLDIER) {
			set(36+Info.round_num%2, shared_arr[36+Info.round_num%2]+1);
		}
		
		for (int i=archon_positions.length; --i>=0;) {  // track archon healing demand
			if (archons_alive[i] && !archons_portable[i]) {
				archon_healing_health[i] = shared_arr[25+i];
			}
			else {
				archon_healing_health[i] = (1<<16)-1;
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
//				rc.setIndicatorLine(Info.loc, sampled_enemies[n_sampled_enemies], 255, 255, 0);
				n_sampled_enemies++;
			}
			if ((sampled_enemy_data>>13)%2 == 1) {
				sampled_attackers[n_sampled_attackers] = sampled_enemies[n_sampled_enemies-1];
				n_sampled_attackers++;
			}
		}
		if (Info.enemy_robots.length > 0) {
			MapLocation loc = Info.enemy_robots[Info.rng.nextInt(Info.enemy_robots.length)].location;
			if (Info.loc.isWithinDistanceSquared(loc, (int)Math.ceil(closest_enemy_dist*closest_enemy_dist))) {
				closest_enemy_dx = loc.x - Info.x;
				closest_enemy_dy = loc.y - Info.y;
				closest_enemy_loc = Info.loc.translate((int)closest_enemy_dx, (int)closest_enemy_dy);
				closest_enemy_dist = Math.sqrt(closest_enemy_dx*closest_enemy_dx+closest_enemy_dy*closest_enemy_dy);
			}
		}
		if (Info.n_enemy_attackers > 0) {
			MapLocation loc = Info.get_enemy_attacker(Info.rng.nextInt(Info.n_enemy_attackers)).location;
			if (Info.loc.isWithinDistanceSquared(loc, (int)Math.ceil(closest_attacker_dist*closest_attacker_dist))) {
				closest_attacker_dx = loc.x - Info.x;
				closest_attacker_dy = loc.y - Info.y;
				closest_attacker_loc = Info.loc.translate((int)closest_attacker_dx, (int)closest_attacker_dy);
				closest_attacker_dist = Math.sqrt(closest_attacker_dx*closest_attacker_dx+closest_attacker_dy*closest_attacker_dy);
			}
		}
//		if (!attacker_seen_before) {
//			enemy_distribution_size = Math.min(1<<16, enemy_distribution_size + Info.enemy_robots.length);  // sample 4 enemy locations
//			if (Info.enemy_robots.length > 0) {
//				if (Info.rng.nextDouble()*enemy_distribution_size < Info.enemy_robots.length) {
//					MapLocation send_loc = Info.enemy_robots[Info.rng.nextInt(Info.enemy_robots.length)].location;
//					if (Info.loc.isWithinDistanceSquared(send_loc, (int)Math.ceil(closest_enemy_dist*closest_enemy_dist))) {
//						closest_enemy_dx = send_loc.x - Info.x;
//						closest_enemy_dy = send_loc.y - Info.y;
//						closest_enemy_loc = Info.loc.translate((int)closest_enemy_dx, (int)closest_enemy_dy);
//						closest_enemy_dist = Math.sqrt(closest_enemy_dx*closest_enemy_dx+closest_enemy_dy*closest_enemy_dy);
//					}
//				}
//			}
//		}
//		else {
//			enemy_distribution_size = Math.min(1<<16, enemy_distribution_size + Info.n_enemy_attackers);  // sample 4 enemy locations
//			if (Info.n_enemy_attackers > 0) {
//				if (Info.rng.nextDouble()*enemy_distribution_size < Info.n_enemy_attackers) {
//					int idx = Info.rng.nextInt(Info.n_enemy_attackers);
//					MapLocation send_loc = Info.get_enemy_attacker(idx).location;
//					if (Info.loc.isWithinDistanceSquared(send_loc, (int)Math.ceil(closest_attacker_dist*closest_attacker_dist))) {
//						closest_attacker_dx = send_loc.x - Info.x;
//						closest_attacker_dy = send_loc.y - Info.y;
//						closest_attacker_loc = Info.loc.translate((int)closest_attacker_dx, (int)closest_attacker_dy);
//						closest_attacker_dist = Math.sqrt(closest_attacker_dx*closest_attacker_dx+closest_attacker_dy*closest_attacker_dy);
//					}
//					if (Info.loc.isWithinDistanceSquared(send_loc, (int)Math.ceil(closest_enemy_dist*closest_enemy_dist))) {
//						closest_enemy_dx = send_loc.x - Info.x;
//						closest_enemy_dy = send_loc.y - Info.y;
//						closest_enemy_loc = Info.loc.translate((int)closest_enemy_dx, (int)closest_enemy_dy);
//						closest_enemy_dist = Math.sqrt(closest_enemy_dx*closest_enemy_dx+closest_enemy_dy*closest_enemy_dy);
//					}
//				}
//			}
//		}

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
//		closest_enemy_dx = Math.min(Math.max(closest_enemy_dx, -Info.x), Info.MAP_WIDTH-Info.x-1);
//		closest_enemy_dy = Math.min(Math.max(closest_enemy_dy, -Info.y), Info.MAP_HEIGHT-Info.y-1);
		closest_enemy_loc = Info.loc.translate((int)closest_enemy_dx, (int)closest_enemy_dy);
		closest_enemy_dist = Math.sqrt(closest_enemy_dx*closest_enemy_dx+closest_enemy_dy*closest_enemy_dy);
		if (closest_enemy_dist < Math.sqrt(Info.VISION_DIST2)-1 && Info.enemy_robots.length == 0) {
			closest_enemy_dx = -closest_enemy_dx*1000 + 1;
			closest_enemy_dy = -closest_enemy_dy*1000 + 1;
			closest_enemy_loc = Info.loc.translate((int)closest_enemy_dx, (int)closest_enemy_dy);
			closest_enemy_dist = Math.sqrt(closest_enemy_dx*closest_enemy_dx+closest_enemy_dy*closest_enemy_dy);
		}
		// don't let warzone position estimate exit the map, and reset it if you can see no enemies there
//		closest_attacker_dx = Math.min(Math.max(closest_attacker_dx, -Info.x), Info.MAP_WIDTH-Info.x-1);
//		closest_attacker_dy = Math.min(Math.max(closest_attacker_dy, -Info.y), Info.MAP_HEIGHT-Info.y-1);
		closest_attacker_loc = Info.loc.translate((int)closest_attacker_dx, (int)closest_attacker_dy);
		closest_attacker_dist = Math.sqrt(closest_attacker_dx*closest_attacker_dx+closest_attacker_dy*closest_attacker_dy);
		if (closest_attacker_dist < Math.sqrt(Info.VISION_DIST2)-1 && Info.enemy_robots.length == 0) {
			closest_attacker_dx = -closest_attacker_dx*1000 + 1;
			closest_attacker_dy = -closest_attacker_dy*1000 + 1;
			closest_attacker_loc = Info.loc.translate((int)closest_attacker_dx, (int)closest_attacker_dy);
			closest_attacker_dist = Math.sqrt(closest_attacker_dx*closest_attacker_dx+closest_attacker_dy*closest_attacker_dy);
		}

		if (Info.type == RobotType.ARCHON) {
			soldier_built = false;
			most_urgent_archon = true;
			Archon.lead_needed_before_building = 0;
			for (int i=archon_positions.length; --i>=0;) {
				if (archons_alive[i] && !archons_portable[i]) {
					archon_build_urgencies[i] = (i==archon_number)? 0 : shared_arr[15+i]%(1<<13);
					int[] lead_requests_table = new int[] {40, 50, 75};
					archon_build_lead_requests[i] = (i==archon_number)? 0 : lead_requests_table[shared_arr[15+i]>>14];
					boolean archon_i_built = (shared_arr[15+i]>>13)%2==1;
					soldier_built = soldier_built || archon_i_built && shared_arr[15+i]>>14==2;
					if (archon_build_urgencies[i] > Archon.communicated_urgency && (i > archon_number || i < archon_number && !archon_i_built)) {
						Archon.lead_needed_before_building += archon_build_lead_requests[i];
					}
					most_urgent_archon = most_urgent_archon && archon_build_urgencies[i] <= Archon.communicated_urgency;
				}
				archon_enemy_seen_before[i] = shared_arr[32+i]%256 != 0;
				archon_closest_enemy_dists[i] = shared_arr[32+i]%256;
				archon_total_leads[i] = (shared_arr[32+i]>>8) * 5;
			}
			Archon.lead_needed_before_building += Archon.desired_lead;
			if (soldier_built) {
				Archon.soldier_urgency = 0;
			}
		}

		crowded_lab_loc = new MapLocation((shared_arr[31]>>6) % (1<<6), shared_arr[31] % (1<<6));
		lab_crowded = (shared_arr[31]>>12) > 0;
	}
	
	public static void compute() throws GameActionException {
		set(0, (turnq_num<<1)+round_parity+(x_symmetry_possible? 0 : 1<<13)+(y_symmetry_possible? 0 : 1<<14)+(xy_symmetry_possible? 0 : 1<<15));
		
		set(1+5*round_parity, enemy_distribution_size);
		if (Info.n_enemy_attackers > 0 && attacker_seen_before && Info.type != RobotType.LABORATORY) {
			for (int i=4; --i>=0;) {
				if (Info.rng.nextDouble()*enemy_distribution_size < Info.n_enemy_attackers) {
					MapLocation send_loc = Info.get_enemy_attacker(Info.rng.nextInt(Info.n_enemy_attackers)).location;
					set(2+i+5*round_parity, (send_loc.x<<6) + send_loc.y + (1<<12) + (1<<13));
//					rc.setIndicatorLine(Info.loc, send_loc, 255, 0, 0);
				}
			}
		}
		if (Info.enemy_robots.length > 0 && !attacker_seen_before && Info.type != RobotType.LABORATORY) {
			for (int i=4; --i>=0;) {
				if (Info.rng.nextDouble()*enemy_distribution_size < Info.enemy_robots.length) {
					MapLocation send_loc = Info.enemy_robots[Info.rng.nextInt(Info.enemy_robots.length)].location;
					set(2+i+5*round_parity, (send_loc.x<<6) + send_loc.y + (1<<12));
//					rc.setIndicatorLine(Info.loc, send_loc, 255, 0, 0);
				}
			}
		}
		
		if (Info.type == RobotType.ARCHON) {
			set(11+archon_number, (Info.x << 6) + Info.y);
			set(21+archon_number, Info.round_num + (rc.getMode()==RobotMode.PORTABLE? (1<<11) : 0));
			Archon.decide_what_to_build_next_turn();
			int build_type = (Archon.desired_robot==RobotType.BUILDER)? 0 : 
				((Archon.desired_robot==RobotType.MINER)? 1 : 2);
			set(15+archon_number, Math.min(Archon.communicated_urgency, (1<<13)-1) + (Archon.release_reserved_lead? (1<<13) : 0) + (build_type<<14));
			set(25+archon_number, Archon.health_to_heal);
			set(32+archon_number, (enemy_seen_before? Math.min(255, Math.max(0, (int)closest_enemy_dist)) : 0) + (Math.min(255, Math.max(0, Archon.total_lead/5))<<8));
		}
		if (Info.type == RobotType.BUILDER) {
			set(30, ((shared_arr[30]>>1)<<1) + 1);  // builder presence flag + lab counter
			set(19, shared_arr[19]/256*256 + Math.min(255, shared_arr[19]%256 + 1));  // builder counter
		}
		if (Info.type == RobotType.LABORATORY) {
			set(30, shared_arr[30] + 2);  // builder presence flag + lab counter
			if (Info.friendly_robots.length > 2) {
				set(31, Math.max(shared_arr[31], (Math.min(15, Math.max(0, Info.friendly_robots.length-1))<<12) + (Info.x << 6) + Info.y));  // lab crowdedness & coords
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
