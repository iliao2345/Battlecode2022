package debugging_16;

import battlecode.common.*;

public class Laboratory {
	public static final double PREFERRED_WARZONE_DIST = 10;
	public static RobotController rc;
	public static int transmute_cost = 0;
	public static MapLocation[] other_unit_locs = new MapLocation[] {new MapLocation(-100, -100), new MapLocation(-100, -100), new MapLocation(-100, -100), new MapLocation(-100, -100), new MapLocation(-100, -100)};
	public static int n_other_unit_locs = 0;
	
	public static void update() throws GameActionException {
		transmute_cost = (int)Math.floor(20-18*Math.exp(-0.02*Info.friendly_robots.length));
		for (int i=5; --i>=0;) {
			if (rc.canSenseLocation(other_unit_locs[i]) && !rc.isLocationOccupied(other_unit_locs[i])) {
				other_unit_locs[i] = new MapLocation(-100, -100);
			}
		}
		if (Info.friendly_robots.length > 0) {
			for (int i=5; --i>=0;) {
				RobotInfo robot = Info.friendly_robots[Info.rng.nextInt(Info.friendly_robots.length)];
				boolean seen_before = other_unit_locs[0].equals(robot.location) || other_unit_locs[1].equals(robot.location) || other_unit_locs[2].equals(robot.location) || other_unit_locs[3].equals(robot.location) || other_unit_locs[4].equals(robot.location);
				if (!seen_before) {
					other_unit_locs[n_other_unit_locs++ % 5] = robot.location;
				}
			}
		}
//		for (int i=5; --i>=0;) {
//			rc.setIndicatorLine(Info.loc, other_unit_locs[i], 255, 0, 0);
//		}
	}
	
	public static void act() throws GameActionException {
		try_to_move();
//		if (transmute_cost < 6 && rc.canTransmute() && (Info.lead >= 50+transmute_cost || Info.gold % 20 != 0)) {
//			Action.transmute();
//		}
		if (transmute_cost < 6 && rc.canTransmute() && (Info.lead >= 50+transmute_cost || Info.gold % 20 != 0)) {
			Action.transmute();
		}
//		if (rc.canTransmute() && Info.lead >= 50+transmute_cost && transmute_cost < 6) {
//			Action.transmute();
//		}
//		if (rc.canTransmute()) {
//			Action.transmute();
//		}
	}
	
