package healing_balancing_8;

import java.util.Random;

import battlecode.common.*;

public class Archon {
	public static final double LEAD_DETECTION_DECAY = 0.1;
	public static RobotController rc;
	public static final int SOLDIER_SATURATION_CAP = 400;
	public static final int WATCHTOWER_SATURATION_CAP = 500;
	public static final int BUILDER_SATURATION_CAP = 600;
//	public static final int FARM_SATURATION_CAP = 1000;
	public static final double MIN_MINER_DENSITY = 0.015;
	public static final double MAX_MINER_DENSITY = 0.016;
	public static final double CONTROL_DECAY = 1/100.;

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
	
	public static void update() throws GameActionException {
		lead_tiles = rc.senseNearbyLocationsWithLead(Info.VISION_DIST2);
//		most_lead = 0;  // get the location of the largest lead pile visible
//		most_lead_loc = null;
		total_lead = 0;
		if (lead_tiles.length > 0) {
			for (MapLocation tile:lead_tiles) {
				int lead = rc.senseLead(tile);
				average_lead += LEAD_DETECTION_DECAY * (lead - average_lead);
				total_lead += lead;
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
		
		double closest_possible_enemy_starting_archon_dist = Integer.MAX_VALUE;
		MapLocation closest_loc = null;
		for (MapLocation loc:Comms.possible_enemy_archon_locs) {
			if (closest_possible_enemy_starting_archon_dist > Math.sqrt(Info.loc.distanceSquaredTo(loc))) {
				closest_loc = loc;
			}
			closest_possible_enemy_starting_archon_dist = Math.min(closest_possible_enemy_starting_archon_dist, Math.sqrt(Info.loc.distanceSquaredTo(loc)));
		}
		if (closest_loc != null) {
			rc.setIndicatorLine(Info.loc, closest_loc, 255, 0, 0);
		}

//		double stop_econ_round = closest_possible_enemy_starting_archon_dist*(1+Info.average_rubble/10)*RobotType.MINER.movementCooldown;
		double soldier_transit_time = (closest_possible_enemy_starting_archon_dist/2-Math.sqrt(RobotType.SOLDIER.actionRadiusSquared)/2)*(1+Info.rubble_quantile/10)*RobotType.SOLDIER.movementCooldown/10;
		double miner_transit_time = (closest_possible_enemy_starting_archon_dist/2-1)*(1+Info.rubble_quantile/10)*RobotType.MINER.movementCooldown/10;
		double initial_mining_delay = RobotType.MINER.buildCostLead/5/Math.E*Math.log((total_lead+Info.n_friendly_miners*RobotType.MINER.buildCostLead+Info.lead)/(Info.n_friendly_miners*RobotType.MINER.buildCostLead+Info.lead));
		double stop_econ_round = 1.4*(miner_transit_time - soldier_transit_time) + initial_mining_delay;
		if (Info.round_num < 3) {
			stop_econ_round = 2000;
		}
//		if (total_lead > Info.n_friendly_miners*RobotType.MINER.buildCostLead) {
//			stop_econ_round = Math.max(Info.round_num + initial_mining_delay, stop_econ_round);
//		}
//		stop_econ_round = Double.MAX_VALUE;
		stop_econ_round = Math.max(Math.min(stop_econ_round, Comms.enemy_seen_round), 0);
		
		if (Comms.enemy_seen_before) {
			controlled_tiles *= 1-CONTROL_DECAY;
			controlled_tiles_checked *= 1-CONTROL_DECAY;
			controlled_tiles += Info.loc.isWithinDistanceSquared(new MapLocation(Info.rng.nextInt(Info.MAP_WIDTH), Info.rng.nextInt(Info.MAP_HEIGHT)), (int)Math.pow(Comms.closest_enemy_dist-Math.sqrt(RobotType.SOLDIER.actionRadiusSquared), 2))? 1 : 0;
			controlled_tiles_checked++;
			controlled_area = Info.MAP_WIDTH*Info.MAP_HEIGHT*controlled_tiles/controlled_tiles_checked;
		}
		boolean lacking_miners = Comms.enemy_seen_before && Comms.total_miner_count < MIN_MINER_DENSITY * controlled_area;
		boolean excess_miners = Comms.enemy_seen_before && Comms.total_miner_count > MAX_MINER_DENSITY * controlled_area;

//		rc.setIndicatorString(String.valueOf(controlled_tiles/controlled_tiles_checked));
//		rc.setIndicatorString(String.valueOf(Comms.total_miner_count));
		
		health_to_heal = 0;
		RobotInfo[] healable_robots = rc.senseNearbyRobots(Info.ACTION_DIST2, Info.friendly);
		for (RobotInfo robot:healable_robots) {
			health_to_heal += robot.type.health - robot.health;
		}
		
		if (Info.lead > BUILDER_SATURATION_CAP) {
			soldier_rate = 0;
			miner_rate = 0;
			builder_rate = 100;
		}
//		else if (Info.lead > SOLDIER_SATURATION_CAP) {
//			miner_rate = Math.max(30, Math.min(100, (int)(average_lead)));
//			soldier_rate = 100-miner_rate;
//			builder_rate = 0;
//		}
		else if (!Comms.enemy_seen_before && Info.round_num < stop_econ_round && !excess_miners) {
			soldier_rate = 0;
			builder_rate = 0;
			miner_rate = 100;
		}
		else if (Comms.enemy_seen_before && Info.round_num < stop_econ_round*1.3) {
			soldier_rate = 100;
			miner_rate = 0;
			builder_rate = 0;
		}
		else if (Comms.enemy_seen_before && lacking_miners) {
			miner_rate = 100;
			soldier_rate = 0;
			builder_rate = 0;
		}
		else {
			miner_rate = 0;
//			miner_rate = Math.min(100, (int)(2*average_lead));
//			miner_rate = Math.min(100, (int)(average_lead));
			soldier_rate = 100-miner_rate;
			builder_rate = 0;
		}
	}
	
	public static void act() throws GameActionException {
		if (!Info.action_ready) {
			return;
		}
		Direction closest_dir = null;
		int closest_dist2 = Integer.MAX_VALUE;
		if (desired_robot == RobotType.MINER) {
			for (Direction dir:Math2.UNIT_DIRECTIONS) {
				if (rc.onTheMap(Info.loc.add(dir)) && !rc.isLocationOccupied(Info.loc.add(dir)) && Info.loc.isWithinDistanceSquared(closest_lead_loc, closest_dist2)) {
					closest_dist2 = Info.loc.add(dir).distanceSquaredTo(closest_lead_loc);
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
        if (Info.lead >= lead_needed_before_building && Info.lead >= desired_robot.buildCostLead && closest_dir != null) {
            Action.buildRobot(desired_robot, closest_dir);
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
        	int strongest_health = 0;
        	for (RobotInfo robot:repairable_robots) {
        		if (robot.type == RobotType.SOLDIER && rc.canRepair(robot.location) && 100+robot.health > strongest_health && robot.health < RobotType.SOLDIER.health) {
        			strongest_robot = robot;
        			strongest_health =  100+robot.health;
        		}
        		if (robot.type == RobotType.MINER && rc.canRepair(robot.location) && robot.health > strongest_health && robot.health < RobotType.SOLDIER.health) {
        			strongest_robot = robot;
        			strongest_health = robot.health;
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
		if (miner_urgency > builder_urgency && miner_urgency > soldier_urgency) {
			desired_robot = RobotType.MINER;
			communicated_urgency = (int)miner_urgency;
        }
        if (builder_urgency > miner_urgency && builder_urgency > soldier_urgency) {
			desired_robot = RobotType.BUILDER;
			communicated_urgency = (int)builder_urgency;
        }
        if (soldier_urgency > builder_urgency && soldier_urgency > miner_urgency) {
			desired_robot = RobotType.SOLDIER;
			communicated_urgency = (int)soldier_urgency;
        }
        if (Comms.attacker_seen_before) {
        	communicated_urgency += 100-(int)Comms.closest_attacker_dist;
        }
		desired_lead = desired_robot.buildCostLead;
	}

}