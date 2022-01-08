package micro_6;

import battlecode.common.*;

public class Micro {
	public static RobotController rc;
	public static final int SPARSE_THRESHOLD = 5;
	
	public static boolean dense_mode = false;

	public static void update() throws GameActionException {
		dense_mode = Info.friendly_robots.length + Info.enemy_robots.length > SPARSE_THRESHOLD;
	}
	
	public static boolean[][] illegal_tiles() throws GameActionException {
//		if (Info.n_enemy_soldiers+Info.n_enemy_watchtowers+Info.n_enemy_sages > 0 && Info.type == RobotType.SOLDIER) {
		if (Info.enemy_robots.length > 0 && Info.type == RobotType.SOLDIER) {
			boolean[][] illegal_tiles = new boolean[3][3];
			double[][] values = battle_tile_values_dense();
			double min_value = Double.MAX_VALUE;
			for (int dx=2; --dx>=-1;) {
				for (int dy=2; --dy>=-1;) {
					min_value = Math.min(min_value, values[dx+1][dy+1]);
				}
			}
			rc.setIndicatorString(String.valueOf(min_value));
//			double illegal_threshold = 1;
			double illegal_threshold = min_value;
			for (int dx=2; --dx>=-1;) {
				for (int dy=2; --dy>=-1;) {
					illegal_tiles[dx+1][dy+1] = values[dx+1][dy+1] > illegal_threshold;
				}
			}
			return illegal_tiles;
		}
		return new boolean[3][3];
	}
	
	public static double[][] battle_tile_values_dense() throws GameActionException {
		double enemy_rubble_plus_10 = 10+rc.senseRubble(Info.enemy_robots[Info.rng.nextInt(Info.enemy_robots.length)].location);
		double[][] values = new double[3][3];
		for (Direction dir:Direction.allDirections()) {
			MapLocation testloc = Info.loc.add(dir);
			if (rc.onTheMap(testloc)) {
				int n_enemy = rc.senseNearbyRobots(testloc, Info.ACTION_DIST2, Info.enemy).length;
				int n_friendly = rc.senseNearbyRobots(testloc, Info.ACTION_DIST2, Info.friendly).length;
				values[dir.dx+1][dir.dy+1] = ((double)n_enemy)/(n_friendly + 1) * (10+rc.senseRubble(testloc)) / enemy_rubble_plus_10;
				if (n_enemy == 0) {
					values[dir.dx+1][dir.dy+1] = 1;
				}
				if (n_friendly >= 25 && dir.dx == 0 && dir.dy == 0) {
					return new double[3][3];
				}
			}
			else {
				values[dir.dx+1][dir.dy+1] = Double.MAX_VALUE;
			}
		}

		return values;
	}
}
