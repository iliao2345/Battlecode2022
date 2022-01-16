package dont_flee_miners_19;

import battlecode.common.*;

public class Builder {
	public static RobotController rc;
	public static double FARM_ENEMY_REPULSION = 0.1;
	
	public static void update() throws GameActionException {
	}
	
	public static void try_to_move() throws GameActionException {
		if (!Info.move_ready) {
			return;
		}
		if (Info.n_enemy_soldiers + Info.n_enemy_watchtowers + Info.n_enemy_sages > 0) {  // flee enemy robots
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
		RobotInfo closest_robot = null;
		int closest_dist2 = Integer.MAX_VALUE;
		for (int i=Info.n_friendly_watchtowers; --i>=0;) {
			RobotInfo robot = Info.friendly_watchtowers[i];
			MapLocation loc = robot.location;
			if (robot.mode == RobotMode.PROTOTYPE && Info.loc.isWithinDistanceSquared(loc, closest_dist2)) {
				closest_robot = robot;
				closest_dist2 = Info.loc.distanceSquaredTo(loc);
			}
		}
		if (closest_robot != null) {
			if (Comms.enemy_seen_before) {
				boolean[][] illegal_tiles = new boolean[3][3];
				int max_dist2 = Math.max(2, Info.loc.distanceSquaredTo(closest_robot.location)-1);
				for (Direction dir:Direction.allDirections()) {
					illegal_tiles[dir.dx+1][dir.dy+1] = !rc.adjacentLocation(dir).isWithinDistanceSquared(closest_robot.location, max_dist2);
				}
				Pathing.target(Comms.closest_enemy_loc, illegal_tiles, 1);
			}
			else {
				boolean[][] illegal_tiles = new boolean[3][3];
				Pathing.target(closest_robot.location, illegal_tiles, 1);
			}
			return;
		}
		if (Comms.closest_attacker_dist < 10 && Comms.attacker_seen_before) {  // flee warzone
			boolean[][] illegal_tiles = new boolean[3][3];
			Pathing.target(Comms.closest_attacker_loc, illegal_tiles, -1);
			return;
		}
		Exploration.repel(Info.friendly_soldiers, Info.n_friendly_soldiers, 1);  // don't block soldiers
		Exploration.repel(Info.friendly_archons, Info.n_friendly_archons, 1);  // don't block soldiers
//		double magnitude = Math.sqrt(Exploration.force_dx*Exploration.force_dx+Exploration.force_dy*Exploration.force_dy);
//		Exploration.force_dx += Comms.closest_enemy_dx / Comms.closest_enemy_dist / magnitude;
//		Exploration.force_dy += Comms.closest_enemy_dy / Comms.closest_enemy_dist / magnitude;
		Exploration.travel();
	}
	
	public static void try_to_build() throws GameActionException {
		if (!Info.action_ready) {
			return;
		}
		RobotInfo strongest_robot = null;
		int strongest_health = Integer.MIN_VALUE;
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
		if (strongest_robot != null) {
			Action.repair(strongest_robot.location);
			return;
		}
		int least_occupied_amount = Integer.MAX_VALUE;
		Direction least_occupied_dir = null;
		for (Direction dir:Math2.UNIT_DIRECTIONS) {
			if (rc.onTheMap(Info.loc.add(dir)) && !rc.isLocationOccupied(Info.loc.add(dir))) {
				int occupied_amount = rc.senseNearbyRobots(rc.adjacentLocation(dir), 2, Info.friendly).length;
				if (Comms.enemy_seen_before) {
					occupied_amount = 10000*occupied_amount + rc.adjacentLocation(dir).distanceSquaredTo(Comms.closest_enemy_loc);
				}
				if (occupied_amount < least_occupied_amount) {
					least_occupied_amount = occupied_amount;
					least_occupied_dir = dir;
				}
			}
		}
		if (Info.lead > Archon.WATCHTOWER_SATURATION_CAP && Info.lead >= RobotType.WATCHTOWER.buildCostLead && least_occupied_dir != null) {
            Action.buildRobot(RobotType.WATCHTOWER, least_occupied_dir);
        }
//		Direction chosen_dir = null;
//		int valid_dirs = 0;
//		for (Direction dir:Math2.UNIT_DIRECTIONS) {
//			if (rc.onTheMap(Info.loc.add(dir)) && !rc.isLocationOccupied(Info.loc.add(dir))) {
//				valid_dirs++;
//				if (Info.rng.nextInt(valid_dirs)==0) {
//					chosen_dir = dir;
//				}
//			}
//		}
//		if (Info.lead > Archon.WATCHTOWER_SATURATION_CAP && Info.lead >= RobotType.WATCHTOWER.buildCostLead && chosen_dir != null && Info.n_moveable_tiles >= 8) {
//            Action.buildRobot(RobotType.WATCHTOWER, chosen_dir);
//        }
	}
	
	public static void act() throws GameActionException {
		try_to_build();
		try_to_move();
		try_to_build();
	}

}
