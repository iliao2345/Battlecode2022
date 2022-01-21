package original;

import battlecode.common.*;

public class Miner {
//	public static final double LEAD_FORCE_COEFFICIENT = -1/10.;
	public static double WARZONE_BUFFER = 7;
	public static RobotController rc;
//	public static MapLocation[] gold_locs;
//	public static MapLocation gold_loc = null;

	public static MapLocation[] lead_locs;
	public static int most_lead = 0;
	public static MapLocation most_lead_loc = null;
	public static int total_lead = 0;
	public static int self_lead_excess = 0;
	public static int miners_needed_locally = 0;
	
	public static void update() throws GameActionException {
//		gold_locs = rc.senseNearbyLocationsWithGold(Info.VISION_DIST2);
//		if (gold_locs.length > 0) {
//			gold_loc = gold_locs[0];
//		}
//		else {
//			gold_loc = null;
//		}
		lead_locs = rc.senseNearbyLocationsWithLead(Info.loc, Info.VISION_DIST2, 2);
//		if (Info.round_num < 20) {
//			MapLocation closest_lead_loc = null;
//			int closest_dist2 = Integer.MAX_VALUE;
//			for (MapLocation test_loc:lead_locs) {
//				if (Info.loc.isWithinDistanceSquared(test_loc, closest_dist2) && rc.senseLead(test_loc) > 1) {
//					closest_dist2 = Info.loc.distanceSquaredTo(test_loc);
//					closest_lead_loc = test_loc;
//				}
//			}
//			most_lead_loc = closest_lead_loc;
//			most_lead = (closest_lead_loc == null)? 0 : rc.senseLead(most_lead_loc);  // get the location of the largest lead pile visible
//		}
//		else {
			WARZONE_BUFFER = (Info.health > 20)? 5 : 8;
			most_lead = 0;  // get the location of the largest lead pile visible
			most_lead_loc = null;
			total_lead = 0;
			int dist2_buffer = (int) ((1+WARZONE_BUFFER)*(1+WARZONE_BUFFER));
			for (MapLocation test_loc:lead_locs) {
				int lead = rc.senseLead(test_loc);
				total_lead += lead;
				if (lead > most_lead && (!Comms.attacker_seen_before || !test_loc.isWithinDistanceSquared(Comms.closest_attacker_loc, dist2_buffer))) {
					most_lead = lead;
					most_lead_loc = test_loc;
				}
			}
			total_lead -= lead_locs.length;
//		}
		self_lead_excess = Math.max(0, (int)Math.floor((total_lead - 40*Info.n_friendly_miners)/100.));
		miners_needed_locally = Math.max(0, (int)Math.ceil(total_lead/200.));
	}
	
	public static void try_to_mine()  throws GameActionException {
		int max_dx = Math.min(1, Info.MAP_WIDTH-1-Info.x)+1;
		int min_dx = Math.max(-1, -Info.x);
		int max_dy = Math.min(1, Info.MAP_HEIGHT-1-Info.y)+1;
		int min_dy = Math.max(-1, -Info.y);
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
			for (int dx = max_dx; --dx >= min_dx;) {
				for (int dy = max_dy; --dy >= min_dy;) {
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
		try_to_mine();
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
		if (Comms.closest_attacker_dist < WARZONE_BUFFER && Comms.attacker_seen_before) {  // flee warzone
			boolean[][] illegal_tiles = new boolean[3][3];
			return Pathing.target(Comms.closest_attacker_loc, illegal_tiles, -1);
		}
		
//		if (gold_loc != null) {  // collect gold
//			boolean[][] illegal_tiles = new boolean[3][3];
////			rc.setIndicatorLine(Info.loc, most_gold_loc, 255, 255, 0);
//			return Pathing.approach(gold_loc, illegal_tiles);
//		}

		if (most_lead > 1) {  // farm lead
			if (most_lead_loc.isWithinDistanceSquared(Info.loc, 2)) {
				boolean[][] illegal_tiles = new boolean[3][3];
//				return Pathing.approach(most_lead_loc, illegal_tiles);
				rc.setIndicatorLine(Info.loc, most_lead_loc, 0, 0, 0);
				return Pathing.touch(most_lead_loc, illegal_tiles);
			}
			else if (miners_needed_locally > Math.min(Info.n_friendly_miners, rc.senseNearbyRobots(Info.loc.distanceSquaredTo(most_lead_loc)).length)) {
				Pathing.approach(most_lead_loc, new boolean[3][3]);
//				Action.move(Bfs.way_to(most_lead_loc, Info.average_rubble, Clock.getBytecodesLeft() > 3500));
				return true;
			}
		}
		
		if (Info.move_ready) {  // explore
//			if (!Info.encountered_attackers && (Comms.enemy_seen_round > 2000 || Math.sqrt(Info.loc.distanceSquaredTo(Info.spawn_loc)) > 2*(Comms.enemy_seen_round-Info.spawn_round))) {
//			if (!Info.encountered_attackers) {
//			if (!Comms.enemy_seen_before && (Info.spawn_round > 30 || Comms.attacker_seen_before)) {
			if (!Comms.enemy_seen_before || !Info.encountered_enemies && Info.spawn_round < 30) {
				Comms.compute_lead_direction();
				MapLocation lead_loc = Info.loc.translate((int)(Comms.lead_dx / Comms.lead_dist * 1000), (int)(Comms.lead_dy / Comms.lead_dist * 1000));
				rc.setIndicatorLine(Info.loc, Info.loc.translate((int)(Comms.lead_dx / Comms.lead_dist * 3), (int)(Comms.lead_dy / Comms.lead_dist * 3)), 128, 128, 128);
				rc.setIndicatorDot(Info.loc, 128, 128, 128);
				Action.move(Bfs.way_to(lead_loc, Info.average_rubble, false));
				return true;
			}
			else {
				Exploration.repel(Info.friendly_miners, Info.n_friendly_miners, 1);
				return Exploration.travel();
			}
		}
		return true;
	}

}
