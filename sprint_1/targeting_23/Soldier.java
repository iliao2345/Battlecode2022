package targeting_23;

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
//			// This is commented out because it doesn't get used often but takes bytecode, sometimes causing out of bytecode events
//			RobotInfo[] shootable_robots = rc.senseNearbyRobots(Info.ACTION_DIST2, Info.enemy);
//			if (shootable_robots.length > 0) {
//				RobotInfo best_robot = null;
//				int best_health = Integer.MAX_VALUE;
//				for (RobotInfo robot:shootable_robots) {
//					int robot_health = 0;
//					switch (robot.type) {
//					case ARCHON: robot_health = robot.health + 40000; break;
//					case BUILDER: robot_health = robot.health + 60000; break;
//					case LABORATORY: robot_health = robot.health + 50000; break;
//					case MINER: robot_health = robot.health + 30000; break;
//					case SAGE: robot_health = robot.health + 20000; break;
//					case SOLDIER: robot_health = robot.health; break;
//					case WATCHTOWER: robot_health = robot.health + 10000; break;
//					default: break;
//					}
//					if (robot_health < best_health) {
//						best_robot = robot;
//						best_health = robot_health;
//					}
//				}
//				Action.attack(best_robot.location);
//			}
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
				if (rc.senseLead(Info.loc) == 0 && Comms.closest_attacker_dist > 9 && Info.n_friendly_miners>0 && Info.enemy_robots.length == 0) {
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
//			if (Micro.retreat_signal) {
//				return;
////				Exploration.momentum_dx = -0.01 * Comms.closest_attacker_dx/Comms.closest_attacker_dist;
////				Exploration.momentum_dy = -0.01 * Comms.closest_attacker_dy/Comms.closest_attacker_dist;
////				Exploration.repel(Info.enemy_soldiers, Info.n_enemy_soldiers, 1);
////				Exploration.travel();
//			}
			RobotInfo closest_robot = null;
			int closest_dist2 = Integer.MAX_VALUE;
//			for (RobotInfo robot:Info.enemy_robots) {
//				if (Info.loc.isWithinDistanceSquared(robot.location, closest_dist2-1)) {
//					closest_robot = robot;
//					closest_dist2 = Info.loc.distanceSquaredTo(robot.location);
//				}
//			}
			for (int i=Info.n_enemy_soldiers; --i>=0;) {
				if (Info.loc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, closest_dist2-1)) {
					closest_robot = Info.enemy_soldiers[i];
					closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
				}
			}
			if (closest_robot==null) {
				for (int i=Info.n_enemy_watchtowers; --i>=0;) {
					if (Info.loc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, closest_dist2-1)) {
						closest_robot = Info.enemy_watchtowers[i];
						closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
					}
				}
			}
			if (closest_robot==null) {
				for (int i=Info.n_enemy_sages; --i>=0;) {
					if (Info.loc.isWithinDistanceSquared(Info.enemy_sages[i].location, closest_dist2-1)) {
						closest_robot = Info.enemy_sages[i];
						closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
					}
				}
			}
			if (closest_robot==null) {
				for (int i=Info.n_enemy_miners; --i>=0;) {
					if (Info.loc.isWithinDistanceSquared(Info.enemy_miners[i].location, closest_dist2-1)) {
						closest_robot = Info.enemy_miners[i];
						closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
					}
				}
			}
			if (closest_robot==null) {
				for (int i=Info.n_enemy_archons; --i>=0;) {
					if (Info.loc.isWithinDistanceSquared(Info.enemy_archons[i].location, closest_dist2-1)) {
						closest_robot = Info.enemy_archons[i];
						closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
					}
				}
			}
			if (closest_robot==null) {
				for (int i=Info.n_enemy_laboratories; --i>=0;) {
					if (Info.loc.isWithinDistanceSquared(Info.enemy_laboratories[i].location, closest_dist2-1)) {
						closest_robot = Info.enemy_laboratories[i];
						closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
					}
				}
			}
			if (closest_robot==null) {
				for (int i=Info.n_enemy_builders; --i>=0;) {
					if (Info.loc.isWithinDistanceSquared(Info.enemy_builders[i].location, closest_dist2-1)) {
						closest_robot = Info.enemy_builders[i];
						closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
					}
				}
			}
			if (!Info.move_ready) {
			}
			else if (closest_robot != null) {
				boolean[][] illegal_tiles = Micro.illegal_tiles();
//				Pathing.target(closest_robot.location, illegal_tiles, 1);
				Pathing.approach(closest_robot.location, illegal_tiles);
			}
			else if (Micro.outnumbered) {
				Exploration.momentum_dx = -0.01 * Comms.closest_attacker_dx/Comms.closest_attacker_dist;
				Exploration.momentum_dy = -0.01 * Comms.closest_attacker_dy/Comms.closest_attacker_dist;
				Exploration.repel(Info.enemy_soldiers, Info.n_enemy_soldiers, 1);
//				Exploration.repel(Info.enemy_archons, Info.n_enemy_archons, 1);
//				Exploration.repel(Info.enemy_watchtowers, Info.n_enemy_watchtowers, 1);
//				Exploration.repel(Info.enemy_sages, Info.n_enemy_sages, 1);
				Exploration.travel();
			}
			else if (Comms.attacker_seen_before && Comms.closest_attacker_dist < 60*Math.sqrt(2)){
				Action.move(Bfs.way_to(Comms.closest_attacker_loc, Info.average_rubble, Clock.getBytecodesLeft()>2500));
//				boolean[][] illegal_tiles = new boolean[3][3];
//				Pathing.approach(Comms.closest_attacker_loc, illegal_tiles);
//				Pathing.target(Comms.closest_attacker_loc, illegal_tiles, 1);
//				rc.setIndicatorLine(Info.loc, Comms.closest_enemy_loc, 0, 255, 0);
			}
			else if (Comms.enemy_seen_before && Comms.closest_enemy_dist < 60*Math.sqrt(2)){
				Action.move(Bfs.way_to(Comms.closest_enemy_loc, Info.average_rubble, Clock.getBytecodesLeft()>2500));
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
