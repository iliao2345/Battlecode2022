package sages_3;

import battlecode.common.*;

public class Builder {
	public static RobotController rc;
	public static double FARM_ENEMY_REPULSION = 0.1;
	public static int[][] REPAIR_OFFSETS = new int[][] {{-2,-1},{-2,0},{-2,1},{-1,-2},{-1,-1},{-1,0},{-1,1},{-1,2},{0,-2},{0,-1},{0,0},{0,1},{0,2},{1,-2},{1,-1},{1,0},{1,1},{1,2},{2,-1},{2,0},{2,1}};
	public static boolean back_edge = false;
	public static boolean build_new_lab = false;
	public static MapLocation build_loc = null;
	
	public static void update() throws GameActionException {
		back_edge = true;
		if (Comms.enemy_seen_before) {
			double back_dx = -Comms.closest_enemy_dx/Comms.closest_enemy_dist;
			double back_dy = -Comms.closest_enemy_dy/Comms.closest_enemy_dist;
			MapLocation test_loc_0 = Info.loc.translate((int)(7*back_dx+3*back_dy), (int)(7*back_dy-3*back_dx));
			MapLocation test_loc_1 = Info.loc.translate((int)(7*back_dx-3*back_dy), (int)(7*back_dy+3*back_dx));
			back_edge = (test_loc_0.x < 0 || test_loc_0.x > Info.MAP_WIDTH-1 || test_loc_0.y < 0 || test_loc_0.y > Info.MAP_HEIGHT-1) && (test_loc_1.x < 0 || test_loc_1.x > Info.MAP_WIDTH-1 || test_loc_1.y < 0 || test_loc_1.y > Info.MAP_HEIGHT-1);
		}
//		back_edge = Info.x < 3 || Info.x > Info.MAP_WIDTH-1-3 || Info.y < 3 || Info.y > Info.MAP_HEIGHT-1-3;
		build_new_lab = false;
		build_loc = null;
	}
	
	public static void try_to_move() throws GameActionException {
		if (!Info.move_ready) {
			return;
		}
		else if (Info.n_enemy_soldiers + Info.n_enemy_watchtowers + Info.n_enemy_sages > 0) {  // flee enemy robots
			RobotInfo closest_robot = null;
			int closest_dist2 = Integer.MAX_VALUE;
			for (int i=Info.n_enemy_soldiers; --i>=0;) {
				if (Info.loc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, closest_dist2-1)) {
					closest_robot = Info.enemy_soldiers[i];
					closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
				} 
			}
			for (int i=Info.n_enemy_watchtowers; --i>=0;) {
				if (Info.loc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, closest_dist2-1)) {
					closest_robot = Info.enemy_watchtowers[i];
					closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
				} 
			}
			for (int i=Info.n_enemy_sages; --i>=0;) {
				if (Info.loc.isWithinDistanceSquared(Info.enemy_sages[i].location, closest_dist2-1)) {
					closest_robot = Info.enemy_sages[i];
					closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
				} 
			}
			boolean[][] illegal_tiles = new boolean[3][3];
			rc.setIndicatorLine(Info.loc, closest_robot.location, 255, 0, 0);
			Pathing.target(closest_robot.location, illegal_tiles, -1);
			return;
		}
		RobotInfo closest_robot = null;  // repair labs and towers
		int closest_dist2 = Integer.MAX_VALUE;
		for (int i=Info.n_friendly_watchtowers; --i>=0;) {
			RobotInfo robot = Info.friendly_watchtowers[i];
			MapLocation loc = robot.location;
			if (robot.mode == RobotMode.PROTOTYPE && Info.loc.isWithinDistanceSquared(loc, closest_dist2)) {
				closest_robot = robot;
				closest_dist2 = Info.loc.distanceSquaredTo(loc);
			}
		}
		for (int i=Info.n_friendly_laboratories; --i>=0;) {
			RobotInfo robot = Info.friendly_laboratories[i];
			MapLocation loc = robot.location;
			if (robot.mode == RobotMode.PROTOTYPE && Info.loc.isWithinDistanceSquared(loc, closest_dist2)) {
				closest_robot = robot;
				closest_dist2 = Info.loc.distanceSquaredTo(loc);
			}
		}
		if (closest_robot != null) {
			MapLocation best_loc = null;
			if (Comms.enemy_seen_before) {
				double best_score = Double.MAX_VALUE;
				for (int[] diff:REPAIR_OFFSETS) {
					MapLocation loc = closest_robot.location.translate(diff[0], diff[1]);
					double score = rc.canSenseLocation(loc)? rc.senseRubble(loc) - 0.0001*loc.distanceSquaredTo(Comms.closest_enemy_loc) : Double.POSITIVE_INFINITY;
					if (score < best_score) {
						best_score = score;
						best_loc = loc;
					}
				}
				if (best_loc == null) {
					best_loc = closest_robot.location;
				}
			}
			else {
				best_loc = Info.loc.translate(Info.x - Info.MAP_WIDTH/2-1, Info.y-Info.MAP_HEIGHT/2-1);
			}
			rc.setIndicatorLine(Info.loc, best_loc, 0, 0, 255);
			Action.move(Bfs.direction(Bfs.query(best_loc, 100, 1, false)));
			return;
		}
		else if (Comms.enemy_seen_before && !back_edge) {  // flee warzone
			int target_x = Math.min(Info.MAP_WIDTH, Math.max(0, Info.x + (int)(-10*Comms.closest_enemy_dx/Comms.closest_enemy_dist)));
			int target_y = Math.min(Info.MAP_HEIGHT, Math.max(0, Info.y + (int)(-10*Comms.closest_enemy_dy/Comms.closest_enemy_dist)));
			MapLocation target_loc = new MapLocation(target_x, target_y);
			rc.setIndicatorLine(Info.loc, target_loc, 128, 0, 0);
			Action.move(Bfs.direction(Bfs.query(target_loc, 100, 1, false)));
			return;
		}
