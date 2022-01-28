package starting_soldiers_17;

import battlecode.common.*;

public class Miner {
//	public static final double LEAD_FORCE_COEFFICIENT = -1/10.;
	public static double WARZONE_BUFFER = 7;
	public static RobotController rc;
	public static MapLocation[] gold_locs;

	public static MapLocation[] lead_locs;
	public static int most_lead = 0;
	public static MapLocation most_lead_loc = null;
	public static int total_lead = 0;
	public static int self_lead_excess = 0;
	public static boolean healing = false;
	public static MapLocation healing_loc = null;
	public static MapLocation explore_loc = null;
	public static boolean employed = false;
	
	public static void update() throws GameActionException {
		lead_locs = rc.senseNearbyLocationsWithLead(Info.loc, Info.VISION_DIST2, 2);
		gold_locs = rc.senseNearbyLocationsWithGold(Info.loc, Info.VISION_DIST2);
//		if (Info.round_num < 20) {
			total_lead = 0;
			WARZONE_BUFFER = (Info.n_friendly_attackers > 0)? 0 : ((Info.health > 30)? 5 : 8);
			int dist2_buffer = (int) ((1+WARZONE_BUFFER)*(1+WARZONE_BUFFER));
			MapLocation closest_lead_loc = null;
			int closest_dist2 = Integer.MAX_VALUE;
			for (MapLocation test_loc:lead_locs) {
				total_lead += rc.senseLead(test_loc) - 1;
				if (Info.loc.isWithinDistanceSquared(test_loc, closest_dist2) && (!Comms.attacker_seen_before || !test_loc.isWithinDistanceSquared(Comms.closest_attacker_loc, dist2_buffer))) {
					closest_dist2 = Info.loc.distanceSquaredTo(test_loc);
					closest_lead_loc = test_loc;
				}
			}
			most_lead_loc = closest_lead_loc;
			most_lead = (closest_lead_loc == null)? 0 : rc.senseLead(most_lead_loc);  // get the location of the largest lead pile visible
//		}
//		else {
//			WARZONE_BUFFER = (Info.n_friendly_attackers > 0)? 0 : ((Info.health > 30)? 5 : 8);
//			most_lead = 0;  // get the location of the largest lead pile visible
//			most_lead_loc = null;
//			total_lead = 0;
//			int dist2_buffer = (int) ((1+WARZONE_BUFFER)*(1+WARZONE_BUFFER));
//			for (MapLocation test_loc:lead_locs) {
//				int lead = rc.senseLead(test_loc);
//				total_lead += lead;
//				if (lead > most_lead && (!Comms.attacker_seen_before || !test_loc.isWithinDistanceSquared(Comms.closest_attacker_loc, dist2_buffer))) {
//					most_lead = lead;
//					most_lead_loc = test_loc;
//				}
//			}
//			total_lead -= lead_locs.length;
//		}
		int n_friendly_miners_close_to_lead = Info.n_friendly_miners;
		if (Info.n_friendly_miners < 10 && most_lead_loc != null) {
			n_friendly_miners_close_to_lead = 0;
			for (int i=Info.n_friendly_miners; --i>=0;) {
				if (Info.friendly_miners[i].location.isWithinDistanceSquared(most_lead_loc, most_lead_loc.distanceSquaredTo(Info.loc))) {
					n_friendly_miners_close_to_lead++;
				}
			}
		}
		if (Info.enemy_robots.length > 0) {
			n_friendly_miners_close_to_lead = 0;
		}
		self_lead_excess = Math.max(0, (int)Math.ceil((total_lead - 120*n_friendly_miners_close_to_lead)/100.));
	}
	
	public static void try_to_mine()  throws GameActionException {
		if (!Info.action_ready) {
			return;
		}
		int max_dx = Math.min(1, Info.MAP_WIDTH-1-Info.x)+1;
		int min_dx = Math.max(-1, -Info.x);
		int max_dy = Math.min(1, Info.MAP_HEIGHT-1-Info.y)+1;
		int min_dy = Math.max(-1, -Info.y);
		if (gold_locs.length > 0 && Info.loc.isAdjacentTo(gold_locs[0])) {
			while (rc.senseGold(gold_locs[0]) > 0 && Info.action_ready) {
				Action.mineGold(gold_locs[0]);
			}
		}
		while (Info.action_ready) {
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
			int mine_until = (Info.n_enemy_attackers > Info.n_friendly_attackers)? 0 : 1;
			if (most_close_lead_loc != null && rc.senseLead(most_close_lead_loc) > mine_until) {
				while (rc.senseLead(most_close_lead_loc) > mine_until && Info.action_ready) {
					Action.mineLead(most_close_lead_loc);
				}
				if (!Info.action_ready) {break;}
				continue;
			}
			break;
		}
	}
	
	public static void act() throws GameActionException {
		try_to_mine();
		try_to_move();
		if (employed) {
			rc.setIndicatorDot(Info.loc, 0, 255, 0);
		}
		else {
			rc.setIndicatorDot(Info.loc, 255, 0, 0);
		}
		try_to_mine();
		return;
	}
	
