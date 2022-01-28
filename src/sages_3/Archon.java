package sages_3;

import java.util.Random;

import battlecode.common.*;

public class Archon {
	public static final double LEAD_DETECTION_DECAY = 0.1;
	public static RobotController rc;
	public static final int SOLDIER_SATURATION_CAP = 400;
	public static final int WATCHTOWER_SATURATION_CAP = 500;
//	public static final int BUILDER_SATURATION_CAP = 600;
	public static final int BUILDER_SATURATION_CAP = Integer.MAX_VALUE;
//	public static final int FARM_SATURATION_CAP = 1000;
	public static final double BEST_MINER_DENSITY = 0.015;
	public static final double CONTROL_DECAY = 1/100.;
	public static final int MIN_ARCHON_WARZONE_SEPARATION_DIST2 = 0;
//	public static final int MIN_ARCHON_WARZONE_SEPARATION_DIST2 = (int)Math.pow(5, 2);
	public static final int MAX_ARCHON_WARZONE_SEPARATION_DIST2 = (int)Math.pow(11, 2);
	public static final int EMERGENCY_TURRET_DIST2 = 5;

	public static MapLocation[] lead_tiles;
	public static MapLocation closest_lead_loc = null;
	public static double average_lead = 0;
	public static double soldier_urgency = 0;
	public static double miner_urgency = 0;
	public static double builder_urgency = 0;
	public static int communicated_urgency = 0;
	public static double soldier_rate = 0;
	public static double miner_rate = 0;
	public static double builder_rate = 0;
	public static RobotType desired_robot = RobotType.MINER;
	public static int desired_lead = RobotType.MINER.buildCostLead;
	public static int lead_needed_before_building = RobotType.MINER.buildCostLead;
	public static int total_lead = 0;
	public static double controlled_tiles = 0;
	public static double controlled_tiles_checked = 0;
	public static double controlled_area = 1;
	public static int health_to_heal = 0;
	public static boolean release_reserved_lead = false;
	public static MapLocation closest_possible_enemy_starting_archon_loc = null;
	public static boolean initial_mining_phase = true;
	public static double initial_mining_delay = 0;
	public static double econ_stopped_round = 2000;
	public static boolean enable_sacrifice = false;
	public static double self_closest_tiles = 0;
	public static double self_closest_tiles_checked = 0;
	public static double self_closest_area = 0;
	public static boolean far_archon = false;
	public static boolean save_lead_for_transmutation = false;
	public static boolean stop_building_lead_units = false;
	public static boolean make_a_builder = false;
	public static int last_builder_made_round = 0;
	
