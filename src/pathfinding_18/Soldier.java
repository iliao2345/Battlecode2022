package pathfinding_18;

import battlecode.common.*;

public class Soldier {
	public static final double RUBBLE_ESTIMATE_DECAY = 0.2;
	public static RobotController rc;
	public static double average_rubble = 10;
	
	public static void update() throws GameActionException {
		double added_rubble = 0;
		for (int i=2; --i>=0;) {
			added_rubble += rc.senseRubble(Info.visible_tiles[Info.rng.nextInt(Info.visible_tiles.length)]);
		}
		average_rubble += RUBBLE_ESTIMATE_DECAY*(added_rubble/2-average_rubble);
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
			if (Info.health <= 5) {
				if (rc.senseLead(Info.loc) == 0 && Comms.closest_attacker_dist > 15) {
//				if (rc.senseLead(Info.loc) == 0 && Comms.closest_attacker_dist > 15 && Info.lead < Archon.FARM_SATURATION_CAP) {
					rc.disintegrate();
				}
				if (rc.senseLead(Info.loc) == 0 && Comms.closest_attacker_dist > 9 && Info.n_friendly_miners>0) {
					rc.disintegrate();
				}
				else if (Comms.closest_attacker_dist > 15 && rc.senseNearbyLocationsWithLead(Info.VISION_DIST2).length > Info.visible_tiles.length*0.9) {
					rc.disintegrate();
				}
				else {
					Exploration.momentum_dx = -0.01 * Comms.closest_attacker_dx/Comms.closest_attacker_dist;
					Exploration.momentum_dy = -0.01 * Comms.closest_attacker_dy/Comms.closest_attacker_dist;
					Exploration.repel(Info.enemy_soldiers, Info.n_enemy_soldiers, 1);
					Exploration.travel();
				}
			}
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
				Action.move(Bfs.way_to(Comms.closest_attacker_loc, average_rubble));
//				boolean[][] illegal_tiles = new boolean[3][3];
//				Pathing.approach(Comms.closest_attacker_loc, illegal_tiles);
//				Pathing.target(Comms.closest_attacker_loc, illegal_tiles, 1);
//				rc.setIndicatorLine(Info.loc, Comms.closest_enemy_loc, 0, 255, 0);
			}
			else if (Comms.enemy_seen_before && Comms.closest_enemy_dist < 60*Math.sqrt(2)){
				Action.move(Bfs.way_to(Comms.closest_enemy_loc, average_rubble));
//				boolean[][] illegal_tiles = new boolean[3][3];
//				Pathing.approach(Comms.closest_enemy_loc, illegal_tiles);
//				rc.setIndicatorLine(Info.loc, Comms.closest_enemy_loc, 0, 255, 0);
			}
			else {
				Exploration.repel(Info.friendly_soldiers, Info.n_friendly_soldiers, 1);
				Exploration.travel();
			}
			try_to_shoot();
//			if (!saw_enemy_last_turn) {
//				RobotInfo[] new_robots = rc.senseNearbyRobots(Info.VISION_DIST2, Info.enemy);
//				if (new_robots.length > 0) {
//					for (RobotInfo robot:new_robots) {
//						if (robot.type == RobotType.SOLDIER || robot.type == RobotType.WATCHTOWER || robot.type == RobotType.SAGE) {
//							Comms.closest_attacker_dx = robot.location.x - Info.x;
//							Comms.closest_attacker_dy = robot.location.y - Info.y;
//							Comms.closest_enemy_dx = Comms.closest_attacker_dx;
//							Comms.closest_enemy_dy = Comms.closest_attacker_dy;
//							Comms.enemy_distribution_size++;
//							if (robot.type == RobotType.SOLDIER) {
//								Info.enemy_soldiers[Info.n_enemy_soldiers] = robot;
//								Info.n_enemy_soldiers++;
//							}
//							if (robot.type == RobotType.WATCHTOWER) {
//								Info.enemy_watchtowers[Info.n_enemy_watchtowers] = robot;
//								Info.n_enemy_watchtowers++;
//							}
//							if (robot.type == RobotType.SAGE) {
//								Info.enemy_sages[Info.n_enemy_sages] = robot;
//								Info.n_enemy_sages++;
//							}
//							Info.n_enemy_attackers++;
//							break;
//						}
//					}
//				}
//			}
		}
	}
}
