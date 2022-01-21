package sages_3;

import battlecode.common.*;

public class Laboratory {
	public static final double PREFERRED_WARZONE_DIST = 10;
	public static RobotController rc;
	public static int transmute_cost = 0;
	
	public static void update() throws GameActionException {
		transmute_cost = (int)Math.floor(20-18*Math.exp(-0.02*Info.friendly_robots.length));
	}
	
	public static void act() throws GameActionException {
		try_to_move();
//		if (transmute_cost == 2 && rc.canTransmute()) {
//			Action.transmute();
//		}
		if (rc.canTransmute() && Info.lead >= 50+transmute_cost) {
			Action.transmute();
		}
	}
	
	public static void try_to_move() throws GameActionException {
		if (rc.getMode() == RobotMode.PORTABLE) {
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
				Pathing.target(closest_robot.location, illegal_tiles, -1);
				return;
			}

			MapLocation best_loc = null;
			if (Comms.enemy_seen_before) {
				double lowest_rubble = Double.MAX_VALUE;
				int move_radius_squared = 13;
				MapLocation[] search_locs = rc.getAllLocationsWithinRadiusSquared(Info.loc, move_radius_squared);
				for (MapLocation loc:search_locs) {
					if (rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc) < lowest_rubble && !loc.isWithinDistanceSquared(Comms.closest_enemy_loc, (int) (PREFERRED_WARZONE_DIST*PREFERRED_WARZONE_DIST)) && (!rc.isLocationOccupied(loc) || loc.equals(Info.loc))) {
						best_loc = loc;
						lowest_rubble = rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc);
					}
				}
				if (best_loc==null) {
					best_loc = Info.loc.translate((int)(-1000*Comms.closest_enemy_dx/Comms.closest_enemy_dist), (int)(-1000*Comms.closest_enemy_dy/Comms.closest_enemy_dist));
				}
			}
			else {
				double lowest_rubble = Double.MAX_VALUE;
				int move_radius_squared = 13;
				MapLocation[] search_locs = rc.getAllLocationsWithinRadiusSquared(Info.loc, move_radius_squared);
				for (MapLocation loc:search_locs) {
					if (rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc) < lowest_rubble && (!rc.isLocationOccupied(loc) || loc.equals(Info.loc))) {
						best_loc = loc;
						lowest_rubble = rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc);
					}
				}
			}
			rc.setIndicatorLine(Info.loc, best_loc, 0, 255, 0);
			if (best_loc.equals(Info.loc) && rc.canTransform()) {
				rc.transform();
				return;
			}
			Direction dir = Bfs.direction(Bfs.query(best_loc, 100, 2000, false));
			Action.move(dir);
			return;
		}
		if (rc.getMode() == RobotMode.TURRET && Comms.enemy_seen_before) {

			MapLocation best_loc = null;
			if (Comms.enemy_seen_before) {
				double lowest_rubble = Double.MAX_VALUE;
				int move_radius_squared = 13;
				MapLocation[] search_locs = rc.getAllLocationsWithinRadiusSquared(Info.loc, move_radius_squared);
				for (MapLocation loc:search_locs) {
					if (rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc) < lowest_rubble && !loc.isWithinDistanceSquared(Comms.closest_enemy_loc, (int) (PREFERRED_WARZONE_DIST*PREFERRED_WARZONE_DIST)) && (!rc.isLocationOccupied(loc) || loc.equals(Info.loc))) {
						best_loc = loc;
						lowest_rubble = rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc);
					}
				}
				if (best_loc==null) {
					best_loc = Info.loc.translate((int)(-1000*Comms.closest_enemy_dx/Comms.closest_enemy_dist), (int)(-1000*Comms.closest_enemy_dy/Comms.closest_enemy_dist));
				}
			}
			else {
				double lowest_rubble = Double.MAX_VALUE;
				int move_radius_squared = 13;
				MapLocation[] search_locs = rc.getAllLocationsWithinRadiusSquared(Info.loc, move_radius_squared);
				for (MapLocation loc:search_locs) {
					if (rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc) < lowest_rubble && (!rc.isLocationOccupied(loc) || loc.equals(Info.loc))) {
						best_loc = loc;
						lowest_rubble = rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc);
					}
				}
			}
			
			for (AnomalyScheduleEntry anomaly:rc.getAnomalySchedule()) {
				if (anomaly.anomalyType == AnomalyType.VORTEX && anomaly.roundNumber < Info.round_num+8 && anomaly.roundNumber > Info.round_num && rc.canTransform()) {
					rc.transform();
					return;
				}
			}
			
			boolean enemy_can_reach_while_transforming = 10*(1+Info.rubble/10.) > 2*Comms.closest_attacker_dist*(1+Info.rubble_quantile/10.)*RobotType.SOLDIER.movementCooldown/10.;
			if ((!best_loc.equals(Info.loc) && !enemy_can_reach_while_transforming) && rc.isTransformReady()) {
				rc.transform();
				return;
			}
		}
	}

}
