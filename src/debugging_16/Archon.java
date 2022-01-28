package debugging_16;

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
	public static int health_to_heal = 0;
	public static boolean release_reserved_lead = false;
	public static MapLocation closest_possible_enemy_starting_archon_loc = null;
//	public static boolean initial_mining_phase = true;
	public static boolean enable_sacrifice = false;
	public static boolean far_archon = false;
	public static boolean stop_requesting_lead_units = false;
	public static boolean stop_building_lead_units = false;
	public static boolean make_a_builder = false;
	public static boolean lacking_miners = false;
	public static double enemy_distance_estimate = Double.MAX_VALUE;
	public static double[] employment = new double[20];
	public static double[] global_employment = new double[20];
	public static double[] n_employment = new double[20];
	public static double[] n_global_employment = new double[20];
	public static double average_employment = 0;
	public static double global_average_employment = 0;
	public static double n_average_employment = 0;
	public static double n_global_average_employment = 0;
	
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
	}
	
	public static void post_comms_update() throws GameActionException {
		
		if (Info.round_num >= 3) {
			double closest_possible_enemy_starting_archon_dist = Integer.MAX_VALUE;
			closest_possible_enemy_starting_archon_loc = null;
			for (MapLocation loc:Comms.possible_enemy_archon_locs) {
				if (closest_possible_enemy_starting_archon_dist > Math.sqrt(Info.loc.distanceSquaredTo(loc))) {
					closest_possible_enemy_starting_archon_loc = loc;
				}
				closest_possible_enemy_starting_archon_dist = Math.min(closest_possible_enemy_starting_archon_dist, Math.sqrt(Info.loc.distanceSquaredTo(loc)));
			}
			enemy_distance_estimate = Comms.enemy_seen_before? Comms.closest_enemy_dist : closest_possible_enemy_starting_archon_dist;
		}

		employment[Info.round_num%20] = Comms.archon_mining_miner_count+0.4/rc.getArchonCount();
		global_employment[Info.round_num%20] = Comms.total_mining_miner_count+0.4;
		n_employment[Info.round_num%20] = Comms.archon_miner_count+0.4/rc.getArchonCount();
		n_global_employment[Info.round_num%20] = Comms.total_miner_count+0.4;
		average_employment = 0;
		global_average_employment = 0;
		n_average_employment = 0;
		n_global_average_employment = 0;
		for (int i=Math.min(20, Info.round_num+1); --i>=0;) {
			average_employment += employment[i];
			global_average_employment += global_employment[i];
			n_average_employment += n_employment[i];
			n_global_average_employment += n_global_employment[i];
		}
		double employment_rate = average_employment / n_average_employment;
		double summary_employment_rate = (0.2*global_average_employment+0.8*average_employment) / (0.2*n_global_average_employment+0.8*n_average_employment);
		
		lacking_miners = summary_employment_rate > 0.7 || 5*lead_tiles.length + total_lead > 100*(Info.n_friendly_miners+1);
		rc.setIndicatorString(String.valueOf(summary_employment_rate));
		
		health_to_heal = 0;
		RobotInfo[] healable_robots = rc.senseNearbyRobots(Info.ACTION_DIST2, Info.friendly);
		for (RobotInfo robot:healable_robots) {
			health_to_heal += robot.type.health - robot.health;
		}

		release_reserved_lead = false;

//		make_a_builder = (!Comms.builder_exists || Comms.total_builder_count < 4 && Info.lead > 500) && Info.not_enough_laboratories && Info.round_num >= 3;
		make_a_builder = (!Comms.builder_exists || Comms.total_builder_count < 4 && Info.lead > 220 || Comms.total_builder_count < rc.getRobotCount() / 20 || Comms.total_builder_count < Comms.archon_mining_miner_count / 7) && Info.not_enough_laboratories && Info.round_num >= 3;
		if (!make_a_builder) {
			builder_urgency = 0;
		}
		
		int n_sages = rc.getRobotCount()-rc.getArchonCount()-Comms.laboratory_count-Comms.total_miner_count-Comms.total_soldier_count;
		
		stop_building_lead_units = stop_requesting_lead_units;
		stop_requesting_lead_units = false;
//		rc.setIndicatorString(String.valueOf(Comms.builder_exists));
//		rc.setIndicatorString(String.valueOf(lacking_miners));
		if (lacking_miners) {
			miner_rate = 100*employment_rate;
			soldier_rate = 0;
			builder_rate = 0;
		}
		else if (make_a_builder) {
			soldier_rate = 0;
			miner_rate = 0;
			builder_rate = 100;
			soldier_urgency = 0;
			miner_urgency = 0;
			builder_urgency = 0;
		}
		else {
			stop_requesting_lead_units = true;
		}
//		rc.setIndicatorString(String.valueOf(stop_econ_round));
		
		far_archon = false;
		if (Info.round_num >= 3) {
			MapLocation target_loc = closest_possible_enemy_starting_archon_loc;
			if (Comms.enemy_seen_before) {
				target_loc = Comms.closest_enemy_loc;
			}
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
		
		enable_sacrifice = far_archon && health_to_heal == 0 && !lacking_miners && rc.getRobotCount()-rc.getArchonCount() + (Info.lead + total_lead)/RobotType.MINER.buildCostLead < 10;
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
			
			if (nearby_miners && Info.n_enemy_miners==0 && rc.getArchonCount() > 1 && Comms.laboratory_count == 0) {
				if (!Comms.builder_exists && rc.getMode() != RobotMode.PORTABLE) {
					soldier_rate = 0;
					miner_rate = 0;
					builder_rate = 100;
					soldier_urgency = 0;
					miner_urgency = 0;
					builder_urgency = 0;
					stop_requesting_lead_units = false;
				}
				else {
					rc.disintegrate();
				}
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
			
			if ((best_loc.equals(Info.loc) || Info.n_enemy_attackers > Info.n_friendly_attackers && rc.senseNearbyRobots(Info.loc, EMERGENCY_TURRET_DIST2, Info.enemy).length > 0) && rc.canTransform()) {
				rc.transform();
				return;
			}
//			Action.move(Bfs.way_to(best_loc, 100, true));
			Direction dir = Bfs.direction(Bfs.query(best_loc, 100, 2000, true));
			Action.move(dir);
			return;
		}
		if (rc.getMode() == RobotMode.TURRET && !lacking_miners) {
			
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
				if (!Comms.builder_exists) {
					soldier_rate = 0;
					miner_rate = 0;
					builder_rate = 100;
					soldier_urgency = 0;
					miner_urgency = 0;
					builder_urgency = 0;
					stop_requesting_lead_units = false;
				}
				else {
					rc.transform();
				}
				rc.setIndicatorDot(Info.loc, 255, 255, 0);
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
        if (closest_dir != null) {
        	if (desired_robot != RobotType.BUILDER || make_a_builder) {
                Action.buildRobot(desired_robot, closest_dir);
        	}
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
			communicated_urgency = (1<<12) + (int)miner_urgency;
//			rc.setIndicatorDot(Info.loc, 0, 255, 0);
        }
        if (builder_urgency > miner_urgency && builder_urgency > soldier_urgency) {
			desired_robot = RobotType.BUILDER;
//			communicated_urgency = (1<<12)+(int)builder_urgency;
			communicated_urgency = (int)enemy_distance_estimate;
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
        	else if (desired_robot == RobotType.SOLDIER) {
            	communicated_urgency += 100-(int)Comms.closest_enemy_dist;
        	}
        }
        if (stop_requesting_lead_units) {
        	soldier_urgency = 0;
        	miner_urgency = 0;
        	builder_urgency = 0;
        	communicated_urgency = 0;
        }
		desired_lead = desired_robot.buildCostLead;
//		rc.setIndicatorString(String.valueOf(desired_robot));
	}

}