	public static void update() throws GameActionException {
		lead_tiles = rc.senseNearbyLocationsWithLead(Info.VISION_DIST2, 2);
//		most_lead = 0;  // get the location of the largest lead pile visible
//		most_lead_loc = null;
		total_lead = 0;
		if (lead_tiles.length > 0) {
			for (MapLocation tile:lead_tiles) {
				int lead = rc.senseLead(tile);
				average_lead += LEAD_DETECTION_DECAY * (lead - average_lead);
				total_lead += lead-1;
			}
		}
//		for (MapLocation test_loc:lead_tiles) {
//			int lead = rc.senseLead(test_loc);
//			if (lead > most_lead) {
//				most_lead = lead;
//				most_lead_loc = test_loc;
//			}
//		}
		closest_lead_loc = null;
		int closest_dist2 = Integer.MAX_VALUE;
		for (MapLocation test_loc:lead_tiles) {
			if (Info.loc.isWithinDistanceSquared(test_loc, closest_dist2)) {
				closest_dist2 = Info.loc.distanceSquaredTo(test_loc);
				closest_lead_loc = test_loc;
			}
		}
		
		double stop_econ_round = 2000;
		if (Info.round_num == 3) {
			initial_mining_delay = 4*RobotType.MINER.buildCostLead/5./Math.E*Math.log((double)(total_lead+Info.n_friendly_miners*RobotType.MINER.buildCostLead+Info.lead)/(Info.n_friendly_miners*RobotType.MINER.buildCostLead+Info.lead));
		}
		if (Info.round_num >= 3) {
			double closest_possible_enemy_starting_archon_dist = Integer.MAX_VALUE;
			closest_possible_enemy_starting_archon_loc = null;
			for (MapLocation loc:Comms.possible_enemy_archon_locs) {
				if (closest_possible_enemy_starting_archon_dist > Math.sqrt(Info.loc.distanceSquaredTo(loc))) {
					closest_possible_enemy_starting_archon_loc = loc;
				}
				closest_possible_enemy_starting_archon_dist = Math.min(closest_possible_enemy_starting_archon_dist, Math.sqrt(Info.loc.distanceSquaredTo(loc)));
			}
//			if (closest_loc != null) {
//				rc.setIndicatorLine(Info.loc, closest_loc, 255, 0, 0);
//			}
			
//			double stop_econ_round = closest_possible_enemy_starting_archon_dist*(1+Info.average_rubble/10)*RobotType.MINER.movementCooldown;
//			double soldier_transit_time = (closest_possible_enemy_starting_archon_dist/2)*(1+Info.rubble_quantile/10)*RobotType.SOLDIER.movementCooldown/10;
//			double miner_transit_time = (closest_possible_enemy_starting_archon_dist/2-1)*(1+Info.rubble_quantile/10)*RobotType.MINER.movementCooldown/10;
//			stop_econ_round = 1*Math.max(0, miner_transit_time - soldier_transit_time) + initial_mining_delay;
//			rc.setIndicatorString(String.valueOf(stop_econ_round));
		}

//		if (total_lead > Info.n_friendly_miners*RobotType.MINER.buildCostLead) {
//			stop_econ_round = Math.max(Info.round_num + initial_mining_delay, stop_econ_round);
//		}
//		stop_econ_round = Double.MAX_VALUE;
		stop_econ_round = Math.max(Math.min(stop_econ_round, Comms.enemy_seen_round), 0);
		if (stop_econ_round < Info.round_num + 10 && total_lead+Info.lead > RobotType.SOLDIER.buildCostLead) {
			stop_econ_round = Math.min(stop_econ_round, Info.round_num);
		}
		econ_stopped_round = Info.round_num <= stop_econ_round? Info.round_num : econ_stopped_round;
		
		if (Info.round_num >= 3) {
			self_closest_tiles *= 1-CONTROL_DECAY;
			self_closest_tiles_checked *= 1-CONTROL_DECAY;
			MapLocation test_loc = new MapLocation(Info.rng.nextInt(Info.MAP_WIDTH), Info.rng.nextInt(Info.MAP_HEIGHT));
			boolean closest_to_me = true;
			for (int i=Comms.archon_positions.length; --i>=0;) {
				if (Comms.archons_alive[i] && Comms.archon_positions[i].isWithinDistanceSquared(test_loc, test_loc.distanceSquaredTo(Info.loc)) && i != Comms.archon_number) {
					closest_to_me = false;
				}
			}
			self_closest_tiles += closest_to_me? 1 : 0;
			self_closest_tiles_checked++;
			self_closest_area = Info.MAP_WIDTH*Info.MAP_HEIGHT*self_closest_tiles/self_closest_tiles_checked;
		}
		else {
			self_closest_area = Info.visible_tiles.length;
		}
		
		if (Comms.enemy_seen_before) {
			controlled_tiles *= 1-CONTROL_DECAY;
			controlled_tiles_checked *= 1-CONTROL_DECAY;
			MapLocation test_loc = new MapLocation(Info.rng.nextInt(Info.MAP_WIDTH), Info.rng.nextInt(Info.MAP_HEIGHT));
//			controlled_tiles += Info.loc.isWithinDistanceSquared(test_loc, (int)Math.pow(Comms.closest_enemy_dist-Math.sqrt(RobotType.SOLDIER.actionRadiusSquared), 2))? 1 : 0;
			boolean closest_to_me = Math.hypot(test_loc.x-Info.x, test_loc.y-Info.y) < Math.hypot(Comms.closest_enemy_loc.x-Info.x, Comms.closest_enemy_loc.y-Info.y);
//			if (Info.round_num >= 3) {
//				for (int i=Comms.archon_positions.length; --i>=0;) {
//					if (Comms.archons_alive[i] && Comms.archon_positions[i].isWithinDistanceSquared(test_loc, test_loc.distanceSquaredTo(Info.loc)) && i != Comms.archon_number) {
//						closest_to_me = false;
//					}
//				}
//			}
			controlled_tiles += closest_to_me? 1 : 0;
			controlled_tiles_checked++;
			controlled_area = Info.MAP_WIDTH*Info.MAP_HEIGHT*controlled_tiles/controlled_tiles_checked;
		}
		else {
			controlled_area = Info.MAP_WIDTH*Info.MAP_HEIGHT/2;
//			controlled_tiles_checked = 1/CONTROL_DECAY;
//			controlled_tiles = controlled_tiles_checked/2;
		}
		boolean lacking_miners = Comms.total_miner_count < BEST_MINER_DENSITY * controlled_area && Comms.total_miner_count < (rc.getRobotCount()-rc.getArchonCount())/2 || Comms.total_miner_count < 0.4*(rc.getRobotCount()-rc.getArchonCount());

//		rc.setIndicatorString(String.valueOf(controlled_tiles/controlled_tiles_checked));
//		rc.setIndicatorString(String.valueOf(Comms.total_miner_count));
//		rc.setIndicatorString(String.valueOf(Comms.total_miner_count/(BEST_MINER_DENSITY * controlled_area)));
		rc.setIndicatorString(String.valueOf(lacking_miners));
		
		health_to_heal = 0;
		RobotInfo[] healable_robots = rc.senseNearbyRobots(Info.ACTION_DIST2, Info.friendly);
		for (RobotInfo robot:healable_robots) {
			health_to_heal += robot.type.health - robot.health;
		}

//		enable_sacrifice = !initial_mining_phase && rc.getRobotCount()-rc.getArchonCount() < 8;
		enable_sacrifice = !initial_mining_phase && rc.getRobotCount()-rc.getArchonCount() + (Info.lead + total_lead)/RobotType.MINER.buildCostLead < 10;
//		enable_sacrifice = Comms.enemy_seen_before && controlled_area > Info.MAP_WIDTH*Info.MAP_HEIGHT/3 && rc.getRobotCount()-rc.getArchonCount() < 10;
//		rc.setIndicatorString(String.valueOf(enable_sacrifice));
//		rc.setIndicatorString(String.valueOf(BEST_MINER_DENSITY/2 * controlled_area));
		
		release_reserved_lead = false;
		initial_mining_phase = !Comms.enemy_seen_before && Info.round_num < stop_econ_round;
//		boolean send_initial_soldiers = Comms.total_miner_count >= Comms.n_starting_archons*3 && rc.getRobotCount()-rc.getArchonCount()-Comms.total_miner_count < Comms.n_starting_archons;
		boolean send_initial_soldiers = false;

		make_a_builder = false;
		save_lead_for_transmutation = false;
		if (Info.TEST_SAGES) {
			save_lead_for_transmutation = Comms.builder_exists && rc.getRobotCount()-rc.getArchonCount()-Comms.total_miner_count > 10 && Info.lead < 275;
			make_a_builder = !Comms.builder_exists && rc.getRobotCount()-rc.getArchonCount()-Comms.total_miner_count > 10;
			if (!make_a_builder) {
				builder_urgency = 0;
			}
		}
		
		stop_building_lead_units = false;
		rc.setIndicatorString(String.valueOf(Comms.builder_exists));
		if (make_a_builder) {
			soldier_rate = 0;
			miner_rate = 0;
			builder_rate = 100;
		}
//		else if (Info.lead > SOLDIER_SATURATION_CAP) {
//			miner_rate = Math.max(30, Math.min(100, (int)(average_lead)));
//			soldier_rate = 100-miner_rate;
//			builder_rate = 0;
//		}
		else if (initial_mining_phase && !send_initial_soldiers && !save_lead_for_transmutation) {
			soldier_rate = Info.round_num < 3? 0 : 40 - 30*Math.sqrt(Info.loc.distanceSquaredTo(closest_possible_enemy_starting_archon_loc))/60;
			builder_rate = 0;
			miner_rate = (100-soldier_rate)*self_closest_area/(Info.MAP_WIDTH*Info.MAP_HEIGHT);
		}
//		else if (Info.round_num < stop_econ_round*1.3 && !save_lead_for_transmutation) {
//			soldier_rate = 100;
//			miner_rate = 0;
//			builder_rate = 0;
//		}
		else if (Comms.enemy_seen_before && lacking_miners) {
			miner_rate = 100*self_closest_area/(Info.MAP_WIDTH*Info.MAP_HEIGHT);
			soldier_rate = 0;
			builder_rate = 0;
		}
		else if (!save_lead_for_transmutation) {
			miner_rate = 0;
//			miner_rate = Math.min(100, (int)(2*average_lead));
//			miner_rate = Math.min(100, (int)(average_lead));
			soldier_rate = 100-miner_rate;
			builder_rate = 0;
		}
		else {
			stop_building_lead_units = true;
		}
	}
	