	public static void try_to_move() throws GameActionException {
		employed = false;
		
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
			get_new_explore_loc();
			if (Info.move_ready) {
				Pathing.target(closest_robot.location, illegal_tiles, -1);
			}
			employed = self_lead_excess > 0;
			return;
		}
		if (Info.health <= 11 + 3*Info.n_enemy_attackers*(Comms.closest_attacker_dist-Math.sqrt(RobotType.SOLDIER.actionRadiusSquared)) || healing) {
			rc.setIndicatorDot(Info.loc, 255, 0, 0);
			employed = gold_locs.length + lead_locs.length > 0;
			double best_delay = Double.MAX_VALUE;
			for (int i=Comms.archon_positions.length; --i>=0;) {
				if (Comms.archons_alive[i]) {
					double travel_delay_1 = (Math.sqrt(Info.loc.distanceSquaredTo(Comms.archon_positions[i]))-Math.sqrt(RobotType.ARCHON.actionRadiusSquared))*RobotType.MINER.movementCooldown/10.*(1+Info.rubble_quantile/10.);
					double travel_delay_2 = (Math.sqrt(Comms.closest_attacker_loc.distanceSquaredTo(Comms.archon_positions[i]))-Math.sqrt(RobotType.ARCHON.actionRadiusSquared))*RobotType.MINER.movementCooldown/10.*(1+Info.rubble_quantile/10.);
					double expected_healing_delay = travel_delay_1 + travel_delay_2 + Comms.archon_healing_health[i]/2.;
					if (expected_healing_delay < best_delay) {
						best_delay = expected_healing_delay;
						healing_loc = Comms.archon_positions[i];
					}
				}
			}
			healing = Info.health != RobotType.MINER.health;
			get_new_explore_loc();
			if (Info.n_enemy_attackers > 0) {
				if (Info.move_ready) {
					Exploration.repel(Info.enemy_soldiers, Info.n_enemy_soldiers, 1);
					Exploration.repel(Info.enemy_watchtowers, Info.n_enemy_watchtowers, 1);
					Exploration.repel(Info.enemy_sages, Info.n_enemy_sages, 1);
					Exploration.travel();
				}
				return;
			}
			if (!Info.loc.isWithinDistanceSquared(healing_loc, RobotType.ARCHON.actionRadiusSquared)) {
				if (Info.move_ready) {
					Direction dir = Bfs.direction(Bfs.query(healing_loc, 100, 2000, true));
					Action.move(dir);
				}
				return;
			}
		}
		if (Comms.closest_attacker_dist < WARZONE_BUFFER && Comms.attacker_seen_before && !healing) {  // flee warzone
			get_new_explore_loc();
			if (Info.move_ready) {
				boolean[][] illegal_tiles = new boolean[3][3];
				Pathing.target(Comms.closest_attacker_loc, illegal_tiles, -1);
			}
			return;
		}
		
		if (gold_locs.length > 0) {  // collect gold
			MapLocation best_adjacent_loc = null;
			int best_rubble = Integer.MAX_VALUE;
			for (int dx=2; --dx>=-1;) {
				for (int dy=2; --dy>=-1;) {
					if (rc.canSenseLocation(gold_locs[0].translate(dx, dy))) {
						int rubble = rc.senseRubble(gold_locs[0].translate(dx, dy));
						if (rubble < best_rubble || rubble==best_rubble && !gold_locs[0].translate(dx, dy).isWithinDistanceSquared(best_adjacent_loc, dx*dx+dy*dy)) {
							best_rubble = rubble;
							best_adjacent_loc = gold_locs[0].translate(dx, dy);
						}
					}
				}
			}
			employed = true;
			if (Info.move_ready) {
				Action.move(Bfs.direction(Bfs.query(best_adjacent_loc, 100, 20, false)));
			}
			return;
		}
		
//		if ((most_lead > 1 || most_lead_loc!=null && !Info.loc.isWithinDistanceSquared(most_lead_loc, (int)Math.pow((2000-Info.round_num)%40/RobotType.MINER.movementCooldown*10, 2))) && !healing) {  // farm lead
//		rc.setIndicatorString(String.valueOf(self_lead_excess));
		if ((most_lead > 1 || most_lead_loc!=null) && !healing && self_lead_excess > 0) {  // farm lead
//		if ((most_lead > 1 || most_lead_loc!=null) && !healing) {  // farm lead
			MapLocation best_adjacent_loc = null;
			int best_rubble = Integer.MAX_VALUE;
			for (int dx=2; --dx>=-1;) {
				for (int dy=2; --dy>=-1;) {
					if (rc.canSenseLocation(most_lead_loc.translate(dx, dy))) {
						int rubble = rc.senseRubble(most_lead_loc.translate(dx, dy));
						if (rubble < best_rubble || rubble==best_rubble && !most_lead_loc.translate(dx, dy).isWithinDistanceSquared(best_adjacent_loc, dx*dx+dy*dy)) {
							best_rubble = rubble;
							best_adjacent_loc = most_lead_loc.translate(dx, dy);
						}
					}
				}
			}
			rc.setIndicatorLine(Info.loc, best_adjacent_loc, 255, 0, 0);
			employed = true;
			if (Info.move_ready) {
				Action.move(Bfs.direction(Bfs.query(best_adjacent_loc, 100, 20, false)));
			}
			return;
		}
		
//		if (Comms.lab_crowded && Info.loc.isWithinDistanceSquared(Comms.crowded_lab_loc, RobotType.LABORATORY.visionRadiusSquared)) {
//			double dx = Info.x - Comms.crowded_lab_loc.x;
//			double dy = Info.y - Comms.crowded_lab_loc.y;
//			double r = Math.hypot(dx, dy);
//			MapLocation target_loc = Comms.crowded_lab_loc.translate((int)(1000*dx/r), (int)(1000*dy/r));
//			if (Comms.enemy_seen_before) {
//				explore_loc = Comms.closest_enemy_loc;
//				target_loc = Comms.closest_enemy_loc;
//			}
//			else {
//				get_new_explore_loc();
//			}
//			if (Info.move_ready) {
//				Action.move(Bfs.direction(Bfs.query(target_loc, 100, 2000, false)));
//			}
//			return true;
//		}
		
