package fury_fix_20;

import battlecode.common.*;

public class Soldier {
	public static RobotController rc;

	public static boolean low_hp = false;
	public static boolean healing = false;
	public static boolean seen_friendly_soldier_before = false;
	public static MapLocation healing_loc = null;
	public static boolean low_hp_overwhelm = false;
	public static MapLocation explore_loc = null;
	public static int healing_archon_index = 0;
	public static boolean find_suicide_spot = false;
	public static MapLocation estimated_enemy_archon_starting_loc = null;
	
	public static void update() throws GameActionException {
		seen_friendly_soldier_before = seen_friendly_soldier_before || Info.n_friendly_soldiers > 0;
	}
	
	public static void post_comms_update() throws GameActionException {
		
		if (Info.round_num >= 3 && estimated_enemy_archon_starting_loc == null) {
			double closest_possible_enemy_starting_archon_dist2 = Integer.MAX_VALUE;
			for (MapLocation loc:Comms.possible_enemy_archon_locs) {
				if (closest_possible_enemy_starting_archon_dist2 > Info.loc.distanceSquaredTo(loc)) {
					estimated_enemy_archon_starting_loc = loc;
					closest_possible_enemy_starting_archon_dist2 = Info.loc.distanceSquaredTo(loc);
				}
			}
		}
		
		double best_delay = Double.MAX_VALUE;
		healing_loc = null;
		for (int i=Comms.archon_positions.length; --i>=0;) {
			if (Comms.archons_alive[i]) {
				double travel_delay_1 = (Math.sqrt(Info.loc.distanceSquaredTo(Comms.archon_positions[i]))-Math.sqrt(RobotType.ARCHON.actionRadiusSquared))*RobotType.SOLDIER.movementCooldown/10.*(1+Info.rubble_quantile/10.);
				double travel_delay_2 = (Math.sqrt(Comms.closest_attacker_loc.distanceSquaredTo(Comms.archon_positions[i]))-Math.sqrt(RobotType.ARCHON.actionRadiusSquared))*RobotType.SOLDIER.movementCooldown/10.*(1+Info.rubble_quantile/10.);
				double expected_healing_delay = travel_delay_1 + travel_delay_2 + Comms.archon_healing_health[i]/2.;
				low_hp_overwhelm = low_hp_overwhelm || Comms.archon_healing_health[i]/RobotType.SOLDIER.health > 8 && healing && !Comms.archons_portable[i];
				if (expected_healing_delay < best_delay) {
					best_delay = expected_healing_delay;
					healing_loc = Comms.archon_positions[i];
					healing_archon_index = i;
				}
			}
		}
		if (healing_loc==null) {
			for (int i=Comms.archon_positions.length; --i>=0;) {
				double travel_delay_1 = (Math.sqrt(Info.loc.distanceSquaredTo(Comms.archon_positions[i]))-Math.sqrt(RobotType.ARCHON.actionRadiusSquared))*RobotType.SOLDIER.movementCooldown/10.*(1+Info.rubble_quantile/10.);
				double travel_delay_2 = (Math.sqrt(Comms.closest_attacker_loc.distanceSquaredTo(Comms.archon_positions[i]))-Math.sqrt(RobotType.ARCHON.actionRadiusSquared))*RobotType.SOLDIER.movementCooldown/10.*(1+Info.rubble_quantile/10.);
				double expected_healing_delay = travel_delay_1 + travel_delay_2 + Comms.archon_healing_health[i]/2.;
				low_hp_overwhelm = low_hp_overwhelm || Comms.archon_healing_health[i]/RobotType.SOLDIER.health > 8 && healing && !Comms.archons_portable[i];
				if (expected_healing_delay < best_delay) {
					best_delay = expected_healing_delay;
					healing_loc = Comms.archon_positions[i];
					healing_archon_index = i;
				}
			}
		}
//		if (Info.n_enemy_attackers <= 2) {
//			MapLocation[] retreat_path = new MapLocation[12];  // retreat_path[retreat_path_start:12] give the path from Info.loc inclusive to healing_loc inclusive
//			retreat_path[11] = healing_loc;
//			double retreat_info = Bfs.query(healing_loc, 100, 10, true);
//			MapLocation prev_tile = null;
//			int retreat_path_start = 0;
//			for (int i=11; --i>=0;) {
//				prev_tile = Bfs.end_location(retreat_info);
//				retreat_path_start = i;
//				retreat_path[i] = prev_tile;
//				if (prev_tile.equals(Info.loc)) {
//					break;
//				}
//				retreat_info = Bfs.query(prev_tile, 100, 10, true);
//			}
//			double damage_received_upon_retreat = 0;
//			for (int r=Info.n_enemy_attackers; --r>=0;) {
//				RobotInfo robot = Info.get_enemy_attacker(r);
//				double attack_rate = 1/(1+rc.senseRubble(robot.location)/10.) / (robot.type.actionCooldown/10.);
//				int n_retreat_tiles_visible = 0;
//				for (int i=retreat_path_start; i<12; i++) {
//					n_retreat_tiles_visible += retreat_path[i].isWithinDistanceSquared(robot.location, robot.type.actionRadiusSquared)? 1 : 0;
//				}
//				damage_received_upon_retreat += n_retreat_tiles_visible*attack_rate*robot.type.damage;
//			}
//			low_hp = Info.health - damage_received_upon_retreat <= 8;
//		}
//		else {
		low_hp = Info.health <= (healing_loc.isWithinDistanceSquared(Info.loc, 144)? 17 : 5) + 3*(1+Info.rubble/10.)*Math.max(1, Info.n_enemy_attackers)*Math.max(1, (Math.sqrt(RobotType.SOLDIER.actionRadiusSquared)-Comms.closest_attacker_dist));
//		low_hp = Info.health <= 45 && Info.health > 20;
//		}
//		low_hp = Info.health <= 5 + 3*(1+Info.rubble/10.)*Math.max(1, Info.n_enemy_attackers)*Math.max(1, (Math.sqrt(RobotType.SOLDIER.actionRadiusSquared)-Comms.closest_attacker_dist));
//		if (Info.n_friendly_soldiers < 10) {
//			boolean weakest = false;
//			for (int i=Info.n_friendly_soldiers; --i>=0;) {
//				if (Info.health >= Info.friendly_soldiers[i].health) {
//					weakest = false;
//				}
//			}
//		}
//		low_hp = low_hp || Info.loc.isWithinDistanceSquared(healing_loc, (int)Math.pow(Math.sqrt(Info.VISION_DIST2) + Math.sqrt(RobotType.ARCHON.actionRadiusSquared)-1, 2)) && best_delay_transit_time <= best_delay_waiting_time && Info.health < RobotType.SOLDIER.health;
		if (low_hp && !low_hp_overwhelm || healing) {
			rc.setIndicatorDot(Info.loc, 255, 0, 0);
			healing = Info.health != RobotType.SOLDIER.health && !low_hp_overwhelm;
		}
		if (healing) {
			rc.setIndicatorLine(Info.loc, healing_loc, 255, 0, 0);
		}
	}
	
