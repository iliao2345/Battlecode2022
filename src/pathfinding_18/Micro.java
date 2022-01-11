package pathfinding_18;

import battlecode.common.*;

public class Micro {
	public static RobotController rc;
	public static final int SPARSE_THRESHOLD = 5;
	public static final double RUBBLE_SAMPLE_DECAY = 0.7;
	public static final double ENEMY_SAMPLE_DECAY = 0.7;
	public static final double NO_MANS_LAND_THICKNESS = 3;

//	public static boolean dense_mode = false;
//	public static double battle_ratio = 1;
//	public static double average_friendly_shot_rate = 1;
//	public static double average_friendly_count = 1;
	public static double average_enemy_shot_rate = 1;
	public static double average_enemy_count = 1;
//	public static double rubble_sample_size = 0;
//	public static double rubble_sample_logrubble = 0;
//	public static double rubble_sample_x = 0;
//	public static double rubble_sample_y = 0;
//	public static double rubble_sample_xx = 0;
//	public static double rubble_sample_yy = 0;
//	public static double rubble_sample_xlogrubble = 0;
//	public static double rubble_sample_ylogrubble = 0;
//	public static double rubble_gradient_dx = 0;
//	public static double rubble_gradient_dy = 0;
//	public static double enemy_sample_size = 0;
//	public static double enemy_sample_x = 0;
//	public static double enemy_sample_y = 0;
//	public static double enemy_x = 0;
//	public static double enemy_y = 0;

	public static void update() throws GameActionException {
//		dense_mode = Info.friendly_robots.length + Info.enemy_robots.length > SPARSE_THRESHOLD;
//		int this_is_attacker = (Info.type==RobotType.SOLDIER || Info.type==RobotType.WATCHTOWER || Info.type==RobotType.SAGE)? 1 : 0;
//		average_friendly_count += 0.5 * (Info.n_friendly_attackers+this_is_attacker - average_friendly_count);
		average_enemy_count += 0.5 * (Info.n_enemy_attackers - average_enemy_count);
//		double friendly_rubble_plus_10 = (Info.rng.nextInt(Info.n_friendly_attackers+1)==0)? 10+Info.rubble :
//				10+rc.senseRubble(Info.get_friendly_attacker(Info.rng.nextInt(Info.n_friendly_attackers)).location);
//		average_friendly_shot_rate += 1/Math.sqrt(9) * (1/friendly_rubble_plus_10 - average_friendly_shot_rate);
		if (Info.n_enemy_attackers > 0) {
			double enemy_rubble_plus_10 = 10+rc.senseRubble(Info.get_enemy_attacker(Info.rng.nextInt(Info.n_enemy_attackers)).location);
			average_enemy_shot_rate += 1/Math.sqrt(9) * (1/enemy_rubble_plus_10 - average_enemy_shot_rate);
		}
		else {
			double enemy_rubble_plus_10 = 10+rc.senseRubble(Info.loc);
			average_enemy_shot_rate += 1/Math.sqrt(9) * (1/enemy_rubble_plus_10 - average_enemy_shot_rate);
		}
		
//		rubble_sample_size *= RUBBLE_SAMPLE_DECAY;
//		rubble_sample_logrubble *= RUBBLE_SAMPLE_DECAY;
//		rubble_sample_x *= RUBBLE_SAMPLE_DECAY;
//		rubble_sample_y *= RUBBLE_SAMPLE_DECAY;
//		rubble_sample_xx *= RUBBLE_SAMPLE_DECAY;
//		rubble_sample_yy *= RUBBLE_SAMPLE_DECAY;
//		rubble_sample_xlogrubble *= RUBBLE_SAMPLE_DECAY;
//		rubble_sample_ylogrubble *= RUBBLE_SAMPLE_DECAY;
//		for (int i=4; --i>=0;) {
//			MapLocation tile = Info.visible_tiles[Info.rng.nextInt(Info.visible_tiles.length)];
//			double logrubble = Math.log(10+rc.senseRubble(tile)/10);
//			rubble_sample_logrubble += logrubble;
//			rubble_sample_x += tile.x;
//			rubble_sample_y += tile.y;
//			rubble_sample_xx += tile.x*tile.x;
//			rubble_sample_yy += tile.y*tile.y;
//			rubble_sample_xlogrubble += tile.x*logrubble;
//			rubble_sample_ylogrubble += tile.y*logrubble;
//		}
//		rubble_sample_size += 4;
//		rubble_gradient_dx = (rubble_sample_size*rubble_sample_xlogrubble-rubble_sample_logrubble*rubble_sample_x) / (rubble_sample_size*rubble_sample_xx-rubble_sample_x*rubble_sample_x);
//		rubble_gradient_dy = (rubble_sample_size*rubble_sample_ylogrubble-rubble_sample_logrubble*rubble_sample_y) / (rubble_sample_size*rubble_sample_yy-rubble_sample_y*rubble_sample_y);
//
//		enemy_sample_size *= RUBBLE_SAMPLE_DECAY;
//		enemy_sample_x *= ENEMY_SAMPLE_DECAY;
//		enemy_sample_y *= ENEMY_SAMPLE_DECAY;
//		if (Info.n_enemy_attackers > 0) {
//			for (int i=2; --i>=0;) {
//				RobotInfo attacker = Info.get_enemy_attacker(Info.rng.nextInt(Info.n_enemy_attackers));
//				enemy_sample_x += attacker.location.x;
//				enemy_sample_y += attacker.location.y;
//			}
//			enemy_sample_size += 2;
//			enemy_x = enemy_sample_x / enemy_sample_size;
//			enemy_y = enemy_sample_y / enemy_sample_size;
//		}
////		double enemy_r = (enemy_x-Info.x)*(enemy_x-Info.x)-(enemy_y-Info.y)*(enemy_y-Info.y);
////		double enemy_direction_dx = (enemy_x-Info.x) / enemy_r;
////		double enemy_direction_dy = (enemy_y-Info.y) / enemy_r;
//
//		double log_shot_rate_diff = (enemy_x-Info.x)*rubble_gradient_dx + (enemy_y-Info.y)*rubble_gradient_dy;
//		double battle_ratio = 1;
//		if (average_enemy_count > 1) {
//			battle_ratio = 0.4*((double)average_friendly_count)/(average_enemy_count) * Math.exp(log_shot_rate_diff);
//		}
//		if (battle_ratio < 0.8) {
//			rc.setIndicatorDot(Info.loc, 255, 0, 0);
//		}
//		if (battle_ratio >= 0.8 && battle_ratio < 1.2) {
//			rc.setIndicatorDot(Info.loc, 255, 255, 0);
//		}
//		if (battle_ratio >= 1.2) {
//			rc.setIndicatorDot(Info.loc, 0, 255, 0);
//		}
	}
	
