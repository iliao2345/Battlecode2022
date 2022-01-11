package spreadtest_1;

import battlecode.common.*;
import spreadtest_1.*;

public class Miner {
	public static RobotController rc;

	
	public static void update() throws GameActionException {
	}
	
	public static boolean act() throws GameActionException {
		boolean trying_to_mine = true;
		while (trying_to_mine) {
			int most_close_gold = 0;  // get the location of the largest gold and lead piles mineable
			MapLocation most_close_gold_loc = null;
			int most_close_lead = 0;
			MapLocation most_close_lead_loc = null;
			for (int dx = Math.min(1, Info.MAP_WIDTH-1-Info.x)+1; --dx >= Math.max(-1, -Info.x);) {
				for (int dy = Math.min(1, Info.MAP_HEIGHT-1-Info.y)+1; --dy >= Math.max(-1, -Info.y);) {
					MapLocation test_loc = Info.loc.translate(dx, dy);
					int gold = rc.senseGold(test_loc);
					int lead = rc.senseLead(test_loc);
					if (gold > most_close_gold) {
						most_close_gold = gold;
						most_close_gold_loc = test_loc;
					}
					if (lead > most_close_lead) {
						most_close_lead = lead;
						most_close_lead_loc = test_loc;
					}
				}
			}
			if (Info.action_ready) {
				if (most_close_gold_loc != null && rc.senseGold(most_close_gold_loc) > 0) {
					Action.mineGold(most_close_gold_loc);
					continue;
				}
				if (most_close_lead_loc != null && rc.senseLead(most_close_lead_loc) > 1) {
					rc.setIndicatorLine(Info.loc, most_close_lead_loc, 255, 0, 0);
					Action.mineLead(most_close_lead_loc);
					continue;
				}
			}
			trying_to_mine = false;
		}
		
		int most_gold = 0;  // get the location of the largest gold and lead piles visible
		MapLocation most_gold_loc = null;
		int most_lead = 0;
		MapLocation most_lead_loc = null;
		for (MapLocation test_loc:rc.getAllLocationsWithinRadiusSquared(Info.loc, Info.VISION_DIST2)) {
			int gold = rc.senseGold(test_loc);
			int lead = rc.senseLead(test_loc);
			if (gold > most_gold) {
				most_gold = gold;
				most_gold_loc = test_loc;
			}
			if (lead > most_lead) {
				most_lead = lead;
				most_lead_loc = test_loc;
			}
		}
		if (most_gold > 0) {
			boolean[][] illegal_tiles = new boolean[3][3];
//			rc.setIndicatorLine(Info.loc, most_gold_loc, 255, 255, 0);
			return Pathing.approach(most_gold_loc, illegal_tiles);
		}
		else if (most_lead > 1) {
			boolean[][] illegal_tiles = new boolean[3][3];
//			rc.setIndicatorLine(Info.loc, most_lead_loc, 128, 128, 128);
			return Pathing.approach(most_lead_loc, illegal_tiles);
		}
		else {
			return Exploration.avoid(Info.friendly_miners, Info.n_friendly_miners);
		}
	}

}