	public static void try_to_shoot(boolean delay_shot)  throws GameActionException {
		if (!Info.action_ready) {
			return;
		}
//		if (rc.senseNearbyRobots(Info.ACTION_DIST2, Info.enemy).length == 0) {
//			return;
//		}
		RobotInfo weakest_robot = null;
		int weakest_health = Integer.MAX_VALUE;
		for (int i=Info.n_enemy_sages; --i>=0;) {
			if (Info.enemy_sages[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_sages[i].location, Info.ACTION_DIST2)) {
				weakest_robot = Info.enemy_sages[i];
				weakest_health = Info.enemy_sages[i].health;
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_soldiers; --i>=0;) {
				if (Info.enemy_soldiers[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_soldiers[i];
					weakest_health = Info.enemy_soldiers[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_watchtowers; --i>=0;) {
				if (Info.enemy_watchtowers[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_watchtowers[i];
					weakest_health = Info.enemy_watchtowers[i].health;
				}
			}
		}
		if (weakest_robot == null && delay_shot && (!healing || Info.n_enemy_attackers > 0)) {
//		if (weakest_robot == null && Info.move_ready && (!healing || Info.n_enemy_attackers > 0)) {
			return;
		}
//		else if (Micro.outnumbered && !healing) {
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_miners; --i>=0;) {
				if (Info.enemy_miners[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_miners[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_miners[i];
					weakest_health = Info.enemy_miners[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_archons; --i>=0;) {
				if (Info.enemy_archons[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_archons[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_archons[i];
					weakest_health = Info.enemy_archons[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_laboratories; --i>=0;) {
				if (Info.enemy_laboratories[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_laboratories[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_laboratories[i];
					weakest_health = Info.enemy_laboratories[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_builders; --i>=0;) {
				if (Info.enemy_builders[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_builders[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_builders[i];
					weakest_health = Info.enemy_builders[i].health;
				}
			}
		}
		if (weakest_robot != null) {
			Action.attack(weakest_robot.location);
		}
		else {
//			// This is commented out because it doesn't get used often but takes bytecode, sometimes causing out of bytecode events
//			RobotInfo[] shootable_robots = rc.senseNearbyRobots(Info.ACTION_DIST2, Info.enemy);
//			if (shootable_robots.length > 0) {
//				RobotInfo best_robot = null;
//				int best_health = Integer.MAX_VALUE;
//				for (RobotInfo robot:shootable_robots) {
//					int robot_health = 0;
//					switch (robot.type) {
//					case ARCHON: robot_health = robot.health + 40000; break;
//					case BUILDER: robot_health = robot.health + 60000; break;
//					case LABORATORY: robot_health = robot.health + 50000; break;
//					case MINER: robot_health = robot.health + 30000; break;
//					case SAGE: robot_health = robot.health + 20000; break;
//					case SOLDIER: robot_health = robot.health; break;
//					case WATCHTOWER: robot_health = robot.health + 10000; break;
//					default: break;
//					}
//					if (robot_health < best_health) {
//						best_robot = robot;
//						best_health = robot_health;
//					}
//				}
//				Action.attack(best_robot.location);
//			}
		}
	}
	
	public static void sage_assist_shot(boolean delay_shot)  throws GameActionException {
		if (!Info.action_ready) {
			return;
		}
		RobotInfo weakest_robot = null;
		int weakest_pseudohealth = Integer.MAX_VALUE;
		for (int i=Info.n_enemy_sages; --i>=0;) {
			int pseudohealth = (Info.enemy_sages[i].health-1) % 45 * 45 + (Info.enemy_sages[i].health-1) / 45;
			if (pseudohealth < weakest_pseudohealth && Info.loc.isWithinDistanceSquared(Info.enemy_sages[i].location, Info.ACTION_DIST2)) {
				weakest_robot = Info.enemy_sages[i];
				weakest_pseudohealth = pseudohealth;
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_soldiers; --i>=0;) {
				int pseudohealth = (Info.enemy_soldiers[i].health-1) % 45 * 45 + (Info.enemy_soldiers[i].health-1) / 45;
				if (pseudohealth < weakest_pseudohealth && Info.loc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_soldiers[i];
					weakest_pseudohealth = pseudohealth;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_watchtowers; --i>=0;) {
				int pseudohealth = (Info.enemy_watchtowers[i].health-1) % 45 * 45 + (Info.enemy_watchtowers[i].health-1) / 45;
				if (pseudohealth < weakest_pseudohealth && Info.loc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_watchtowers[i];
					weakest_pseudohealth = pseudohealth;
				}
			}
		}
		if (weakest_robot == null && delay_shot && (!healing || Info.n_enemy_attackers > 0)) {
//		if (weakest_robot == null && Info.move_ready && (!healing || Info.n_enemy_attackers > 0)) {
			return;
		}
//		else if (Micro.outnumbered && !healing) {
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_miners; --i>=0;) {
				int pseudohealth = (Info.enemy_miners[i].health-1) % 45 * 45 + (Info.enemy_miners[i].health-1) / 45;
				if (pseudohealth < weakest_pseudohealth && Info.loc.isWithinDistanceSquared(Info.enemy_miners[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_miners[i];
					weakest_pseudohealth = pseudohealth;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_archons; --i>=0;) {
				int pseudohealth = (Info.enemy_archons[i].health-1) % 45 * 45 + (Info.enemy_archons[i].health-1) / 45;
				if (pseudohealth < weakest_pseudohealth && Info.loc.isWithinDistanceSquared(Info.enemy_archons[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_archons[i];
					weakest_pseudohealth = pseudohealth;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_laboratories; --i>=0;) {
				int pseudohealth = (Info.enemy_laboratories[i].health-1) % 45 * 45 + (Info.enemy_laboratories[i].health-1) / 45;
				if (pseudohealth < weakest_pseudohealth && Info.loc.isWithinDistanceSquared(Info.enemy_laboratories[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_laboratories[i];
					weakest_pseudohealth = pseudohealth;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_builders; --i>=0;) {
				int pseudohealth = (Info.enemy_laboratories[i].health-1) % 45 * 45 + (Info.enemy_laboratories[i].health-1) / 45;
				if (pseudohealth < weakest_pseudohealth && Info.loc.isWithinDistanceSquared(Info.enemy_builders[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_builders[i];
					weakest_pseudohealth = pseudohealth;
				}
			}
		}
		if (weakest_robot != null) {
			Action.attack(weakest_robot.location);
		}
	}
	
	public static void try_to_move() throws GameActionException {
		if (healing || find_suicide_spot) {
//			if (Info.n_enemy_attackers > 0) {
//				Exploration.repel(Info.enemy_soldiers, Info.n_enemy_soldiers, 1);
//				Exploration.repel(Info.enemy_watchtowers, Info.n_enemy_watchtowers, 1);
//				Exploration.repel(Info.enemy_sages, Info.n_enemy_sages, 1);
//				Exploration.travel();
//				return;
//			}
			if (!Info.loc.isWithinDistanceSquared(healing_loc, 10) && !find_suicide_spot) {
//				Direction dir = Bfs.way_to(healing_loc, 100, true);
				Direction dir = Bfs.direction(Bfs.query(healing_loc, 100, 2000, true));
				Action.move(dir);
				return;
			}
			else if (find_suicide_spot && rc.senseLead(Info.loc) == 0) {
				rc.disintegrate();
			}
			else if (!Info.loc.isWithinDistanceSquared(healing_loc, 10) && find_suicide_spot) {
				Direction dir = Direction.CENTER;
				int n_dirs = 0;
				for (Direction testdir:Math2.UNIT_DIRECTIONS) {
					if (rc.canMove(testdir)) {
						n_dirs++;
						if (Info.rng.nextInt(n_dirs) == 0) {
							dir = testdir;
						}
					}
				}
				Action.move(dir);
				return;
			}
			else {
//				if (Comms.archon_healing_health[healing_archon_index] > 1*RobotType.SOLDIER.health + Info.health*3) {
//					if (Info.health > 20) {
//						healing = false;
//						find_suicide_spot = false;
//					}
//					else {
						find_suicide_spot = true;
						MapLocation target_loc = healing_loc;
						if (Comms.closest_attacker_loc!=null) {
							target_loc = Info.loc.translate(healing_loc.x - Comms.closest_attacker_loc.x, healing_loc.y - Comms.closest_attacker_loc.y);
						}
						Action.move(Bfs.direction(Bfs.query(target_loc, 100, 2000, false)));
						return;
//					}
//				}
//				else {
//					find_suicide_spot = false;
//				}
			}
		}
		RobotInfo closest_robot = null;
		int closest_dist2 = Integer.MAX_VALUE;
		for (int i=Info.n_enemy_sages; --i>=0;) {
			if (Info.loc.isWithinDistanceSquared(Info.enemy_sages[i].location, closest_dist2-1)) {
				closest_robot = Info.enemy_sages[i];
				closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
			}
		}
		if (closest_robot==null) {
			for (int i=Info.n_enemy_soldiers; --i>=0;) {
				if (Info.loc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, closest_dist2-1)) {
					closest_robot = Info.enemy_soldiers[i];
					closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
				}
			}
		}
		if (closest_robot==null) {
			for (int i=Info.n_enemy_watchtowers; --i>=0;) {
				if (Info.loc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, closest_dist2-1)) {
					closest_robot = Info.enemy_watchtowers[i];
					closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
				}
			}
		}
		if (closest_robot==null) {
			for (int i=Info.n_enemy_miners; --i>=0;) {
				if (Info.loc.isWithinDistanceSquared(Info.enemy_miners[i].location, closest_dist2-1)) {
					closest_robot = Info.enemy_miners[i];
					closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
				}
			}
		}
		if (closest_robot==null) {
			for (int i=Info.n_enemy_archons; --i>=0;) {
				if (Info.loc.isWithinDistanceSquared(Info.enemy_archons[i].location, closest_dist2-1)) {
					closest_robot = Info.enemy_archons[i];
					closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
				}
			}
		}
		if (closest_robot==null) {
			for (int i=Info.n_enemy_laboratories; --i>=0;) {
				if (Info.loc.isWithinDistanceSquared(Info.enemy_laboratories[i].location, closest_dist2-1)) {
					closest_robot = Info.enemy_laboratories[i];
					closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
				}
			}
		}
		if (closest_robot==null) {
			for (int i=Info.n_enemy_builders; --i>=0;) {
				if (Info.loc.isWithinDistanceSquared(Info.enemy_builders[i].location, closest_dist2-1)) {
					closest_robot = Info.enemy_builders[i];
					closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
				}
			}
		}
		if (!Info.move_ready) {
			return;
		}
		else if ((Micro.chase_enemy_econ_units && Comms.closest_attacker_dist > 7 && Info.n_enemy_attackers==0 || Info.n_enemy_archons>0 && Info.n_friendly_attackers > Info.n_enemy_attackers+1) && closest_robot != null || Micro.winning_1v1_soldier) {
			rc.setIndicatorDot(Info.loc, 255, 128, 0);
//			boolean[][] illegal_tiles = Micro.illegal_tiles();
////			Pathing.target(closest_robot.location, illegal_tiles, 1);
//			Pathing.approach(closest_robot.location, illegal_tiles);
//			Action.move(Bfs.way_to(closest_robot.location, 100, true));
			Action.move(Bfs.direction(Bfs.query(closest_robot.location, 100, 2000, true)));
			return;
		}
		else if (Micro.outnumbered && !healing) {
			rc.setIndicatorDot(Info.loc, 0, 255, 0);
//			Exploration.momentum_dx = -0.01 * Comms.closest_attacker_dx/Comms.closest_attacker_dist;
//			Exploration.momentum_dy = -0.01 * Comms.closest_attacker_dy/Comms.closest_attacker_dist;
//			Exploration.repel(Info.enemy_soldiers, Info.n_enemy_soldiers, 1);
////			Exploration.repel(Info.enemy_archons, Info.n_enemy_archons, 1);
////			Exploration.repel(Info.enemy_watchtowers, Info.n_enemy_watchtowers, 1);
////			Exploration.repel(Info.enemy_sages, Info.n_enemy_sages, 1);
//			Exploration.travel();
//			return;
			MapLocation target_loc = healing_loc;
//			if (Info.n_enemy_attackers > 0) {
//				Exploration.repel(Info.enemy_soldiers, Info.n_enemy_soldiers, 1);
//				Exploration.repel(Info.enemy_watchtowers, Info.n_enemy_watchtowers, 1);
//				Exploration.repel(Info.enemy_sages, Info.n_enemy_sages, 1);
//				double force_r = Math.hypot(Exploration.force_dx, Exploration.force_dy);
//				target_loc = Info.loc.translate((int)(Exploration.force_dx/force_r*1000), (int)(Exploration.force_dy/force_r*1000));
//			}
//			Direction dir = Bfs.way_to(target_loc, 100, true);
			Direction dir = Bfs.direction(Bfs.query(target_loc, 100, 2000, true));
			Action.move(dir);
			return;
		}
		else if (closest_robot != null && (!healing || Info.n_enemy_attackers > 0 && Info.loc.isWithinDistanceSquared(healing_loc, RobotType.ARCHON.actionRadiusSquared))) {
			rc.setIndicatorDot(Info.loc, 255, 255, 0);
			boolean[][] illegal_tiles = Micro.illegal_tiles();
			int n_illegal_tiles = ((illegal_tiles[0][0]? 1 : 0) + 
								   (illegal_tiles[0][1]? 1 : 0) + 
								   (illegal_tiles[0][2]? 1 : 0) + 
								   (illegal_tiles[1][0]? 1 : 0) + 
								   (illegal_tiles[1][1]? 1 : 0) + 
								   (illegal_tiles[1][2]? 1 : 0) + 
								   (illegal_tiles[2][0]? 1 : 0) + 
								   (illegal_tiles[2][1]? 1 : 0) + 
								   (illegal_tiles[2][2]? 1 : 0));
			if (n_illegal_tiles == 8) {
				if (!illegal_tiles[0][0]) {Action.move(Direction.SOUTHWEST); return;}
				else if (!illegal_tiles[0][1]) {Action.move(Direction.WEST); return;}
				else if (!illegal_tiles[0][2]) {Action.move(Direction.NORTHWEST); return;}
				else if (!illegal_tiles[1][0]) {Action.move(Direction.SOUTH); return;}
				else if (!illegal_tiles[1][2]) {Action.move(Direction.NORTH); return;}
				else if (!illegal_tiles[2][0]) {Action.move(Direction.SOUTHEAST); return;}
				else if (!illegal_tiles[2][1]) {Action.move(Direction.EAST); return;}
				else if (!illegal_tiles[2][2]) {Action.move(Direction.NORTHEAST); return;}
			}
			else {
//				Pathing.target(closest_robot.location, illegal_tiles, 1);
				Pathing.approach(closest_robot.location, illegal_tiles);
			}
			return;
		}
		else if (Comms.attacker_seen_before && Comms.closest_attacker_dist < 60*Math.sqrt(2) && !healing){
			rc.setIndicatorDot(Info.loc, 0, 255, 255);
//			Action.move(Bfs.way_to(Comms.closest_attacker_loc, Info.average_rubble, Clock.getBytecodesLeft()>2500));
			Action.move(Bfs.direction(Bfs.query(Comms.closest_attacker_loc, Info.average_rubble, (Info.round_num-Info.last_move_turn)/10.+1, true)));
//			boolean[][] illegal_tiles = new boolean[3][3];
//			Pathing.approach(Comms.closest_attacker_loc, illegal_tiles);
//			Pathing.target(Comms.closest_attacker_loc, illegal_tiles, 1);
//			rc.setIndicatorLine(Info.loc, Comms.closest_enemy_loc, 0, 255, 0);
			return;
		}
		else if (Comms.enemy_seen_before && Comms.closest_enemy_dist < 60*Math.sqrt(2) && !healing){
			rc.setIndicatorDot(Info.loc, 0, 0, 255);
//			rc.setIndicatorLine(Info.loc, Comms.closest_enemy_loc, 0, 0, 255);
//			Action.move(Bfs.way_to(Comms.closest_enemy_loc, Info.average_rubble, Clock.getBytecodesLeft()>2500));
			Action.move(Bfs.direction(Bfs.query(Comms.closest_enemy_loc, Info.average_rubble, (Info.round_num-Info.last_move_turn)/10.+1, true)));
//			boolean[][] illegal_tiles = new boolean[3][3];
//			Pathing.approach(Comms.closest_enemy_loc, illegal_tiles);
//			rc.setIndicatorLine(Info.loc, Comms.closest_enemy_loc, 0, 255, 0);
			return;
		}
//		else if (!healing && seen_friendly_soldier_before) {
//			rc.setIndicatorDot(Info.loc, 255, 0, 255);
//			Exploration.repel(Info.friendly_soldiers, Info.n_friendly_soldiers, 1);
//			Exploration.travel();
//			return;
//		}
		else if (!healing) {
			MapLocation target_loc = new MapLocation(Info.MAP_WIDTH/2, Info.MAP_HEIGHT/2);
			if (estimated_enemy_archon_starting_loc!=null) {
				MapLocation archon_approx_loc = estimated_enemy_archon_starting_loc;
				int dx = archon_approx_loc.x-Info.spawn_loc.x;
				int dy = archon_approx_loc.y-Info.spawn_loc.y;
				MapLocation left_intersection = Math2.extend_to_wall(archon_approx_loc, archon_approx_loc.translate(-dy, dx), Info.MAP_WIDTH, Info.MAP_HEIGHT);
				MapLocation right_intersection = Math2.extend_to_wall(archon_approx_loc, archon_approx_loc.translate(dy, -dx), Info.MAP_WIDTH, Info.MAP_HEIGHT);
				boolean closer_to_left = left_intersection.isWithinDistanceSquared(archon_approx_loc, archon_approx_loc.distanceSquaredTo(right_intersection));
				target_loc = new MapLocation(left_intersection.x + ((Comms.first_soldier^closer_to_left)? 2 : 1) * (right_intersection.x - left_intersection.x)/3, left_intersection.y + ((Comms.first_soldier^closer_to_left)? 2 : 1) * (right_intersection.y - left_intersection.y)/3);
				target_loc = Math2.extend_to_wall(Info.spawn_loc, target_loc, Info.MAP_WIDTH, Info.MAP_HEIGHT);
			}
			rc.setIndicatorLine(Info.loc, target_loc, 0, 0, 0);
			Action.move(Bfs.direction(Bfs.query(target_loc, 100, (Info.round_num-Info.last_move_turn)/10.+1, true)));
			return;

//			if (explore_loc==null || Info.loc.isWithinDistanceSquared(explore_loc, 5)) {
//				explore_loc = new MapLocation(Info.rng.nextInt(Info.MAP_WIDTH-1), Info.rng.nextInt(Info.MAP_HEIGHT-1));
//			}
//			Action.move(Bfs.direction(Bfs.query(explore_loc, 100, 2000, true)));
//			return;
			
		}
		return;
	}
	
	public static void act() throws GameActionException {
//		try_to_shoot(false);
		if (Info.n_friendly_sages > Info.n_friendly_soldiers * 3) {
			sage_assist_shot(Info.move_ready);
		}
		else {
			try_to_shoot(Info.move_ready);
		}
		
		if (Info.move_ready) {
			try_to_move();
//			try_to_shoot(false);
			if (Info.n_friendly_sages > Info.n_friendly_soldiers * 3) {
				sage_assist_shot(false);
			}
			else {
				try_to_shoot(false);
			}
		}
	}
}