	public static void post_comms_update() throws GameActionException {
		far_archon = false;
		if (Info.round_num >= 3) {
			MapLocation target_loc = closest_possible_enemy_starting_archon_loc;
			if (Comms.enemy_seen_before) {
				target_loc = Comms.closest_enemy_loc;
			}  //////////////////////////////////////////////////////////////////// don't set far_archon = true if 
			for (int i=Comms.archon_positions.length; --i>=0;) {
				if (Comms.archons_alive[i] && !Comms.archons_portable[i]) {
					if (Comms.archon_positions[i].isWithinDistanceSquared(target_loc, Info.loc.distanceSquaredTo(target_loc)) && Comms.archon_positions[i].isWithinDistanceSquared(Info.loc, Info.loc.distanceSquaredTo(target_loc))) {
						far_archon = true;
					}
				}
			}
			rc.setIndicatorLine(Info.loc, target_loc, 255, 0, 0);
		}
		far_archon = far_archon && (!Comms.enemy_seen_before || Comms.closest_enemy_dist > 12);
	}
	
	public static void act() throws GameActionException {
		try_to_build();
		try_to_move();
		try_to_heal();
	}
	
	public static void try_to_move() throws GameActionException {
		if (enable_sacrifice) {
			boolean nearby_miners = false;
			for (int i=Info.n_friendly_miners; --i>=0;) {
				MapLocation miner_loc = Info.friendly_miners[i].location;
				if (Info.loc.isWithinDistanceSquared(miner_loc, (int)(0.6*RobotType.MINER.visionRadiusSquared/Math.sqrt((1+rc.senseRubble(miner_loc)/10.)/(1+Info.rubble_quantile/10.))))) {
					nearby_miners = true;
					break;
				}
			}
			
			if (far_archon && health_to_heal==0 && nearby_miners && Info.n_enemy_miners==0 && rc.getArchonCount() > 1) {
				rc.disintegrate();
			}
		}
		
		if (rc.getMode() == RobotMode.PORTABLE) {
			MapLocation best_loc = null;
			if (Comms.closest_enemy_dist < Math.sqrt(MAX_ARCHON_WARZONE_SEPARATION_DIST2)) {
				double lowest_rubble = Double.MAX_VALUE;
				int move_radius_squared = 13;
				MapLocation[] search_locs = rc.getAllLocationsWithinRadiusSquared(Info.loc, move_radius_squared);
				MapLocation target_loc = Comms.enemy_seen_before? Comms.closest_enemy_loc : closest_possible_enemy_starting_archon_loc;
				for (MapLocation loc:search_locs) {
					if (rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc) < lowest_rubble && loc.isWithinDistanceSquared(target_loc, MAX_ARCHON_WARZONE_SEPARATION_DIST2) && !loc.isWithinDistanceSquared(target_loc, MIN_ARCHON_WARZONE_SEPARATION_DIST2) && (!rc.isLocationOccupied(loc) || loc.equals(Info.loc))) {
						best_loc = loc;
						lowest_rubble = rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc);
					}
				}
			}
			if (best_loc==null) {
				if (Comms.enemy_seen_before) {
					best_loc = Comms.closest_enemy_loc;
				}
				else {
					best_loc = closest_possible_enemy_starting_archon_loc;
				}
			}
			
