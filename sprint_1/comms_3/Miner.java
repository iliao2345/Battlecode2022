package comms_3;

import battlecode.common.*;

public class Miner {
	public static RobotController rc;

	
	public static void update() throws GameActionException {
	}
	
	public static void try_to_mine()  throws GameActionException {
		while (Info.action_ready) {
			MapLocation gold_loc = null;
			int most_close_lead = 0;
			MapLocation most_close_lead_loc = null;
			for (int dx = Math.min(1, Info.MAP_WIDTH-1-Info.x)+1; --dx >= Math.max(-1, -Info.x);) {
				for (int dy = Math.min(1, Info.MAP_HEIGHT-1-Info.y)+1; --dy >= Math.max(-1, -Info.y);) {
					MapLocation test_loc = Info.loc.translate(dx, dy);
					int lead = rc.senseLead(test_loc);
					if (rc.senseGold(test_loc) > 0) {
						gold_loc = test_loc;
					}
					if (lead > most_close_lead) {
						most_close_lead = lead;
						most_close_lead_loc = test_loc;
					}
				}
			}
			if (gold_loc != null) {
				while (rc.senseGold(gold_loc) > 0 && Info.action_ready) {
					Action.mineGold(gold_loc);
				}
				if (!Info.action_ready) {break;}
				continue;
			}
			if (most_close_lead_loc != null && rc.senseLead(most_close_lead_loc) > 1) {
				while (rc.senseLead(most_close_lead_loc) > 1 && Info.action_ready) {
					Action.mineLead(most_close_lead_loc);
				}
				if (!Info.action_ready) {break;}
				continue;
			}
			break;
		}
	}
	
	public static boolean act() throws GameActionException {
		try_to_mine();
		
		MapLocation gold_loc = null;
		int most_lead = 0;  // get the location of gold and the largest lead pile visible
		MapLocation most_lead_loc = null;
		for (MapLocation test_loc:rc.getAllLocationsWithinRadiusSquared(Info.loc, Info.VISION_DIST2)) {
			int lead = rc.senseLead(test_loc);
			if (rc.senseGold(test_loc) > 0) {
				gold_loc = test_loc;
			}
			if (lead > most_lead) {
				most_lead = lead;
				most_lead_loc = test_loc;
			}
		}
		if (gold_loc != null) {
			boolean[][] illegal_tiles = new boolean[3][3];
//			rc.setIndicatorLine(Info.loc, most_gold_loc, 255, 255, 0);
			if (Info.move_ready) {
				return Pathing.approach(gold_loc, illegal_tiles);
			}
		}
		else if (most_lead > 1) {
			boolean[][] illegal_tiles = new boolean[3][3];
//			rc.setIndicatorLine(Info.loc, most_lead_loc, 128, 128, 128);
			if (Info.move_ready) {
				return Pathing.approach(most_lead_loc, illegal_tiles);
			}
		}
		else {
			if (rc.senseLead(Info.loc) == 0 && Info.round_num > 150 && Info.rng.nextDouble() < 0.1 && Info.enemy_robots.length == 0) {
				rc.disintegrate();
			}
			if (Info.enemy_robots.length > 0) {
				RobotInfo closest_robot = null;
				int closest_dist2 = Integer.MAX_VALUE;
				for (RobotInfo robot:Info.enemy_robots) {
					if (Info.loc.isWithinDistanceSquared(robot.location, closest_dist2-1)) {
						closest_robot = robot;
						closest_dist2 = Info.loc.distanceSquaredTo(robot.location);
					}
				}
				if (closest_robot != null) {
					boolean[][] illegal_tiles = new boolean[3][3];
					if (Info.move_ready) {
						Pathing.target(closest_robot.location, illegal_tiles, -1);
					}
				}
			}
			if (Info.move_ready) {
				return Exploration.avoid(Info.friendly_miners, Info.n_friendly_miners);
			}
		}
		return true;
	}

}
