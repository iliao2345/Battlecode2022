package dynamic_build_order_7;

import battlecode.common.*;

public class Soldier {
	public static RobotController rc;
	
	public static boolean healing = false;
	public static boolean seen_friendly_soldier_before = false;
	public static MapLocation healing_loc = null;
	
	public static void update() throws GameActionException {
		seen_friendly_soldier_before = seen_friendly_soldier_before || Info.n_friendly_soldiers > 0;
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
	
	public static void try_to_move() throws GameActionException {
		if (Info.health <= 11 + 3*Math.max(1, Info.n_enemy_attackers)*Math.max(1, (Math.sqrt(RobotType.SOLDIER.actionRadiusSquared)-Comms.closest_attacker_dist)) || healing) {
			rc.setIndicatorDot(Info.loc, 255, 0, 0);
			if (!healing) {
				int n_archons_alive = 0;
				for (int i=Comms.archon_positions.length; --i>=0;) {
					if (Comms.archons_alive[i]) {
						n_archons_alive++;
						if (Info.rng.nextInt(n_archons_alive)==0) {
							healing_loc = Comms.archon_positions[i];
						}
					}
				}
			}
			healing = Info.health != RobotType.SOLDIER.health;
//			if (Info.n_enemy_attackers > 0) {
//				Exploration.repel(Info.enemy_soldiers, Info.n_enemy_soldiers, 1);
//				Exploration.repel(Info.enemy_watchtowers, Info.n_enemy_watchtowers, 1);
//				Exploration.repel(Info.enemy_sages, Info.n_enemy_sages, 1);
//				Exploration.travel();
//				return;
//			}
			if (Info.n_friendly_archons==0) {
				Direction dir = Bfs.way_to(healing_loc, 100, true);
				Action.move(dir);
				return;
			}
		}
		RobotInfo closest_robot = null;
		int closest_dist2 = Integer.MAX_VALUE;
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
			return;
		}
		else if (Micro.chase_miners && closest_robot != null) {
			rc.setIndicatorDot(Info.loc, 255, 0, 0);
//			boolean[][] illegal_tiles = Micro.illegal_tiles();
////			Pathing.target(closest_robot.location, illegal_tiles, 1);
//			Pathing.approach(closest_robot.location, illegal_tiles);
			Action.move(Bfs.way_to(closest_robot.location, 100, true));
			return;
		}
		else if (closest_robot != null && (!healing || Info.n_enemy_attackers > 0)) {
			rc.setIndicatorDot(Info.loc, 255, 255, 0);
			boolean[][] illegal_tiles = Micro.illegal_tiles();
//			Pathing.target(closest_robot.location, illegal_tiles, 1);
			Pathing.approach(closest_robot.location, illegal_tiles);
			return;
		}
		else if (Micro.outnumbered && !healing) {
			rc.setIndicatorDot(Info.loc, 0, 255, 0);
			Exploration.momentum_dx = -0.01 * Comms.closest_attacker_dx/Comms.closest_attacker_dist;
			Exploration.momentum_dy = -0.01 * Comms.closest_attacker_dy/Comms.closest_attacker_dist;
			Exploration.repel(Info.enemy_soldiers, Info.n_enemy_soldiers, 1);
//			Exploration.repel(Info.enemy_archons, Info.n_enemy_archons, 1);
//			Exploration.repel(Info.enemy_watchtowers, Info.n_enemy_watchtowers, 1);
//			Exploration.repel(Info.enemy_sages, Info.n_enemy_sages, 1);
			Exploration.travel();
			return;
		}
		else if (Comms.attacker_seen_before && Comms.closest_attacker_dist < 60*Math.sqrt(2) && !healing){
			rc.setIndicatorDot(Info.loc, 0, 255, 255);
			Action.move(Bfs.way_to(Comms.closest_attacker_loc, Info.average_rubble, Clock.getBytecodesLeft()>2500));
//			boolean[][] illegal_tiles = new boolean[3][3];
//			Pathing.approach(Comms.closest_attacker_loc, illegal_tiles);
//			Pathing.target(Comms.closest_attacker_loc, illegal_tiles, 1);
//			rc.setIndicatorLine(Info.loc, Comms.closest_enemy_loc, 0, 255, 0);
			return;
		}
		else if (Comms.enemy_seen_before && Comms.closest_enemy_dist < 60*Math.sqrt(2) && !healing){
			rc.setIndicatorDot(Info.loc, 0, 0, 255);
//			rc.setIndicatorLine(Info.loc, Comms.closest_enemy_loc, 0, 0, 255);
			Action.move(Bfs.way_to(Comms.closest_enemy_loc, Info.average_rubble, Clock.getBytecodesLeft()>2500));
//			boolean[][] illegal_tiles = new boolean[3][3];
//			Pathing.approach(Comms.closest_enemy_loc, illegal_tiles);
//			rc.setIndicatorLine(Info.loc, Comms.closest_enemy_loc, 0, 255, 0);
			return;
		}
		else if (!healing && seen_friendly_soldier_before) {
			rc.setIndicatorDot(Info.loc, 255, 0, 255);
			Exploration.repel(Info.friendly_soldiers, Info.n_friendly_soldiers, 1);
			Exploration.travel();
			return;
		}
		else if (!healing) {
			MapLocation target_loc = new MapLocation(Info.MAP_WIDTH/2, Info.MAP_HEIGHT/2);
			Action.move(Bfs.way_to(target_loc, Info.average_rubble, true));
			return;
		}
		return;
	}
	
	public static void act() throws GameActionException {
		try_to_shoot();
		
		if (Info.move_ready) {
			try_to_move();
			try_to_shoot();
		}
	}
}
