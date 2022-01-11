package watchtowers_16;

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
			boolean[][] illegal_tiles = new boolean[3][3];
			Pathing.target(closest_robot.location, illegal_tiles, 1);
			return;
		}
		if (Comms.closest_attacker_dist < 10 && Comms.attacker_seen_before) {  // flee warzone
			boolean[][] illegal_tiles = new boolean[3][3];
			Pathing.target(Comms.closest_attacker_loc, illegal_tiles, -1);
			return;
		}
		Exploration.repel(Info.friendly_soldiers, Info.n_friendly_soldiers);  // don't block soldiers
		Exploration.repel(Info.friendly_archons, Info.n_friendly_archons);  // don't block soldiers
		Exploration.travel();
	}
	
	public static void try_to_build() throws GameActionException {
		if (!Info.action_ready) {
			return;
		}
		RobotInfo strongest_robot = null;
		int strongest_health = 0;
		for (int i=Info.n_friendly_watchtowers; --i>=0;) {
			RobotInfo robot = Info.friendly_watchtowers[i];
			MapLocation loc = robot.location;
			if (robot.mode == RobotMode.PROTOTYPE && rc.canRepair(loc) && robot.health > strongest_health) {
				strongest_robot = robot;
				strongest_health = robot.health;
			}
		}
		if (strongest_robot != null) {
			Action.repair(strongest_robot.location);
			return;
		}
		Direction chosen_dir = null;
		int valid_dirs = 0;
		for (Direction dir:Math2.UNIT_DIRECTIONS) {
			if (rc.onTheMap(Info.loc.add(dir)) && !rc.isLocationOccupied(Info.loc.add(dir))) {
				valid_dirs++;
				if (Info.rng.nextInt(valid_dirs)==0) {
					chosen_dir = dir;
				}
			}
		}
        if (Info.lead > Archon.SOLDIER_SATURATION_CAP && Info.lead >= RobotType.WATCHTOWER.buildCostLead && chosen_dir != null && Info.n_moveable_tiles > 4) {
            Action.buildRobot(RobotType.WATCHTOWER, chosen_dir);
        }
	}
	
	public static void act() throws GameActionException {
		try_to_build();
		try_to_move();
		try_to_build();
	}

}
