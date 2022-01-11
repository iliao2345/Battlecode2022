package lead_search_fix_8;

import java.util.Random;

import battlecode.common.*;

public class Archon {
	public static RobotController rc;

	public static int lead_tiles = 0;
	public static int lead_excess = 0;
	public static double lead_excess_stddev = 0;
	
	public static void update() throws GameActionException {
		lead_tiles = rc.senseNearbyLocationsWithLead(Info.VISION_DIST2).length;
		lead_excess = lead_tiles - 20*Info.n_friendly_miners;
		lead_excess_stddev = 20*Math.sqrt(Info.n_friendly_miners);
	}
	
	public static void act() throws GameActionException {
		Direction dir = Math2.UNIT_DIRECTIONS[Info.rng.nextInt(Math2.UNIT_DIRECTIONS.length)];
        if (Info.rng.nextDouble() < 0.3 || !Comms.enemy_seen_before) {
            if (rc.canBuildRobot(RobotType.MINER, dir)) {
//            	if (lead_excess < 0 && Comms.enemy_seen_before && Info.round_num > Comms.enemy_seen_round*1.3) {
            	if (lead_excess < 0 - 2*lead_excess_stddev && Comms.enemy_seen_before && Info.round_num > Comms.enemy_seen_round*1.3) {
                    Action.buildRobot(RobotType.BUILDER, dir);
            	}
            	else {
            		Action.buildRobot(RobotType.MINER, dir);
            	}
            	return;
            }
        } else {
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            	Action.buildRobot(RobotType.SOLDIER, dir);
            	return;
            }
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

}
