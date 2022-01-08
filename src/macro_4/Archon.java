package macro_4;

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
            }
        } else {
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
            	Action.buildRobot(RobotType.SOLDIER, dir);
            }
        }
	}

}
