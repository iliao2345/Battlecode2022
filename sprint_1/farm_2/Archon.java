package farm_2;

import java.util.Random;

import battlecode.common.*;
import spreadtest_1.*;

public class Archon {
	public static RobotController rc;
	
	public static void update() throws GameActionException {
	}
	
	public static void act() throws GameActionException {
    	
		Direction dir = Math2.UNIT_DIRECTIONS[Info.rng.nextInt(Math2.UNIT_DIRECTIONS.length)];
        if (Info.rng.nextDouble() < 0.3 || Info.round_num < 50) {
            if (rc.canBuildRobot(RobotType.MINER, dir)) {
                Action.buildRobot(RobotType.MINER, dir);
            }
        } else {
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            	Action.buildRobot(RobotType.SOLDIER, dir);
            }
        }
	}

}
