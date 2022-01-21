package suicide_test_16;

import battlecode.common.*;

public class Micro {
	public static RobotController rc;
//	public static final int SPARSE_THRESHOLD = 5;
//	public static final double RUBBLE_SAMPLE_DECAY = 0.8;
//	public static final double ENEMY_SAMPLE_DECAY = 0.7;
//	public static final double NO_MANS_LAND_THICKNESS = 4;

//	public static boolean dense_mode = false;
//	public static double battle_ratio = 1;
//	public static double average_friendly_shot_rate = 1;
//	public static double average_friendly_count = 1;
//	public static double average_enemy_shot_rate = 1;
//	public static double average_enemy_count = 1;
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
//	public static double rubble_advantage = 1;
//	public static double enemy_sample_size = 0;
//	public static double enemy_sample_x = 0;
//	public static double enemy_sample_y = 0;
//	public static double enemy_x = 0;
//	public static double enemy_y = 0;
//	public static int attacker_last_seen_round = Integer.MIN_VALUE;
//	public static int rounds_since_battle = Integer.MAX_VALUE;
//	public static boolean retreat_signal = false;
	public static boolean chase_enemy_econ_units = false;
	public static boolean outnumbered = false;
	public static int retreat_timer = 0;
	public static boolean charging = false;
	public static int max_enemy_seen = 0;
	public static boolean winning_1v1_soldier = false;
	public static boolean losing_1v1_soldier = false;

