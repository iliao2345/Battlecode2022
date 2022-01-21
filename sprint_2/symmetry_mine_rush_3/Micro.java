package symmetry_mine_rush_3;

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
	public static boolean chase_miners = false;
	public static boolean outnumbered = false;
	public static int retreat_timer = 0;
	public static boolean charging = false;
	public static int max_enemy_seen = 0;

	public static void update() throws GameActionException {
		chase_miners = Info.n_enemy_miners == Info.enemy_robots.length;
		retreat_timer = (Info.n_enemy_attackers > Info.n_friendly_attackers+1)? 10 : Math.max(0, retreat_timer-1);
		outnumbered = retreat_timer > 0;
	}
	
	public static boolean[][] illegal_tiles() throws GameActionException {
		if (Info.type != RobotType.SOLDIER) {
			return new boolean[3][3];
		}
		if (Info.enemy_robots.length > 0) {
			boolean[][] illegal_tiles = new boolean[3][3];
			double[][] values;
//			if (Info.n_enemy_attackers > 3) {
				values = battle_tile_values_dense();
//			}
//			else {
//				values = battle_tile_values_sparse();
//			}
			double min_value = Double.MAX_VALUE;
			for (int dx=2; --dx>=-1;) {
				for (int dy=2; --dy>=-1;) {
					min_value = Math.min(min_value, values[dx+1][dy+1]);
				}
			}
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
	
	public static double[][] battle_tile_values_dense() throws GameActionException {
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
				if (enemy_shootable_now && !chase_miners) {
					values[dir.dx+1][dir.dy+1] = +1000*n_enemy_shootable+n_enemy_visible + 2000*(Math.floor((10+rc.senseRubble(testloc)/10.)*RobotType.SOLDIER.actionCooldown)) + (Comms.attacker_seen_before? -0.0001*(Comms.closest_attacker_dx-dir.dx)*(Comms.closest_attacker_dx-dir.dx)+(Comms.closest_attacker_dy-dir.dy)*(Comms.closest_attacker_dy-dir.dy) : 0);
				}
				else if (!chase_miners){
					double neg_approx_distance = 0;
					if (Comms.attacker_seen_before) {
						neg_approx_distance = Comms.closest_attacker_dx/Comms.closest_attacker_dist*dir.dx + Comms.closest_attacker_dy/Comms.closest_attacker_dist*dir.dy;
					}
					else if (Comms.enemy_seen_before) {
						neg_approx_distance = Comms.closest_enemy_dx/Comms.closest_enemy_dist*dir.dx + Comms.closest_enemy_dy/Comms.closest_enemy_dist*dir.dy;
					}
					values[dir.dx+1][dir.dy+1] = ((n_enemy_shootable > 0)? n_enemy_shootable : 1.5) + neg_approx_distance + 1000*(10+rc.senseRubble(testloc)/10.);
				}
				else {
					double neg_approx_distance = 0;
					if (Comms.enemy_seen_before) {
						neg_approx_distance = Comms.closest_enemy_dx/Comms.closest_enemy_dist*dir.dx + Comms.closest_enemy_dy/Comms.closest_enemy_dist*dir.dy;
					}
					values[dir.dx+1][dir.dy+1] = (1000-n_enemy_shootable) - neg_approx_distance + 1000*(10+rc.senseRubble(testloc)/10.);
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