	public static boolean[][] illegal_tiles() throws GameActionException {
//		if (Info.n_enemy_soldiers+Info.n_enemy_watchtowers+Info.n_enemy_sages > 0 && Info.type == RobotType.SOLDIER) {
		if (Info.type != RobotType.SOLDIER) {
			return new boolean[3][3];
		}
		if (Info.enemy_robots.length > 0) {
			boolean[][] illegal_tiles = new boolean[3][3];
			double[][] values;
//			if (Info.n_enemy_attackers > 3) {
				values = battle_tile_values_sparse();
//			}
//			else {
//				values = battle_tile_values_sparser();
//			}
			double min_value = Double.MAX_VALUE;
			for (int dx=2; --dx>=-1;) {
				for (int dy=2; --dy>=-1;) {
					min_value = Math.min(min_value, values[dx+1][dy+1]);
				}
			}
//			double illegal_threshold = 1;
			double illegal_threshold = min_value;
			for (int dx=2; --dx>=-1;) {
				for (int dy=2; --dy>=-1;) {
					illegal_tiles[dx+1][dy+1] = values[dx+1][dy+1] > illegal_threshold;
				}
			}
			return illegal_tiles;
		}
		else {
			return new boolean[3][3];
		}
	}
	
//	public static double[][] battle_tile_values_dense() throws GameActionException {
//		
//		double[][] values = new double[3][3];
//		for (Direction dir:Direction.allDirections()) {
//			MapLocation testloc = Info.loc.add(dir);
//			if (rc.onTheMap(testloc)) {
//				values[dir.dx+1][dir.dy+1] = rubble_gradient_dx*dir.dx + rubble_gradient_dy*dir.dy;
//				if (battle_ratio > 1 || average_friendly_count > 4*average_enemy_count) {
//					values[dir.dx+1][dir.dy+1] = -(enemy_x-Info.x)*dir.dx - (enemy_y-Info.y)*dir.dy;
//				}
//			}
//			else {
//				values[dir.dx+1][dir.dy+1] = Double.MAX_VALUE;
//			}
//		}
//
//		return values;
//	}
	