	public static void update() throws GameActionException {
//		attacker_last_seen_round = Info.n_enemy_attackers>0? Info.round_num : attacker_last_seen_round;
//		rounds_since_battle = Info.round_num - attacker_last_seen_round;
//		dense_mode = Info.friendly_robots.length + Info.enemy_robots.length > SPARSE_THRESHOLD;
//		int this_is_attacker = (Info.type==RobotType.SOLDIER || Info.type==RobotType.WATCHTOWER || Info.type==RobotType.SAGE)? 1 : 0;
//		average_friendly_count += 0.5 * (Info.n_friendly_attackers+this_is_attacker - average_friendly_count);
//		average_enemy_count += 0.5 * (Info.n_enemy_attackers - average_enemy_count);
//		double friendly_rubble_plus_10 = (Info.rng.nextInt(Info.n_friendly_attackers+1)==0)? 10+Info.rubble :
//				10+rc.senseRubble(Info.get_friendly_attacker(Info.rng.nextInt(Info.n_friendly_attackers)).location);
//		average_friendly_shot_rate += 1/Math.sqrt(9) * (1/friendly_rubble_plus_10 - average_friendly_shot_rate);
//		if (Info.n_enemy_attackers > 0) {
//			double enemy_rubble_plus_10 = 10+rc.senseRubble(Info.get_enemy_attacker(Info.rng.nextInt(Info.n_enemy_attackers)).location);
//			average_enemy_shot_rate += 1/Math.sqrt(9) * (1/enemy_rubble_plus_10 - average_enemy_shot_rate);
//		}
//		else {
//			double enemy_rubble_plus_10 = 10+rc.senseRubble(Info.loc);
//			average_enemy_shot_rate += 1/Math.sqrt(9) * (1/enemy_rubble_plus_10 - average_enemy_shot_rate);
//		}
		
		chase_enemy_econ_units = Info.n_enemy_attackers == 0 && Info.n_enemy_archons == 0;
//		outnumbered = Info.n_enemy_attackers > Info.n_friendly_attackers + 1;
//		charging = Info.n_enemy_attackers*5 < Info.n_friendly_attackers;
//		max_enemy_seen = Math.max(Info.n_enemy_attackers, max_enemy_seen);
//		max_enemy_seen = (Info.n_friendly_attackers+1 >= max_enemy_seen)? 0 : max_enemy_seen;
		losing_1v1_soldier = Info.n_enemy_soldiers >= 1 && Info.n_friendly_soldiers == 0 && Info.enemy_soldiers[0].health > Info.health && Info.n_enemy_archons >= Info.n_friendly_archons;
		winning_1v1_soldier = Info.n_enemy_attackers == 1 && Info.n_enemy_soldiers == 1 && Info.enemy_soldiers[0].health < Info.health && Info.n_enemy_archons <= Info.n_friendly_archons;
		retreat_timer = (Info.n_enemy_attackers > Info.n_friendly_attackers+1 || losing_1v1_soldier)? 2 : Math.max(0, retreat_timer-1);
		outnumbered = retreat_timer > 0;
		
//		rubble_sample_size *= RUBBLE_SAMPLE_DECAY;
//		rubble_sample_logrubble *= RUBBLE_SAMPLE_DECAY;
//		rubble_sample_x *= RUBBLE_SAMPLE_DECAY;
//		rubble_sample_y *= RUBBLE_SAMPLE_DECAY;
//		rubble_sample_xx *= RUBBLE_SAMPLE_DECAY;
//		rubble_sample_yy *= RUBBLE_SAMPLE_DECAY;
//		rubble_sample_xlogrubble *= RUBBLE_SAMPLE_DECAY;
//		rubble_sample_ylogrubble *= RUBBLE_SAMPLE_DECAY;
//		for (int i=2; --i>=0;) {
////			MapLocation tile = Info.visible_tiles[Info.rng.nextInt(Info.visible_tiles.length)];
////			for (int tries=2; --tries>=0 && ((tile.x-Info.x)*Comms.closest_attacker_dx+(tile.y-Info.y)*Comms.closest_attacker_dy <= 0);) {
////				tile = Info.visible_tiles[Info.rng.nextInt(Info.visible_tiles.length)];
////			}
////			for (int tries=2; --tries>=0 && ((tile.x-Info.x)*Comms.closest_attacker_dx+(tile.y-Info.y)*Comms.closest_attacker_dy <= 0);) {
////				tile = Info.visible_tiles[Info.rng.nextInt(Info.visible_tiles.length)];
////			}
//			MapLocation tile = null;
//			if (i==0 || Info.n_enemy_attackers==0) {
//				Direction away = Math2.direction_to(Comms.closest_enemy_dx, Comms.closest_enemy_dy).opposite();
//				tile = Info.loc.add(away).add(away);
//				if (!rc.onTheMap(tile)) {
//					tile = Info.loc;
//				}
//			}
//			else {
////				tile = Info.get_enemy_attacker(Info.rng.nextInt(Info.n_enemy_attackers)).location.add(Math2.UNIT_DIRECTIONS[Info.rng.nextInt(8)]);
////				if (!rc.canSenseLocation(tile)) {
////					tile = Info.get_enemy_attacker(Info.rng.nextInt(Info.n_enemy_attackers)).location;
////				}
//				MapLocation enemy_loc = Info.get_enemy_attacker(Info.rng.nextInt(Info.n_enemy_attackers)).location;
//				tile = new MapLocation((int)(0.5*enemy_loc.x + 0.5*Info.x), (int)(0.5*enemy_loc.y + 0.5*Info.y));
//			}
//			if (Info.n_enemy_attackers > 0) {
//				rc.setIndicatorDot(tile, 0, 0, 0);
//			}
//			double logrubble = Math.log(10+rc.senseRubble(tile)/10);
//			rubble_sample_logrubble += logrubble;
//			rubble_sample_x += tile.x;
//			rubble_sample_y += tile.y;
//			rubble_sample_xx += tile.x*tile.x;
//			rubble_sample_yy += tile.y*tile.y;
//			rubble_sample_xlogrubble += tile.x*logrubble;
//			rubble_sample_ylogrubble += tile.y*logrubble;
//		}
//		rubble_sample_size += 2;
//		rubble_gradient_dx = (rubble_sample_size*rubble_sample_xlogrubble-rubble_sample_logrubble*rubble_sample_x) / (rubble_sample_size*rubble_sample_xx-rubble_sample_x*rubble_sample_x+0.01);
//		rubble_gradient_dy = (rubble_sample_size*rubble_sample_ylogrubble-rubble_sample_logrubble*rubble_sample_y) / (rubble_sample_size*rubble_sample_yy-rubble_sample_y*rubble_sample_y+0.01);
//		if (Info.n_enemy_attackers > 0 && Comms.attacker_seen_before) {
//			rubble_advantage = Math.exp(NO_MANS_LAND_THICKNESS*(rubble_gradient_dx*Comms.closest_attacker_dx+rubble_gradient_dy*Comms.closest_attacker_dy)/Comms.closest_attacker_dist);
//		}
//		else {
//			rubble_advantage = 1;
//		}
//		rc.setIndicatorString(String.valueOf(rubble_advantage));
//		retreat_signal = rc.getActionCooldownTurns() > 0 && rubble_advantage < 1.0002 && Comms.attacker_seen_before && Comms.closest_attacker_dist < 6;
////		retreat_signal = rc.senseNearbyRobots(Info.ACTION_DIST2, Info.enemy).length == 0 && rubble_advantage < 1.0002 && Comms.attacker_seen_before && Comms.closest_attacker_dist < 6;
//		if (retreat_signal) {
//			rc.setIndicatorDot(Info.loc, 255, 0, 0);
//			rc.setIndicatorLine(Info.loc, Info.loc.translate((int)(100*rubble_gradient_dx), (int)(100*rubble_gradient_dy)), 255, 0, 0);
//		}
//		else {
//			rc.setIndicatorDot(Info.loc, 0, 255, 0);
//			rc.setIndicatorLine(Info.loc, Info.loc.translate((int)(100*rubble_gradient_dx), (int)(100*rubble_gradient_dy)), 0, 255, 0);
//		}
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
			if (Info.n_enemy_attackers > 5) {
				values = battle_tile_values_sparse();
			}
			else {
				values = battle_tile_values_sparser();
			}
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
//		int n_enemy_shootable_now = rc.senseNearbyRobots(Info.loc, Info.ACTION_DIST2, Info.enemy).length;
//		boolean enemy_shootable_now = n_enemy_shootable_now > 0;
		boolean enemy_shootable_now = rc.getActionCooldownTurns() > 0;
		double[][] values = new double[3][3];
		for (Direction dir:Direction.allDirections()) {
			MapLocation testloc = Info.loc.add(dir);
			if (rc.onTheMap(testloc) && !rc.isLocationOccupied(testloc)) {
				int n_enemy_shootable = rc.senseNearbyRobots(testloc, Info.ACTION_DIST2, Info.enemy).length;
				int n_enemy_visible = rc.senseNearbyRobots(testloc, Info.VISION_DIST2, Info.enemy).length;
//				if ((enemy_shootable_now || outnumbered) && !chase_miners) {
				if (enemy_shootable_now) {
					values[dir.dx+1][dir.dy+1] = +1000*n_enemy_shootable+n_enemy_visible + 2000*(Math.floor((1+rc.senseRubble(testloc)/10.)*RobotType.SOLDIER.actionCooldown)) + (Comms.attacker_seen_before? -0.0001*(Comms.closest_attacker_dx-dir.dx)*(Comms.closest_attacker_dx-dir.dx)+(Comms.closest_attacker_dy-dir.dy)*(Comms.closest_attacker_dy-dir.dy) : 0);
				}
				else {
					double neg_approx_distance = 0;
					if (Comms.attacker_seen_before) {
						neg_approx_distance = Comms.closest_attacker_dx/Comms.closest_attacker_dist*dir.dx + Comms.closest_attacker_dy/Comms.closest_attacker_dist*dir.dy;
					}
					else if (Comms.enemy_seen_before) {
						neg_approx_distance = Comms.closest_enemy_dx/Comms.closest_enemy_dist*dir.dx + Comms.closest_enemy_dy/Comms.closest_enemy_dist*dir.dy;
					}
					values[dir.dx+1][dir.dy+1] = ((n_enemy_shootable > 0)? n_enemy_shootable : 1000) + neg_approx_distance + 1000*(1+rc.senseRubble(testloc)/10.);
				}
				if (Soldier.healing) {
					boolean within_healing_range = false;
					for (int i=Info.n_friendly_archons; --i>=0;) {
						if (Info.friendly_archons[i].location.isWithinDistanceSquared(Info.loc, RobotType.ARCHON.actionRadiusSquared)) {
							within_healing_range = true;
						}
					}
					values[dir.dx+1][dir.dy+1] += within_healing_range? 0 : 100;
				}
			}
			else {
				values[dir.dx+1][dir.dy+1] = Double.MAX_VALUE;
			}
		}

		return values;
	}
	