	public static void try_to_move() throws GameActionException {
		if (rc.getMode() == RobotMode.PORTABLE) {
			if (!Info.move_ready) {
				return;
			}
//			else if (Info.n_enemy_soldiers + Info.n_enemy_watchtowers + Info.n_enemy_sages > 0) {  // flee enemy robots
//				RobotInfo closest_robot = null;
//				int closest_dist2 = Integer.MAX_VALUE;
//				for (int i=Info.n_enemy_soldiers; --i>=0;) {
//					if (Info.loc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, closest_dist2-1)) {
//						closest_robot = Info.enemy_soldiers[i];
//						closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
//					} 
//				}
//				for (int i=Info.n_enemy_watchtowers; --i>=0;) {
//					if (Info.loc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, closest_dist2-1)) {
//						closest_robot = Info.enemy_watchtowers[i];
//						closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
//					} 
//				}
//				for (int i=Info.n_enemy_sages; --i>=0;) {
//					if (Info.loc.isWithinDistanceSquared(Info.enemy_sages[i].location, closest_dist2-1)) {
//						closest_robot = Info.enemy_sages[i];
//						closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
//					} 
//				}
//				boolean[][] illegal_tiles = new boolean[3][3];
//				Pathing.target(closest_robot.location, illegal_tiles, -1);
//				return;
//			}

			MapLocation best_loc = null;
//			if (Comms.enemy_seen_before) {
//				double lowest_rubble = Double.MAX_VALUE;
//				int move_radius_squared = 13;
//				MapLocation[] search_locs = rc.getAllLocationsWithinRadiusSquared(Info.loc, move_radius_squared);
//				double dx = 0.001;
//				double dy = 0;
//				for (RobotInfo robot:Info.friendly_robots) {
//					dx += robot.location.x;
//					dy += robot.location.y;
//				}
//				dx = dx / Info.friendly_robots.length - Info.x;
//				dy = dy / Info.friendly_robots.length - Info.y;
//				for (MapLocation loc:search_locs) {
////					if ((loc.isWithinDistanceSquared(other_unit_locs[0], RobotType.LABORATORY.visionRadiusSquared)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[1], RobotType.LABORATORY.visionRadiusSquared)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[2], RobotType.LABORATORY.visionRadiusSquared)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[3], RobotType.LABORATORY.visionRadiusSquared)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[4], RobotType.LABORATORY.visionRadiusSquared)? 1:0) > 1) {
////						continue;
////					}
//					if (dx*(loc.x-Info.x) + dy*(loc.y-Info.y) > -0.0001 && Info.friendly_robots.length >= 5) {
//						continue;
//					}
//					if (rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc) < lowest_rubble && !loc.isWithinDistanceSquared(Comms.closest_enemy_loc, (int) (PREFERRED_WARZONE_DIST*PREFERRED_WARZONE_DIST)) && (!rc.isLocationOccupied(loc) || loc.equals(Info.loc))) {
//						best_loc = loc;
//						lowest_rubble = rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc);
//					}
//				}
//				if (best_loc==null && Comms.closest_enemy_dist < 15) {
//					best_loc = Info.loc.translate((int)(-1000*Comms.closest_enemy_dx/Comms.closest_enemy_dist), (int)(-1000*Comms.closest_enemy_dy/Comms.closest_enemy_dist));
//				}
//				if (best_loc==null) {
////					double dx = (other_unit_locs[0].x + other_unit_locs[1].x + other_unit_locs[2].x + other_unit_locs[3].x + other_unit_locs[4].x) / 5. - Info.x+0.001;
////					double dy = (other_unit_locs[0].y + other_unit_locs[1].y + other_unit_locs[2].y + other_unit_locs[3].y + other_unit_locs[4].y) / 5. - Info.y;
//					best_loc = Info.loc.translate((int)(-1000*dx/Math.hypot(dx, dy)), (int)(-1000*dy/Math.hypot(dx, dy)));
//				}
//			}
//			else {
				double lowest_rubble = Double.MAX_VALUE;
				int move_radius_squared = 13;
				MapLocation[] search_locs = rc.getAllLocationsWithinRadiusSquared(Info.loc, move_radius_squared);
				for (MapLocation loc:search_locs) {
//					if ((loc.isWithinDistanceSquared(other_unit_locs[0], RobotType.LABORATORY.visionRadiusSquared)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[1], RobotType.LABORATORY.visionRadiusSquared)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[2], RobotType.LABORATORY.visionRadiusSquared)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[3], RobotType.LABORATORY.visionRadiusSquared)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[4], RobotType.LABORATORY.visionRadiusSquared)? 1:0) > 1) {
//						continue;
//					}
					if (rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc) < lowest_rubble && (!rc.isLocationOccupied(loc) || loc.equals(Info.loc))) {
						best_loc = loc;
						lowest_rubble = rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc);
					}
				}
				if (best_loc==null) {
					double dx = (other_unit_locs[0].x + other_unit_locs[1].x + other_unit_locs[2].x + other_unit_locs[3].x + other_unit_locs[4].x) / 5. - Info.x+0.001;
					double dy = (other_unit_locs[0].y + other_unit_locs[1].y + other_unit_locs[2].y + other_unit_locs[3].y + other_unit_locs[4].y) / 5. - Info.y;
					best_loc = Info.loc.translate((int)(-1000*dx/Math.hypot(dx, dy)), (int)(-1000*dy/Math.hypot(dx, dy)));
				}
//			}
			rc.setIndicatorLine(Info.loc, best_loc, 0, 255, 0);
			if (best_loc.equals(Info.loc) && rc.canTransform()) {
				rc.transform();
				return;
			}
			Direction dir = Bfs.direction(Bfs.query(best_loc, 100, 2000, false));
			Action.move(dir);
			return;
		}
		if (rc.getMode() == RobotMode.TURRET) {

			MapLocation best_loc = null;
//			if (Comms.enemy_seen_before) {
//				double lowest_rubble = Double.MAX_VALUE;
//				int move_radius_squared = 13;
//				MapLocation[] search_locs = rc.getAllLocationsWithinRadiusSquared(Info.loc, move_radius_squared);
//				for (MapLocation loc:search_locs) {
//					if ((loc.isWithinDistanceSquared(other_unit_locs[0], Info.VISION_DIST2)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[1], Info.VISION_DIST2)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[2], Info.VISION_DIST2)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[3], Info.VISION_DIST2)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[4], Info.VISION_DIST2)? 1:0) > 2) {
//						continue;
//					}
//					if (rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc) < lowest_rubble && !loc.isWithinDistanceSquared(Comms.closest_enemy_loc, (int) (PREFERRED_WARZONE_DIST*PREFERRED_WARZONE_DIST)) && (!rc.isLocationOccupied(loc) || loc.equals(Info.loc))) {
//						best_loc = loc;
//						lowest_rubble = rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc);
//					}
//				}
//				if (best_loc==null) {
//					best_loc = Info.loc.translate((int)(-1000*Comms.closest_enemy_dx/Comms.closest_enemy_dist), (int)(-1000*Comms.closest_enemy_dy/Comms.closest_enemy_dist));
//				}
//			}
//			else {
//				double lowest_rubble = Double.MAX_VALUE;
				int move_radius_squared = 13;
				MapLocation[] search_locs = rc.getAllLocationsWithinRadiusSquared(Info.loc, move_radius_squared);
//				for (MapLocation loc:search_locs) {
////					if ((loc.isWithinDistanceSquared(other_unit_locs[0], Info.VISION_DIST2)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[1], Info.VISION_DIST2)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[2], Info.VISION_DIST2)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[3], Info.VISION_DIST2)? 1:0) + (loc.isWithinDistanceSquared(other_unit_locs[4], Info.VISION_DIST2)? 1:0) > 2) {
////						continue;
////					}
//					if (rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc) < lowest_rubble && (!rc.isLocationOccupied(loc) || loc.equals(Info.loc))) {
//						best_loc = loc;
//						lowest_rubble = rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc);
//					}
//				}
				best_loc = search_locs[Info.rng.nextInt(search_locs.length)];
				if (Info.rubble <= rc.senseRubble(best_loc)) {
					best_loc = Info.loc;
				}
//				if (best_loc==null) {
//					double dx = (other_unit_locs[0].x + other_unit_locs[1].x + other_unit_locs[2].x + other_unit_locs[3].x + other_unit_locs[4].x) / 5. - Info.x+0.001;
//					double dy = (other_unit_locs[0].y + other_unit_locs[1].y + other_unit_locs[2].y + other_unit_locs[3].y + other_unit_locs[4].y) / 5. - Info.y;
//					best_loc = Info.loc.translate((int)(-1000*dx/Math.hypot(dx, dy)), (int)(-1000*dy/Math.hypot(dx, dy)));
//				}
//			}
			
			for (AnomalyScheduleEntry anomaly:rc.getAnomalySchedule()) {
				if (anomaly.anomalyType == AnomalyType.VORTEX && anomaly.roundNumber < Info.round_num+8 && anomaly.roundNumber > Info.round_num && rc.canTransform()) {
					rc.transform();
					return;
				}
			}
			
			boolean enemy_can_reach_while_transforming = !Comms.enemy_seen_before || 10*(1+Info.rubble/10.) > 2*Comms.closest_attacker_dist*(1+Info.rubble_quantile/10.)*RobotType.SOLDIER.movementCooldown/10.;
			if ((!best_loc.equals(Info.loc) && !enemy_can_reach_while_transforming) && rc.isTransformReady()) {
				rc.transform();
				return;
			}
		}
	}

}
