package spreadtest_1;
import battlecode.common.*;

public class Action {
	public static RobotController rc;

	public static void move(Direction dir) throws GameActionException {
		if (dir!=Direction.CENTER) { 
			Info.last_move_direction = dir;
			rc.move(dir);
			Info.move_ready = rc.isMovementReady();
		}
	}
	public static void buildRobot(RobotType type, Direction dir) throws GameActionException {
		rc.buildRobot(type, dir);
		Info.action_ready = rc.isActionReady();
	}
	public static void mineLead(MapLocation mineLocation) throws GameActionException {
    	rc.setIndicatorLine(Info.loc, mineLocation, 128, 128, 128);
        rc.mineLead(mineLocation);
		Info.action_ready = rc.isActionReady();
	}
	public static void mineGold(MapLocation mineLocation) throws GameActionException {
    	rc.setIndicatorLine(Info.loc, mineLocation, 255, 255, 0);
        rc.mineGold(mineLocation);
		Info.action_ready = rc.isActionReady();
	}
	public static void attack(MapLocation loc) throws GameActionException {
    	rc.setIndicatorLine(Info.loc, loc, 255, 0, 0);
        rc.attack(loc);
		Info.action_ready = rc.isActionReady();
	}
}