	public static double[][] battle_tile_values_sparser() throws GameActionException {
	
		boolean[] soldiers_preoccupied = new boolean[Info.n_enemy_soldiers];
		if (Info.n_friendly_soldiers < 5) {
			for (int i=Info.n_enemy_soldiers; --i>=0;) {
				RobotInfo enemy = Info.enemy_soldiers[i];
				for (int j=Info.n_friendly_soldiers; --j>=0;) {
					RobotInfo friend = Info.friendly_soldiers[j];
					if (friend.location.isWithinDistanceSquared(enemy.location, 20)) {
						soldiers_preoccupied[i] = true;
						break;
					}
				}
			}
		}
		
		boolean enemy_shootable_now = false;
		for (int i=Info.n_enemy_soldiers; --i>=0;) {
			enemy_shootable_now = enemy_shootable_now || Info.loc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, Info.ACTION_DIST2);
		}
		for (int i=Info.n_enemy_watchtowers; --i>=0;) {
			enemy_shootable_now = enemy_shootable_now || Info.loc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, Info.ACTION_DIST2);
		}
		for (int i=Info.n_enemy_sages; --i>=0;) {
			enemy_shootable_now = enemy_shootable_now || Info.loc.isWithinDistanceSquared(Info.enemy_sages[i].location, Info.ACTION_DIST2);
		}
		if (!Info.action_ready && !enemy_shootable_now) {
			double[][] values = new double[3][3];
			values[1][1] = -1;
			return values;
		}
		double[][] values = new double[3][3];
		for (Direction dir:Direction.allDirections()) {
			MapLocation testloc = Info.loc.add(dir);
			if (rc.onTheMap(testloc) && !rc.isLocationOccupied(testloc)) {
				double n_enemy_shootable = rc.senseNearbyRobots(testloc, Info.ACTION_DIST2, Info.enemy).length;
				double n_enemy_visible = rc.senseNearbyRobots(testloc, Info.VISION_DIST2, Info.enemy).length;
				for (int i=Info.n_enemy_soldiers; --i>=0;) {
					if (!soldiers_preoccupied[i]) {
						n_enemy_shootable = n_enemy_shootable + (testloc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, RobotType.SOLDIER.actionRadiusSquared)? 1 : 0);
						n_enemy_visible = n_enemy_visible + (testloc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, RobotType.SOLDIER.actionRadiusSquared)? 1 : 0);
					}
					else {
						n_enemy_shootable = n_enemy_shootable + (testloc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, RobotType.SOLDIER.actionRadiusSquared)? 0.9 : 0);
						n_enemy_visible = n_enemy_visible + (testloc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, RobotType.SOLDIER.actionRadiusSquared)? 0.9 : 0);
					}
				}
				for (int i=Info.n_enemy_watchtowers; --i>=0;) {
					n_enemy_shootable = n_enemy_shootable + (testloc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, RobotType.SOLDIER.actionRadiusSquared)? 1 : 0);
					n_enemy_visible = n_enemy_visible + (testloc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, RobotType.SOLDIER.visionRadiusSquared)? 1 : 0);
				}
				for (int i=Info.n_enemy_sages; --i>=0;) {
					n_enemy_shootable = n_enemy_shootable + (testloc.isWithinDistanceSquared(Info.enemy_sages[i].location, RobotType.SOLDIER.actionRadiusSquared)? 1 : 0);
					n_enemy_visible = n_enemy_visible + (testloc.isWithinDistanceSquared(Info.enemy_sages[i].location, RobotType.SOLDIER.visionRadiusSquared)? 1 : 0);
				}
				if (enemy_shootable_now) {
					values[dir.dx+1][dir.dy+1] = +1000*n_enemy_shootable+n_enemy_visible + 2000*(Math.floor((1+rc.senseRubble(testloc)/10.)*RobotType.SOLDIER.actionCooldown)) + (Comms.attacker_seen_before? -0.0001*(Comms.closest_attacker_dx-dir.dx)*(Comms.closest_attacker_dx-dir.dx)+(Comms.closest_attacker_dy-dir.dy)*(Comms.closest_attacker_dy-dir.dy) : 0);
				}
				else {
					double neg_approx_distance = 0;
					if (Comms.attacker_seen_before) {
						neg_approx_distance = Comms.closest_attacker_dx/Comms.closest_attacker_dist*dir.dx + Comms.closest_attacker_dy/Comms.closest_attacker_dist*dir.dy;
					}
					else if (Comms.enemy_seen_before) {
						neg_approx_distance = Comms.closest_enemy_dx/Comms.closest_enemy_dist*dir.dx + Comms.closest_enemy_dy/Comms.closest_enemy_dist*dir.dy;
					}
					values[dir.dx+1][dir.dy+1] = ((n_enemy_shootable > 0)? n_enemy_shootable : 1000) + neg_approx_distance + 1000*(1+rc.senseRubble(testloc)/10.);
				}
				if (Soldier.healing) {
					boolean within_healing_range = false;
					for (int i=Info.n_friendly_archons; --i>=0;) {
						if (Info.friendly_archons[i].location.isWithinDistanceSquared(Info.loc, RobotType.ARCHON.actionRadiusSquared)) {
							within_healing_range = true;
						}
					}
					values[dir.dx+1][dir.dy+1] += within_healing_range? 0 : 100;
				}
			}
			else {
				values[dir.dx+1][dir.dy+1] = Double.MAX_VALUE;
			}
		}

		return values;
	}
	
