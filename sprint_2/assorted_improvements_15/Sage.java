package assorted_improvements_15;

import battlecode.common.*;

public class Sage {
	public static RobotController rc;

	public static boolean low_hp = false;
	public static boolean healing = false;
	public static MapLocation healing_loc = null;
	public static int shot_cooldown = 0;
	public static int shot_round = 0;
	public static boolean advance = true;
	
	public static void update() throws GameActionException {
		low_hp = Info.health <= 50;
		if (low_hp || healing) {
			rc.setIndicatorDot(Info.loc, 255, 0, 0);
			healing = Info.health != RobotType.SAGE.health;
		}
		advance = (Info.round_num - shot_round) >= 0.6*shot_cooldown && !healing;
	}
	
	public static void post_comms_update() throws GameActionException {
		double best_delay = Double.MAX_VALUE;
		for (int i=Comms.archon_positions.length; --i>=0;) {
			if (Comms.archons_alive[i]) {
				double travel_delay_1 = (Math.sqrt(Info.loc.distanceSquaredTo(Comms.archon_positions[i]))-Math.sqrt(RobotType.ARCHON.actionRadiusSquared))*RobotType.SAGE.movementCooldown/10.*(1+Info.rubble_quantile/10.);
				double travel_delay_2 = (Math.sqrt(Comms.closest_attacker_loc.distanceSquaredTo(Comms.archon_positions[i]))-Math.sqrt(RobotType.ARCHON.actionRadiusSquared))*RobotType.SAGE.movementCooldown/10.*(1+Info.rubble_quantile/10.);
				double expected_healing_delay = travel_delay_1 + travel_delay_2;
				if (expected_healing_delay < best_delay) {
					best_delay = expected_healing_delay;
					healing_loc = Comms.archon_positions[i];
				}
			}
		}
	}
	
	public static void try_to_shoot()  throws GameActionException {
		if (!Info.action_ready) {
			return;
		}
		RobotInfo weakest_robot = null;
		int weakest_health = Integer.MAX_VALUE;
		int strongest_health = 0;
		for (int i=Info.n_enemy_sages; --i>=0;) {
			if (Info.enemy_sages[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_sages[i].location, Info.ACTION_DIST2)) {
				weakest_robot = Info.enemy_sages[i];
				weakest_health = Info.enemy_sages[i].health;
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_soldiers; --i>=0;) {
				if (Info.enemy_soldiers[i].health > strongest_health && Info.enemy_soldiers[i].health <= 45 && Info.loc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_soldiers[i];
					strongest_health = Info.enemy_soldiers[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_soldiers; --i>=0;) {
				if (Info.enemy_soldiers[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_soldiers[i];
					weakest_health = Info.enemy_soldiers[i].health;
				}
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
			for (int i=Info.n_enemy_miners; --i>=0;) {
				if (Info.enemy_miners[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_miners[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_miners[i];
					weakest_health = Info.enemy_miners[i].health;
				}
			}
		}
		if (weakest_robot != null) {
			Action.attack(weakest_robot.location);
		}
	}

	public static void try_to_move() throws GameActionException {
		if (healing || !advance) {
//			if (Info.n_enemy_attackers > 0) {
//				Exploration.repel(Info.enemy_soldiers, Info.n_enemy_soldiers, 1);
//				Exploration.repel(Info.enemy_watchtowers, Info.n_enemy_watchtowers, 1);
//				Exploration.repel(Info.enemy_sages, Info.n_enemy_sages, 1);
//				Exploration.travel();
//				return;
//			}
			if (!Info.loc.isWithinDistanceSquared(healing_loc, 10)) {
//				Direction dir = Bfs.way_to(healing_loc, 100, true);
				Direction dir = Bfs.direction(Bfs.query(healing_loc, 100, 2000, true));
				Action.move(dir);
			}
			return;
		}

		RobotInfo closest_robot = null;
		int closest_dist2 = Integer.MAX_VALUE;
		for (int i=Info.n_enemy_sages; --i>=0;) {
			if (Info.loc.isWithinDistanceSquared(Info.enemy_sages[i].location, closest_dist2-1)) {
				closest_robot = Info.enemy_sages[i];
				closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
			}
		}
		if (closest_robot==null) {
			for (int i=Info.n_enemy_soldiers; --i>=0;) {
				if (Info.loc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, closest_dist2-1)) {
					closest_robot = Info.enemy_soldiers[i];
					closest_dist2 = Info.loc.distanceSquaredTo(closest_robot.location);
				}
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
		if (advance) {
			MapLocation target_loc = null;
			if (target_loc == null && closest_robot != null) {
				target_loc = closest_robot.location;
			}
			else if (target_loc == null && Comms.attacker_seen_before) {
				target_loc = Comms.closest_attacker_loc;
			}
			else if (target_loc == null && Comms.enemy_seen_before) {
				target_loc = Comms.closest_enemy_loc;
			}
			else {
				target_loc = new MapLocation(Info.MAP_WIDTH/2, Info.MAP_HEIGHT/2);
			}
			rc.setIndicatorLine(Info.loc, target_loc, 255, 0, 0);
			Action.move(Bfs.direction(Bfs.query(target_loc, Info.rubble_quantile, 1, true)));
			return;
		}
	}

	public static void act() throws GameActionException {
		try_to_shoot();
		
		if (Info.move_ready) {
			try_to_move();
			try_to_shoot();
		}
	}
}