	public static double[][] battle_tile_values_sparse() throws GameActionException {
		
		int n_enemy_shootable_now = rc.senseNearbyRobots(Info.loc, Info.ACTION_DIST2, Info.enemy).length;
//		boolean enemy_shootable_now = n_enemy_shootable_now > 0;
		boolean enemy_shootable_now = rc.getActionCooldownTurns() > 0;
		double[][] values = new double[3][3];
		for (Direction dir:Direction.allDirections()) {
			MapLocation testloc = Info.loc.add(dir);
			if (rc.onTheMap(testloc) && !rc.isLocationOccupied(testloc)) {
				int n_enemy_shootable = rc.senseNearbyRobots(testloc, Info.ACTION_DIST2, Info.enemy).length;
				int n_enemy_visible = rc.senseNearbyRobots(testloc, Info.VISION_DIST2, Info.enemy).length;
				if (enemy_shootable_now) {
					values[dir.dx+1][dir.dy+1] = +1000*n_enemy_shootable+n_enemy_visible + 1000000*(10+rc.senseRubble(testloc)/10.);
				}
				else {
					double neg_approx_distance = 0;
					if (Comms.attacker_seen_before) {
						neg_approx_distance = Comms.closest_attacker_dx/Comms.closest_attacker_dist*dir.dx + Comms.closest_attacker_dy/Comms.closest_attacker_dist*dir.dy;
					}
					else if (Comms.enemy_seen_before) {
						neg_approx_distance = Comms.closest_enemy_dx/Comms.closest_enemy_dist*dir.dx + Comms.closest_enemy_dy/Comms.closest_enemy_dist*dir.dy;
					}
					values[dir.dx+1][dir.dy+1] = ((n_enemy_shootable > 0)? n_enemy_shootable : 1000) + neg_approx_distance + 1000*(10+rc.senseRubble(testloc)/10.);
				}
			}
			else {
				values[dir.dx+1][dir.dy+1] = Double.MAX_VALUE;
			}
		}

		return values;
	}
	
	public static double[][] battle_tile_values_sparser() throws GameActionException {
		
		int n_enemy_shootable_now = rc.senseNearbyRobots(Info.loc, Info.ACTION_DIST2, Info.enemy).length;
		boolean enemy_shootable_now = n_enemy_shootable_now > 0;
		double[][] values = new double[3][3];
		RobotInfo[] enemy_attackers = new RobotInfo[Info.n_enemy_attackers];
		boolean[] attacker_occupied = new boolean[Info.n_enemy_attackers];
		int[] attacker_action_r2 = new int[Info.n_enemy_attackers];
		for (int i=Info.n_enemy_attackers; --i>=0;) {
			RobotInfo robot = Info.get_enemy_attacker(i);
			enemy_attackers[i] = robot;
			attacker_action_r2[i] = (robot.type==RobotType.WATCHTOWER)? 20 : 25; 
			attacker_occupied[i] = rc.senseNearbyRobots(robot.location, attacker_action_r2[i], Info.friendly).length > 0;
		}
		for (Direction dir:Direction.allDirections()) {
			MapLocation testloc = Info.loc.add(dir);
			if (rc.onTheMap(testloc)) {
				boolean cannot_shoot_enemy_from_testloc = true;
				for (int i=Info.n_enemy_attackers; --i>=0;) {
					if (!attacker_occupied[i]) {
						values[dir.dx+1][dir.dy+1] += Info.loc.add(dir).isWithinDistanceSquared(enemy_attackers[i].location, attacker_action_r2[i])? 100 : 0;
						values[dir.dx+1][dir.dy+1] += Info.loc.add(dir).isWithinDistanceSquared(enemy_attackers[i].location, 20)? 1 : 0;
					}
					cannot_shoot_enemy_from_testloc = cannot_shoot_enemy_from_testloc && !Info.loc.add(dir).isWithinDistanceSquared(enemy_attackers[i].location, Info.ACTION_DIST2);
				}
				values[dir.dx+1][dir.dy+1] += 1000000*(10+rc.senseRubble(testloc)/10.);
				if (!enemy_shootable_now && cannot_shoot_enemy_from_testloc) {
					values[dir.dx+1][dir.dy+1] += 10000;
				}
			}
			else {
				values[dir.dx+1][dir.dy+1] = Double.MAX_VALUE;
			}
		}

		return values;
	}
}
