package retreat_20;

import java.util.Random;

import battlecode.common.*;

public class Archon {
	public static final double LEAD_DETECTION_DECAY = 0.1;
	public static RobotController rc;
	public static final int SOLDIER_SATURATION_CAP = 400;
	public static final int WATCHTOWER_SATURATION_CAP = 500;
	public static final int BUILDER_SATURATION_CAP = 600;
//	public static final int FARM_SATURATION_CAP = 1000;

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
	
	public static void update() throws GameActionException {
		lead_tiles = rc.senseNearbyLocationsWithLead(Info.VISION_DIST2);
//		most_lead = 0;  // get the location of the largest lead pile visible
//		most_lead_loc = null;
		if (lead_tiles.length > 0) {
			for (int i=Math.min(lead_tiles.length, 8); --i>=0;) {
				average_lead += LEAD_DETECTION_DECAY * (rc.senseLead(lead_tiles[Info.rng.nextInt(lead_tiles.length)]) - average_lead);
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
		else if (!Comms.enemy_seen_before) {
			soldier_rate = 0;
			builder_rate = 0;
			miner_rate = 100;
		}
		else if (Comms.enemy_seen_before && Info.round_num < Comms.enemy_seen_round*1.3) {
			soldier_rate = 100;
			miner_rate = 0;
			builder_rate = 0;
		}
		else {
			miner_rate = Math.min(100, (int)(2*average_lead));
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
		
    	RobotInfo[] repairable_robots = rc.senseNearbyRobots(Info.ACTION_DIST2, Info.friendly);
    	RobotInfo weakest_robot = null;
    	int weakest_health = Integer.MAX_VALUE;
    	for (RobotInfo robot:repairable_robots) {
    		if (rc.canRepair(robot.location) && robot.health < weakest_health) {
    			weakest_robot = robot;
    			weakest_health = robot.health;
    		}
    	}
    	if (weakest_robot != null) {
        	Action.repair(weakest_robot.location);
        	return;
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
		desired_lead = desired_robot.buildCostLead;
	}

}