//	public static double[][] battle_tile_values_sparser() throws GameActionException {
//		
//		boolean enemy_shootable_now = false;
//		for (int i=Info.n_enemy_soldiers; --i>=0;) {
//			enemy_shootable_now = enemy_shootable_now || Info.loc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, Info.ACTION_DIST2);
//		}
//		for (int i=Info.n_enemy_watchtowers; --i>=0;) {
//			enemy_shootable_now = enemy_shootable_now || Info.loc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, Info.ACTION_DIST2);
//		}
//		for (int i=Info.n_enemy_sages; --i>=0;) {
//			enemy_shootable_now = enemy_shootable_now || Info.loc.isWithinDistanceSquared(Info.enemy_sages[i].location, Info.ACTION_DIST2);
//		}
//		
////		boolean[] preoccupied = new boolean[Info.n_enemy_attackers];
////		for (int i=Info.n_enemy_attackers; --i>=0;) {
////			RobotInfo enemy = Info.get_enemy_attacker(i);
////			for (int j=Info.n_friendly_soldiers; --j>=0;) {
////				RobotInfo friend = Info.friendly_soldiers[i];
////				if (friend.location.isWithinDistanceSquared(enemy.location, 20)) {
////					preoccupied[i] = true;
////					break;
////				}
////			}
////		}
//
//		double[][] dpsrubblemap = new double[3][3];
//		for (int d=9; --d>=0;) {
//			MapLocation testloc = Info.loc.translate(d/3-1, d%3-1);
//			dpsrubblemap[d/3][d%3] = 1/(1+rc.senseRubble(testloc)/10.);
//		}
//		double[][] values = new double[3][3];
//		boolean[][] can_shoot_attacker = new boolean[3][3];
//		for (int i=Info.n_enemy_attackers; --i>=0;) {
//			RobotInfo robot = Info.get_enemy_attacker(i);
//			double enemy_rubble_coeff = 1+rc.senseRubble(robot.location)/10.;
//			double enemy_dps = robot.type.damage/enemy_rubble_coeff/(robot.type.actionCooldown/10.);
//			for (int d=9; --d>=0;) {
//				MapLocation testloc = Info.loc.translate(d/3-1, d%3-1);
//				if (testloc.isWithinDistanceSquared(robot.location, 34)) {
//					values[d/3][d%3] += (10/16)*enemy_dps;
//					if (testloc.isWithinDistanceSquared(robot.location, 20)) {
//						values[d/3][d%3] += (6/16)*enemy_dps;
//						can_shoot_attacker[d/3][d%3] = true;
//					}
//				}
//			}
//		}
//		double self_dps_without_rubble = RobotType.SOLDIER.damage/(1+Info.rubble/10.)/(RobotType.SOLDIER.actionCooldown/10.);
//		for (int d=9; --d>=0;) {
//			MapLocation testloc = Info.loc.translate(d/3-1, d%3-1);
//			values[d/3][d%3] -= can_shoot_attacker[d/3][d%3]? self_dps_without_rubble/(1+rc.senseRubble(testloc)/10.) : 0;
//			values[d/3][d%3] += (enemy_shootable_now? 0.000001 : -0.000001)*(d/3*Comms.closest_attacker_dx/Comms.closest_attacker_dist + d%3*Comms.closest_attacker_dy/Comms.closest_attacker_dist);
//			values[d/3][d%3] += (enemy_shootable_now ^ can_shoot_attacker[d/3][d%3])? 100 : -100;
//		}
//		for (int i=Info.n_friendly_archons; --i>=0;) {
//			RobotInfo robot = Info.friendly_archons[i];
//			double healing_dps = 2/(1+rc.senseRubble(robot.location)/10.)/(robot.type.actionCooldown/10.);
//			for (int d=9; --d>=0;) {
//				MapLocation testloc = Info.loc.translate(d/3-1, d%3-1);
//				if (testloc.isWithinDistanceSquared(robot.location, 34)) {
//					values[d/3][d%3] -= healing_dps;
//				}
//			}
//		}
//
//		return values;
//	}
}