			if ((best_loc.equals(Info.loc) || rc.senseNearbyRobots(Info.loc, EMERGENCY_TURRET_DIST2, Info.enemy).length > 0) && rc.canTransform()) {
				rc.transform();
				return;
			}
//			Action.move(Bfs.way_to(best_loc, 100, true));
			Direction dir = Bfs.direction(Bfs.query(best_loc, 100, 2000, true));
			Action.move(dir);
			return;
		}
		if (rc.getMode() == RobotMode.TURRET && !initial_mining_phase) {
			
			MapLocation best_loc = null;
			double lowest_rubble = Double.MAX_VALUE;
			int move_radius_squared = 13;
			MapLocation[] search_locs = rc.getAllLocationsWithinRadiusSquared(Info.loc, move_radius_squared);
			MapLocation target_loc = Comms.enemy_seen_before? Comms.closest_enemy_loc : closest_possible_enemy_starting_archon_loc;
			for (MapLocation loc:search_locs) {
				if (rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc) < lowest_rubble) {
					best_loc = loc;
					lowest_rubble = rc.senseRubble(loc) + 0.001*Info.loc.distanceSquaredTo(loc);
				}
			}
			
			for (AnomalyScheduleEntry anomaly:rc.getAnomalySchedule()) {
				if (anomaly.anomalyType == AnomalyType.VORTEX && anomaly.roundNumber < Info.round_num+8 && anomaly.roundNumber > Info.round_num && rc.canTransform()) {
					rc.transform();
					return;
				}
			}
			
			boolean enemy_can_reach_while_transforming = 10*(1+Info.rubble/10.) > 2*Comms.closest_attacker_dist*(1+Info.rubble_quantile/10.)*RobotType.SOLDIER.movementCooldown/10.;
			if ((!best_loc.equals(Info.loc) && far_archon && !enemy_can_reach_while_transforming || health_to_heal==0 && far_archon) && rc.isTransformReady()) {
				rc.transform();
				return;
			}
		}
	}
	
	public static void try_to_build() throws GameActionException {
		if (Info.gold >= 20 && Comms.most_urgent_archon && Info.action_ready && rc.getMode()!=RobotMode.PORTABLE) {
			Direction closest_dir = null;
			int closest_dist2 = Integer.MAX_VALUE;
			MapLocation target_loc = (Comms.enemy_seen_before)? Comms.closest_enemy_loc : new MapLocation(Info.MAP_WIDTH/2, Info.MAP_HEIGHT/2);
			for (Direction dir:Math2.UNIT_DIRECTIONS) {
				if (rc.onTheMap(Info.loc.add(dir)) && !rc.isLocationOccupied(Info.loc.add(dir)) && Info.loc.isWithinDistanceSquared(target_loc, closest_dist2)) {
					closest_dist2 = Info.loc.add(dir).distanceSquaredTo(target_loc);
					closest_dir = dir;
				}
			}
			if (closest_dir!=null) {
				Action.buildRobot(RobotType.SAGE, closest_dir);
			}
		}
		if (!Info.action_ready || Info.lead < lead_needed_before_building || Info.lead < desired_robot.buildCostLead || rc.getMode()==RobotMode.PORTABLE || stop_building_lead_units) {
			return;
		}
		Direction closest_dir = null;
		int closest_dist2 = Integer.MAX_VALUE;
		if (desired_robot == RobotType.MINER) {
			MapLocation target_loc = closest_lead_loc==null? new MapLocation(Info.MAP_WIDTH/2, Info.MAP_HEIGHT/2) : closest_lead_loc;
			for (Direction dir:Math2.UNIT_DIRECTIONS) {
				if (rc.onTheMap(Info.loc.add(dir)) && !rc.isLocationOccupied(Info.loc.add(dir)) && Info.loc.isWithinDistanceSquared(target_loc, closest_dist2)) {
					closest_dist2 = Info.loc.add(dir).distanceSquaredTo(target_loc);
					closest_dir = dir;
				}
			}
		}
		else if (desired_robot == RobotType.SOLDIER) {
			MapLocation target_loc = (Comms.enemy_seen_before)? Comms.closest_enemy_loc : new MapLocation(Info.MAP_WIDTH/2, Info.MAP_HEIGHT/2);
			for (Direction dir:Math2.UNIT_DIRECTIONS) {
				if (rc.onTheMap(Info.loc.add(dir)) && !rc.isLocationOccupied(Info.loc.add(dir)) && Info.loc.isWithinDistanceSquared(target_loc, closest_dist2)) {
					closest_dist2 = Info.loc.add(dir).distanceSquaredTo(target_loc);
					closest_dir = dir;
				}
			}
		}
		else {
			int valid_dirs = 0;
			for (Direction dir:Math2.UNIT_DIRECTIONS) {
				if (rc.onTheMap(Info.loc.add(dir)) && !rc.isLocationOccupied(Info.loc.add(dir))) {
					valid_dirs++;
					if (Info.rng.nextInt(valid_dirs)==0) {
						closest_dir = dir;
					}
				}
			}
		}
        if (closest_dir != null && (desired_robot!=RobotType.BUILDER || make_a_builder)) {
            Action.buildRobot(desired_robot, closest_dir);
            return;
        }
	}
    	
    public static void try_to_heal() throws GameActionException {
		if (!Info.action_ready || rc.getMode()==RobotMode.PORTABLE) {
			return;
		}
        if (Comms.attacker_seen_before && Comms.closest_attacker_dist < 10) {
        	RobotInfo[] repairable_robots = rc.senseNearbyRobots(Info.ACTION_DIST2, Info.friendly);
        	RobotInfo weakest_robot = null;
        	int weakest_health = Integer.MAX_VALUE;
        	for (RobotInfo robot:repairable_robots) {
        		if (robot.type == RobotType.SOLDIER && rc.canRepair(robot.location) && robot.health < weakest_health) {
        			weakest_robot = robot;
        			weakest_health = robot.health;
        		}
        	}
        	if (weakest_robot != null) {
            	Action.repair(weakest_robot.location);
            	return;
        	}
        }
        else {
        	RobotInfo[] repairable_robots = rc.senseNearbyRobots(Info.ACTION_DIST2, Info.friendly);
        	RobotInfo strongest_robot = null;
        	int strongest_health = Integer.MIN_VALUE;
        	int n_healing_miners = 0;
        	for (RobotInfo robot:repairable_robots) {
        		if (robot.type == RobotType.MINER && robot.health < RobotType.MINER.health) {
        			n_healing_miners++;
        		}
        	}
        	boolean miners_inactive = n_healing_miners*1.5*rc.getArchonCount() > Comms.total_miner_count;
//        	boolean miners_inactive = false;
        	for (RobotInfo robot:repairable_robots) {
        		if (robot.type == RobotType.SAGE && rc.canRepair(robot.location) && 200+robot.health > strongest_health && robot.health < RobotType.SAGE.health) {
        			strongest_robot = robot;
        			strongest_health = 200+robot.health;
        		}
        		if (robot.type == RobotType.SOLDIER && rc.canRepair(robot.location) && 100+robot.health > strongest_health && robot.health < RobotType.SOLDIER.health) {
        			strongest_robot = robot;
        			strongest_health = 100+robot.health;
        		}
        		if (robot.type == RobotType.MINER && rc.canRepair(robot.location) && (miners_inactive? 200 : 0) + robot.health > strongest_health && robot.health < RobotType.MINER.health) {
        			strongest_robot = robot;
        			strongest_health = (miners_inactive? 200 : 0) + robot.health;
        		}
        	}
        	if (strongest_robot != null) {
            	Action.repair(strongest_robot.location);
            	return;
        	}
        }
	}
	
	public static void decide_what_to_build_next_turn() throws GameActionException {
		soldier_urgency += soldier_rate;  // decide what to build on next turn, send this over comms
		miner_urgency += miner_rate;
		builder_urgency += builder_rate;
//		if (miner_urgency > builder_urgency && miner_urgency > soldier_urgency) {
//			desired_robot = RobotType.MINER;
//			communicated_urgency = (int)miner_urgency;
//			rc.setIndicatorDot(Info.loc, 0, 255, 0);
//        }
//        if (builder_urgency > miner_urgency && builder_urgency > soldier_urgency) {
//			desired_robot = RobotType.BUILDER;
//			communicated_urgency = (int)builder_urgency;
//			rc.setIndicatorDot(Info.loc, 0, 0, 255);
//        }
//        if (soldier_urgency > builder_urgency && soldier_urgency > miner_urgency) {
//			desired_robot = RobotType.SOLDIER;
////			communicated_urgency = (int)soldier_urgency;
//			communicated_urgency = Comms.enemy_seen_before? (1<<12) : (int)soldier_urgency;
//			rc.setIndicatorDot(Info.loc, 255, 0, 0);
//        }
		if (miner_urgency > builder_urgency && miner_urgency > soldier_urgency) {
			desired_robot = RobotType.MINER;
			communicated_urgency = (int)miner_urgency;
//			rc.setIndicatorDot(Info.loc, 0, 255, 0);
        }
        if (builder_urgency > miner_urgency && builder_urgency > soldier_urgency) {
			desired_robot = RobotType.BUILDER;
			communicated_urgency = (1<<12)+(int)builder_urgency;
//			rc.setIndicatorDot(Info.loc, 0, 0, 255);
        }
        if (soldier_urgency > builder_urgency && soldier_urgency > miner_urgency) {
			desired_robot = RobotType.SOLDIER;
			communicated_urgency = (int)soldier_urgency;
//			communicated_urgency = Comms.enemy_seen_before? (1<<12) : (int)soldier_urgency;
//			rc.setIndicatorDot(Info.loc, 255, 0, 0);
        }
        if (Comms.enemy_seen_before) {
        	if (desired_robot == RobotType.BUILDER) {
            	communicated_urgency += (int)Comms.closest_enemy_dist;
        	}
        	communicated_urgency += 100-(int)Comms.closest_enemy_dist;
        }
        if (stop_building_lead_units) {
        	soldier_urgency = 0;
        	miner_urgency = 0;
        	builder_urgency = 0;
        	communicated_urgency = 0;
        }
		desired_lead = desired_robot.buildCostLead;
	}

}