//		else if (Info.friendly_robots.length > 1) {
//			double total_x = 0;
//			double total_y = 0;
//			for (RobotInfo robot:Info.friendly_robots) {
//				total_x += robot.location.x;
//				total_y += robot.location.y;
//			}
//			double mean_dx = total_x/Info.friendly_robots.length - Info.x;
//			double mean_dy = total_y/Info.friendly_robots.length - Info.y;
//			double r = Math.hypot(mean_dx, mean_dy);
//			if (r>1e-10) {
//				MapLocation target_loc = Info.loc.translate((int)(-1000*mean_dx/r), (int)(-1000*mean_dy/r));
//				Action.move(Bfs.direction(Bfs.query(target_loc, 100, 1, false)));
//			}
//			return;
//		}
		else {
			int rubble = Integer.MAX_VALUE;
			MapLocation best_loc = null;
			for (MapLocation loc:Info.visible_tiles) {
				if (rc.senseRubble(loc) < rubble && !rc.isLocationOccupied(loc)) {
					rubble = rc.senseRubble(loc);
					best_loc = loc;
				}
			}
			if (best_loc != null && !Info.loc.equals(best_loc) && !Info.loc.isAdjacentTo(best_loc)) {
				Action.move(Bfs.direction(Bfs.query(best_loc, 100, 2000, false)));
			}
			if (best_loc != null && Info.loc.equals(best_loc)) {
				Direction dir = Math2.UNIT_DIRECTIONS[Info.rng.nextInt(8)];
				if (rc.canMove(dir)) {
					Action.move(Bfs.direction(Bfs.query(best_loc, 100, 2000, false)));
				}
			}
			build_new_lab = true;
			build_loc = best_loc;
			rc.setIndicatorLine(Info.loc, best_loc, 0, 255, 0);
			return;
		}
	}
	
	public static void try_to_build() throws GameActionException {
		if (!Info.action_ready) {
			return;
		}
		RobotInfo strongest_robot = null;
		int strongest_health = Integer.MIN_VALUE;
		for (int i=Info.n_friendly_laboratories; --i>=0;) {
			RobotInfo robot = Info.friendly_laboratories[i];
			MapLocation loc = robot.location;
			if (rc.canRepair(loc)) {
				if (robot.mode == RobotMode.PROTOTYPE && robot.health > strongest_health) {
					strongest_robot = robot;
					strongest_health = robot.health;
				}
				else if (-robot.health > strongest_health && robot.health < RobotType.LABORATORY.health) {
					strongest_robot = robot;
					strongest_health = -robot.health;
				}
			}
		}
		if (strongest_robot == null) {
			for (int i=Info.n_friendly_watchtowers; --i>=0;) {
				RobotInfo robot = Info.friendly_watchtowers[i];
				MapLocation loc = robot.location;
				if (rc.canRepair(loc)) {
					if (robot.mode == RobotMode.PROTOTYPE && robot.health > strongest_health) {
						strongest_robot = robot;
						strongest_health = robot.health;
					}
					else if (-robot.health > strongest_health && robot.health < RobotType.WATCHTOWER.health) {
						strongest_robot = robot;
						strongest_health = -robot.health;
					}
				}
			}
		}
		if (strongest_robot != null) {
			Action.repair(strongest_robot.location);
			return;
		}
		if (build_new_lab && Info.income_didt>0 && Info.income_estimate > 2 && Info.loc.isAdjacentTo(build_loc) && rc.canBuildRobot(RobotType.LABORATORY, Math2.direction_to(build_loc.x-Info.x, build_loc.y-Info.y))) {
            Action.buildRobot(RobotType.LABORATORY, Math2.direction_to(build_loc.x-Info.x, build_loc.y-Info.y));
		}
	}
	
	public static void act() throws GameActionException {
		try_to_build();
		try_to_move();
		try_to_build();
	}

}
