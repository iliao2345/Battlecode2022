package less_labs_5;

import battlecode.common.*;

public class Builder {
	public static RobotController rc;
	public static double FARM_ENEMY_REPULSION = 0.1;
	public static int[][] REPAIR_OFFSETS = new int[][] {{-2,-1},{-2,0},{-2,1},{-1,-2},{-1,-1},{-1,0},{-1,1},{-1,2},{0,-2},{0,-1},{0,0},{0,1},{0,2},{1,-2},{1,-1},{1,0},{1,1},{1,2},{2,-1},{2,0},{2,1}};
	public static boolean back_edge = false;
	public static boolean build_new_lab = false;
	public static MapLocation build_loc = null;
	public static MapLocation[] lab_locs = new MapLocation[] {new MapLocation(-100,-100), new MapLocation(-100,-100), new MapLocation(-100,-100), new MapLocation(-100,-100), new MapLocation(-100,-100)};
	public static int n_lab_locs = 0;
	
	public static void update() throws GameActionException {
		back_edge = true;
		if (Comms.enemy_seen_before) {
			double back_dx = -Comms.closest_enemy_dx/Comms.closest_enemy_dist;
			double back_dy = -Comms.closest_enemy_dy/Comms.closest_enemy_dist;
			MapLocation test_loc_0 = Info.loc.translate((int)(7*back_dx+3*back_dy), (int)(7*back_dy-3*back_dx));
			MapLocation test_loc_1 = Info.loc.translate((int)(7*back_dx-3*back_dy), (int)(7*back_dy+3*back_dx));
			rc.setIndicatorLine(Info.loc, test_loc_0, 255, 0, 0);
			rc.setIndicatorLine(Info.loc, test_loc_1, 255, 0, 0);
//			back_edge = (test_loc_0.x < 0 || test_loc_0.x > Info.MAP_WIDTH-1 || test_loc_0.y < 0 || test_loc_0.y > Info.MAP_HEIGHT-1) && (test_loc_1.x < 0 || test_loc_1.x > Info.MAP_WIDTH-1 || test_loc_1.y < 0 || test_loc_1.y > Info.MAP_HEIGHT-1);
			back_edge = Info.friendly_robots.length < 3 && Comms.closest_attacker_dist > 20 || (test_loc_0.x < 0 || test_loc_0.x > Info.MAP_WIDTH-1 || test_loc_0.y < 0 || test_loc_0.y > Info.MAP_HEIGHT-1) && (test_loc_1.x < 0 || test_loc_1.x > Info.MAP_WIDTH-1 || test_loc_1.y < 0 || test_loc_1.y > Info.MAP_HEIGHT-1);
		}
		build_new_lab = false;
		build_loc = null;
		for (int i=5; --i>=0;) {
			if (rc.canSenseLocation(lab_locs[i]) && !rc.isLocationOccupied(lab_locs[i])) {
				lab_locs[i] = new MapLocation(-100, -100);
			}
		}
		if (Info.n_friendly_laboratories > 0) {
			for (int i=5; --i>=0;) {
				RobotInfo robot = Info.friendly_laboratories[Info.rng.nextInt(Info.n_friendly_laboratories)];
				boolean seen_before = lab_locs[0].equals(robot.location) || lab_locs[1].equals(robot.location) || lab_locs[2].equals(robot.location) || lab_locs[3].equals(robot.location) || lab_locs[4].equals(robot.location);
				if (!seen_before) {
					lab_locs[n_lab_locs++ % 5] = robot.location;
				}
			}
		}
//		for (int i=5; --i>=0;) {
//			rc.setIndicatorLine(Info.loc, lab_locs[i], 255, 0, 0);
//		}
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
			Action.move(Bfs.direction(Bfs.query(best_loc, 100, 2000, false)));
			return;
		}
		else if (Comms.enemy_seen_before && !back_edge) {  // flee warzone
			int target_x = Math.min(Info.MAP_WIDTH, Math.max(0, Info.x + (int)(-10*Comms.closest_enemy_dx/Comms.closest_enemy_dist)));
			int target_y = Math.min(Info.MAP_HEIGHT, Math.max(0, Info.y + (int)(-10*Comms.closest_enemy_dy/Comms.closest_enemy_dist)));
			MapLocation target_loc = new MapLocation(target_x, target_y);
			rc.setIndicatorLine(Info.loc, target_loc, 128, 0, 0);
			Action.move(Bfs.direction(Bfs.query(target_loc, 100, 2000, false)));
			return;
		}
		else if (Info.friendly_robots.length >= 3) {
			double total_x = 0;
			double total_y = 0;
			for (RobotInfo robot:Info.friendly_robots) {
				total_x += robot.location.x;
				total_y += robot.location.y;
			}
			double mean_dx = total_x/Info.friendly_robots.length - Info.x;
			double mean_dy = total_y/Info.friendly_robots.length - Info.y;
			double r = Math.hypot(mean_dx, mean_dy);
			if (r>1e-10) {
				MapLocation target_loc = Info.loc.translate((int)(-1000*mean_dx/r), (int)(-1000*mean_dy/r));
				Action.move(Bfs.direction(Bfs.query(target_loc, 100, 1, false)));
			}
			return;
		}
		else if (rc.getRobotCount()-rc.getArchonCount()-Comms.total_miner_count > 20*(Comms.laboratory_count+0.5)) {
//			double dx = 0;
//			double dy = 0;
//			if (Info.friendly_robots.length >= 6) {
//				for (RobotInfo robot:Info.friendly_robots) {
//					dx += robot.location.x;
//					dy += robot.location.y;
//				}
//				dx = dx / Info.friendly_robots.length - Info.x;
//				dy = dy / Info.friendly_robots.length - Info.y;
//			}
			double best_rubble = Double.POSITIVE_INFINITY;
			MapLocation best_loc = null;
			for (MapLocation loc:Info.visible_tiles) {
				if ((loc.isWithinDistanceSquared(lab_locs[0], RobotType.LABORATORY.visionRadiusSquared)? 1:0) + (loc.isWithinDistanceSquared(lab_locs[1], RobotType.LABORATORY.visionRadiusSquared)? 1:0) + (loc.isWithinDistanceSquared(lab_locs[2], RobotType.LABORATORY.visionRadiusSquared)? 1:0) + (loc.isWithinDistanceSquared(lab_locs[3], RobotType.LABORATORY.visionRadiusSquared)? 1:0) + (loc.isWithinDistanceSquared(lab_locs[4], RobotType.LABORATORY.visionRadiusSquared)? 1:0) > 1) {
					continue;
				}
				double rubble = rc.senseRubble(loc) + 0.00001*Info.loc.distanceSquaredTo(loc);
				if (rubble < best_rubble && !rc.isLocationOccupied(loc)) {
					best_rubble = rubble;
					best_loc = loc;
				}
			}
			if (best_loc==null) {
				double dx = (lab_locs[0].x + lab_locs[1].x + lab_locs[2].x + lab_locs[3].x + lab_locs[4].x) / 5. - Info.x+0.001;
				double dy = (lab_locs[0].y + lab_locs[1].y + lab_locs[2].y + lab_locs[3].y + lab_locs[4].y) / 5. - Info.y;
				best_loc = Info.loc.translate((int)(-1000*dx/Math.hypot(dx, dy)), (int)(-1000*dy/Math.hypot(dx, dy)));
			}
			if (best_loc != null) {
				rc.setIndicatorLine(Info.loc, best_loc, 0, 255, 0);
			}
			if (best_loc != null && !Info.loc.equals(best_loc) && !Info.loc.isAdjacentTo(best_loc)) {
				Action.move(Bfs.direction(Bfs.query(best_loc, 100, 2000, false)));
				return;
			}
			if (best_loc != null && Info.loc.equals(best_loc)) {
				Direction dir = Math2.UNIT_DIRECTIONS[Info.rng.nextInt(8)];
				if (rc.canMove(dir)) {
					Action.move(dir);
				}
				return;
			}
			build_new_lab = true;
			build_loc = best_loc;
			return;
		}
//		else if (Info.n_friendly_laboratories > 0) {
//
//				double dx = (other_unit_locs[0].x + other_unit_locs[1].x + other_unit_locs[2].x + other_unit_locs[3].x + other_unit_locs[4].x) / 5. - Info.x+0.001;
//				double dy = (other_unit_locs[0].y + other_unit_locs[1].y + other_unit_locs[2].y + other_unit_locs[3].y + other_unit_locs[4].y) / 5. - Info.y;
//		}
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
		if (build_new_lab) {
			rc.setIndicatorLine(Info.loc, build_loc, 0, 255, 0);
		}
		if (build_new_lab && Info.loc.isAdjacentTo(build_loc) && rc.canBuildRobot(RobotType.LABORATORY, Math2.direction_to(build_loc.x-Info.x, build_loc.y-Info.y))) {
            Action.buildRobot(RobotType.LABORATORY, Math2.direction_to(build_loc.x-Info.x, build_loc.y-Info.y));
		}
	}
	
	public static void act() throws GameActionException {
		try_to_build();
		try_to_move();
		try_to_build();
	}

}
