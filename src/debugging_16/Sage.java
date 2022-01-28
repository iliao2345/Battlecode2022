package debugging_16;

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
		if (low_hp && Info.n_friendly_attackers < 3*Info.n_enemy_attackers || healing) {
			rc.setIndicatorDot(Info.loc, 255, 0, 0);
			healing = Info.health != RobotType.SAGE.health;
			healing = Info.health <= 48;
			healing = Info.health <= 93;
		}
		advance = (Info.round_num - shot_round) >= 0.6*shot_cooldown && !healing;
//		advance = (Info.round_num - shot_round) >= 0.6*shot_cooldown || Info.n_enemy_attackers == 0 && Info.n_friendly_attackers >= 3;
		rc.setIndicatorString(String.valueOf(advance));
	}
	
	public static void post_comms_update() throws GameActionException {
		double best_delay = Double.MAX_VALUE;
		healing_loc = null;
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
		if (healing_loc==null) {
			for (int i=Comms.archon_positions.length; --i>=0;) {
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

	public static void act() throws GameActionException {
		if (healing || !advance) {
			rc.setIndicatorLine(Info.loc, healing_loc, 255, 0, 0);
			if (!Info.loc.isWithinDistanceSquared(healing_loc, 10) && Info.move_ready) {
//				Direction dir = Bfs.way_to(healing_loc, 100, true);
				Direction dir = Bfs.direction(Bfs.query(healing_loc, 100, 2000, true));
				Action.move(dir);
			}
			else if (Info.enemy_robots.length > 0 && Comms.enemy_seen_before) {
				Micro.sage_micro(Comms.closest_enemy_loc);
			}
			return;
		}
		if (advance) {
			MapLocation target_loc = null;
			if (target_loc == null && Comms.attacker_seen_before) {
				target_loc = Comms.closest_attacker_loc;
			}
			else if (target_loc == null && Comms.enemy_seen_before) {
				target_loc = Comms.closest_enemy_loc;
			}
			else {
				target_loc = new MapLocation(Info.MAP_WIDTH/2, Info.MAP_HEIGHT/2);
			}
//			rc.setIndicatorLine(Info.loc, target_loc, 255, 0, 0);
			Micro.sage_micro(target_loc);
			return;
		}
	}
}
