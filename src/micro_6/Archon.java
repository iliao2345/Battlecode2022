package micro_6;

import java.util.Random;

import battlecode.common.*;

public class Archon {
	public static RobotController rc;
	
	public static void update() throws GameActionException {
	}
	
	public static void act() throws GameActionException {
		Direction dir = Math2.UNIT_DIRECTIONS[Info.rng.nextInt(Math2.UNIT_DIRECTIONS.length)];
        if (Info.rng.nextDouble() < 0.3 || !Comms.enemy_seen_before) {
            if (rc.canBuildRobot(RobotType.MINER, dir)) {
                Action.buildRobot(RobotType.MINER, dir);
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
