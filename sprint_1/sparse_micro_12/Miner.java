package sparse_micro_12;

import battlecode.common.*;

public class Miner {
	public static RobotController rc;
//	public static MapLocation[] gold_locs;
//	public static MapLocation gold_loc = null;

	public static MapLocation[] lead_locs;
	public static int most_lead = 0;
	public static MapLocation most_lead_loc = null;
	
	public static void update() throws GameActionException {
//		gold_locs = rc.senseNearbyLocationsWithGold(Info.VISION_DIST2);
//		if (gold_locs.length > 0) {
//			gold_loc = gold_locs[0];
//		}
//		else {
//			gold_loc = null;
//		}
		lead_locs = rc.senseNearbyLocationsWithLead(Info.VISION_DIST2);
		most_lead = 0;  // get the location of the largest lead pile visible
		most_lead_loc = null;
		for (MapLocation test_loc:lead_locs) {
			int lead = rc.senseLead(test_loc);
			if (lead > most_lead) {
				most_lead = lead;
				most_lead_loc = test_loc;
			}
		}
	}
	
	public static void try_to_mine()  throws GameActionException {
		while (Info.action_ready) {
//			if (gold_loc != null) {
//				while (rc.senseGold(gold_loc) > 0 && Info.action_ready) {
//					Action.mineGold(gold_loc);
//				}
//				if (!Info.action_ready) {break;}
//				continue;
//			}
			int most_close_lead = 0;
			MapLocation most_close_lead_loc = null;
			for (int dx = Math.min(1, Info.MAP_WIDTH-1-Info.x)+1; --dx >= Math.max(-1, -Info.x);) {
				for (int dy = Math.min(1, Info.MAP_HEIGHT-1-Info.y)+1; --dy >= Math.max(-1, -Info.y);) {
					int test_lead = rc.senseLead(Info.loc.translate(dx, dy));
					if (test_lead > most_close_lead) {
						most_close_lead_loc = Info.loc.translate(dx, dy);
						most_close_lead = test_lead;
					}
				}
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
		if (Info.move_ready) {
			return try_to_move();
		}
		return true;
	}
	
	public static boolean try_to_move() throws GameActionException {
		
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
			return Pathing.target(closest_robot.location, illegal_tiles, -1);
		}
//		if (Comms.closest_attacker_dist < 10 && Comms.attacker_seen_before) {  // flee warzone
//			boolean[][] illegal_tiles = new boolean[3][3];
//			return Pathing.target(Comms.closest_attacker_loc, illegal_tiles, -1);
//		}
		
//		if (gold_loc != null) {  // collect gold
//			boolean[][] illegal_tiles = new boolean[3][3];
////			rc.setIndicatorLine(Info.loc, most_gold_loc, 255, 255, 0);
//			return Pathing.approach(gold_loc, illegal_tiles);
//		}
		if (most_lead > 1) {  // farm lead
			boolean[][] illegal_tiles = new boolean[3][3];
//			rc.setIndicatorLine(Info.loc, most_lead_loc, 128, 128, 128);
			return Pathing.approach(most_lead_loc, illegal_tiles);
		}
//		if (rc.senseLead(Info.loc) == 0 && Info.round_num > 150 && Info.rng.nextDouble() < 0.1 && Info.enemy_robots.length == 0 && Info.n_friendly_miners > 0) {
//			rc.disintegrate();  // make lead farm
//		}
		if (Info.move_ready) {  // explore
			return Exploration.avoid(Info.friendly_miners, Info.n_friendly_miners);
		}
		return true;
	}

}
