package econ_cap_13;

import battlecode.common.*;

public class Soldier {
	public static RobotController rc;

	
	public static void update() throws GameActionException {
	}
	
	public static void try_to_shoot()  throws GameActionException {
		if (!Info.action_ready) {
			return;
		}
//		if (rc.senseNearbyRobots(Info.ACTION_DIST2, Info.enemy).length == 0) {
//			return;
//		}
		RobotInfo weakest_robot = null;
		int weakest_health = Integer.MAX_VALUE;
		for (int i=Info.n_enemy_soldiers; --i>=0;) {
			if (Info.enemy_soldiers[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, Info.ACTION_DIST2)) {
				weakest_robot = Info.enemy_soldiers[i];
				weakest_health = Info.enemy_soldiers[i].health;
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_watchtowers; --i>=0;) {
				if (Info.enemy_watchtowers[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_watchtowers[i];
					weakest_health = Info.enemy_watchtowers[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_sages; --i>=0;) {
				if (Info.enemy_sages[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_sages[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_sages[i];
					weakest_health = Info.enemy_sages[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_miners; --i>=0;) {
				if (Info.enemy_miners[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_miners[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_miners[i];
					weakest_health = Info.enemy_miners[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_archons; --i>=0;) {
				if (Info.enemy_archons[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_archons[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_archons[i];
					weakest_health = Info.enemy_archons[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_laboratories; --i>=0;) {
				if (Info.enemy_laboratories[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_laboratories[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_laboratories[i];
					weakest_health = Info.enemy_laboratories[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_builders; --i>=0;) {
				if (Info.enemy_builders[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_builders[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_builders[i];
					weakest_health = Info.enemy_builders[i].health;
				}
			}
		}
		if (weakest_robot != null) {
			Action.attack(weakest_robot.location);
		}
		else {
			RobotInfo[] shootable_robots = rc.senseNearbyRobots(Info.ACTION_DIST2, Info.enemy);
			if (shootable_robots.length > 0) {
				Action.attack(shootable_robots[0].location);
			}
		}
	}
	
	public static void act() throws GameActionException {
		try_to_shoot();
		
		if (Info.move_ready) {
			RobotInfo closest_robot = null;
			int closest_dist2 = Integer.MAX_VALUE;
			for (RobotInfo robot:Info.enemy_robots) {
				if (Info.loc.isWithinDistanceSquared(robot.location, closest_dist2-1)) {
					closest_robot = robot;
					closest_dist2 = Info.loc.distanceSquaredTo(robot.location);
				}
			}
			if (closest_robot != null) {
				boolean[][] illegal_tiles = Micro.illegal_tiles();
//				Pathing.target(closest_robot.location, illegal_tiles, 1);
				Pathing.approach(closest_robot.location, illegal_tiles);
			}
			else if (Comms.attacker_seen_before && Comms.closest_attacker_dist < 60*Math.sqrt(2)){
				boolean[][] illegal_tiles = new boolean[3][3];
				Pathing.approach(Comms.closest_attacker_loc, illegal_tiles);
//				Pathing.target(Comms.closest_attacker_loc, illegal_tiles, 1);
//				rc.setIndicatorLine(Info.loc, Comms.closest_enemy_loc, 0, 255, 0);
			}
			else if (Comms.enemy_seen_before && Comms.closest_enemy_dist < 60*Math.sqrt(2)){
				boolean[][] illegal_tiles = new boolean[3][3];
				Pathing.approach(Comms.closest_enemy_loc, illegal_tiles);
//				rc.setIndicatorLine(Info.loc, Comms.closest_enemy_loc, 0, 255, 0);
			}
			else {
				Exploration.avoid(Info.friendly_soldiers, Info.n_friendly_soldiers);
			}
			try_to_shoot();
		}
	}
}