		if (Info.move_ready && !healing) {  // explore
			if (explore_loc == null || Info.loc.isWithinDistanceSquared(explore_loc, 10)) {
				get_new_explore_loc();
			}
			if (explore_loc == null) {
				if (Info.move_ready) {
					Exploration.repel(Info.friendly_miners, Info.n_friendly_miners, 1);
					Exploration.travel();
				}
				return;
			}
//				if (Info.loc.isWithinDistanceSquared(explore_loc, 10)) {
//					explore_loc = new MapLocation(Math.max(0, Math.min(Info.MAP_WIDTH-1, Info.rng.nextInt(Info.MAP_WIDTH+8)-4)), Math.max(0, Math.min(Info.MAP_HEIGHT-1, Info.rng.nextInt(Info.MAP_HEIGHT))));
//				}
//				Action.move(Bfs.way_to(explore_loc, 100, false));
//			rc.setIndicatorLine(Info.loc, explore_loc, 0, 0, 0);
			if (Info.move_ready) {
				Action.move(Bfs.direction(Bfs.query(explore_loc, 100, 2000, true)));
			}
			return;
		}
		return;
	}
	
	public static void get_new_explore_loc() throws GameActionException {
		double explore_x = Info.rng.nextInt(Info.MAP_WIDTH-1);
		double explore_y = Info.rng.nextInt(Info.MAP_HEIGHT-1);
		MapLocation test_loc = new MapLocation((int)explore_x, (int)explore_y);
		boolean closest_to_me = true;
		if (explore_loc == null) {
			switch (Info.rng.nextInt(12)) {
			case 0: test_loc = new MapLocation(3, 3); break;
			case 1: test_loc = new MapLocation(3, Info.MAP_HEIGHT-4); break;
			case 2: test_loc = new MapLocation(Info.MAP_WIDTH-4, 3); break;
			case 3: test_loc = new MapLocation(Info.MAP_WIDTH-4, Info.MAP_HEIGHT-4); break;
			case 4: test_loc = new MapLocation(Info.MAP_WIDTH/2, Info.MAP_HEIGHT/2); break;
			case 5: test_loc = new MapLocation(Info.MAP_WIDTH/2, Info.MAP_HEIGHT/2); break;
			case 6: test_loc = new MapLocation(Info.MAP_WIDTH/2, Info.MAP_HEIGHT/2); break;
			case 7: test_loc = new MapLocation(Info.MAP_WIDTH/2, Info.MAP_HEIGHT/2); break;
			default: break;
			}
			for (int i=Comms.archon_positions.length; --i>=0;) {
				if (Comms.archons_alive[i] && Comms.archon_positions[i].isWithinDistanceSquared(test_loc, test_loc.distanceSquaredTo(Info.spawn_loc)) && !Comms.archon_positions[i].isWithinDistanceSquared(Info.spawn_loc, 20)) {
					closest_to_me = false;
				}
			}
		}
		if (closest_to_me) {
			double mult = Double.POSITIVE_INFINITY;
			if (explore_x > Info.x) {
				mult = Math.min(mult, (Info.MAP_WIDTH-1-Info.x)/(explore_x-Info.x));
			}
			else if (explore_x < Info.x) {
				mult = Math.min(mult, (0-Info.x)/(explore_x-Info.x));
			}
			if (explore_y > Info.y) {
				mult = Math.min(mult, (Info.MAP_HEIGHT-1-Info.y)/(explore_y-Info.y));
			}
			else if (explore_y < Info.y) {
				mult = Math.min(mult, (0-Info.y)/(explore_y-Info.y));
			}
			explore_x = Info.x + mult*(explore_x-Info.x);
			explore_y = Info.y + mult*(explore_y-Info.y);
			explore_loc = new MapLocation(Math.max(0, Math.min(Info.MAP_WIDTH-1, (int)explore_x)), Math.max(0, Math.min(Info.MAP_HEIGHT-1, (int)explore_y)));
		}
	}

}
