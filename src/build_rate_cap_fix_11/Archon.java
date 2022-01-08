package build_rate_cap_fix_11;

import java.util.Random;

import battlecode.common.*;

public class Archon {
	public static RobotController rc;

	public static int lead_tiles = 0;
	public static int lead_excess = 0;
	public static double lead_excess_stddev = 0;
	public static int soldier_timer = 0;
	public static int miner_timer = 0;
	public static int builder_timer = 0;
	public static int max_timer = 0;
	public static int soldier_rate = 0;
	public static int miner_rate = 0;
	public static int builder_rate = 0;
	public static RobotType desired_robot = RobotType.MINER;
	public static int desired_lead = RobotType.MINER.buildCostLead;
	public static int lead_needed_before_building = RobotType.MINER.buildCostLead;
	
	public static void update() throws GameActionException {
		lead_tiles = rc.senseNearbyLocationsWithLead(Info.VISION_DIST2).length;
		lead_excess = lead_tiles - 20*Info.n_friendly_miners;
		lead_excess_stddev = 20*Math.sqrt(Info.n_friendly_miners);

		if (!Comms.enemy_seen_before) {
			soldier_rate = 0;
			builder_rate = 0;
			miner_rate = 30;
		}
		else if (Comms.enemy_seen_before && Info.round_num < Comms.enemy_seen_round*1.3) {
			soldier_rate = 100;
			miner_rate = 0;
			builder_rate = 0;
		}
		else {
			soldier_rate = 70;
			if (miner_rate + builder_rate == 0) {
				miner_rate = 15;
				builder_rate = 15;
			}
			miner_rate = Math.max(0, Math.min(30, miner_rate + (int)(2*(lead_excess / lead_excess_stddev))));
			builder_rate = 30-miner_rate;
		}
	}
	
//	public static void act() throws GameActionException {
//		Direction dir = Math2.UNIT_DIRECTIONS[Info.rng.nextInt(Math2.UNIT_DIRECTIONS.length)];
//        if (Info.rng.nextDouble() < 0.3 || !Comms.enemy_seen_before) {
//            if (rc.canBuildRobot(RobotType.MINER, dir)) {
////            	if (lead_excess < 0 && Comms.enemy_seen_before && Info.round_num > Comms.enemy_seen_round*1.3) {
//            	if (lead_excess < 0 - 2*lead_excess_stddev && Comms.enemy_seen_before && Info.round_num > Comms.enemy_seen_round*1.3) {
//                    Action.buildRobot(RobotType.BUILDER, dir);
//            	}
//            	else {
//            		Action.buildRobot(RobotType.MINER, dir);
//            	}
//            	return;
//            }
//        } else {
//            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
//            	Action.buildRobot(RobotType.SOLDIER, dir);
//            	return;
//            }
//        }
//    	RobotInfo[] repairable_robots = rc.senseNearbyRobots(Info.ACTION_DIST2, Info.friendly);
//    	RobotInfo weakest_robot = null;
//    	int weakest_health = Integer.MAX_VALUE;
//    	for (RobotInfo robot:repairable_robots) {
//    		if (rc.canRepair(robot.location) && robot.health < weakest_health) {
//    			weakest_robot = robot;
//    			weakest_health = robot.health;
//    		}
//    	}
//    	if (weakest_robot != null) {
//        	Action.repair(weakest_robot.location);
//        	return;
//    	}
//	}
	
	public static void act() throws GameActionException {
		if (!Info.action_ready) {
			return;
		}
//		Direction dir = Math2.UNIT_DIRECTIONS[Info.rng.nextInt(Math2.UNIT_DIRECTIONS.length)];
		Direction chosen_dir = null;
		int valid_dirs = 0;
		for (Direction dir:Math2.UNIT_DIRECTIONS) {
			if (rc.onTheMap(Info.loc.add(dir)) && !rc.isLocationOccupied(Info.loc.add(dir))) {
				valid_dirs++;
				if (Info.rng.nextInt(valid_dirs)==0) {
					chosen_dir = dir;
				}
			}
		}
//		rc.setIndicatorString(String.valueOf(chosen_dir));
        if (Info.lead >= lead_needed_before_building && Info.lead >= desired_robot.buildCostLead && chosen_dir != null) {
            Action.buildRobot(desired_robot, chosen_dir);
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
		soldier_timer += soldier_rate;  // decide what to build on next turn, send this over comms
		miner_timer += miner_rate;
		builder_timer += builder_rate;
		if (miner_timer > builder_timer && miner_timer > soldier_timer) {
			desired_robot = RobotType.MINER;
			max_timer = miner_timer;
        }
        if (builder_timer > miner_timer && builder_timer > soldier_timer) {
			desired_robot = RobotType.BUILDER;
			max_timer = builder_timer;
        }
        if (soldier_timer > builder_timer && soldier_timer > miner_timer) {
			desired_robot = RobotType.SOLDIER;
			max_timer = soldier_timer;
        }
		desired_lead = desired_robot.buildCostLead;
	}

}
