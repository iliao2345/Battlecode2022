package retreat_20;
import battlecode.common.*;

public class Action {
	public static RobotController rc;

	public static void move(Direction dir) throws GameActionException {
		if (dir!=Direction.CENTER) {
			Info.last_move_direction = dir;
			Comms.closest_enemy_dx -= dir.dx;
			Comms.closest_enemy_dy -= dir.dy;
			Comms.closest_enemy_dist = Math.sqrt(Comms.closest_enemy_dx*Comms.closest_enemy_dx+Comms.closest_enemy_dy*Comms.closest_enemy_dy);
			Comms.closest_attacker_dx -= dir.dx;
			Comms.closest_attacker_dy -= dir.dy;
			Comms.closest_attacker_dist = Math.sqrt(Comms.closest_attacker_dx*Comms.closest_attacker_dx+Comms.closest_attacker_dy*Comms.closest_attacker_dy);
			rc.move(dir);
			Info.loc = Info.loc.add(dir);
			Info.x = Info.loc.x;
			Info.y = Info.loc.y;
			Info.move_ready = rc.isMovementReady();
		}
	}
	public static void repair(MapLocation loc) throws GameActionException {
		rc.repair(loc);
		Info.action_ready = rc.isActionReady();
	}
	public static void buildRobot(RobotType type, Direction dir) throws GameActionException {
		rc.buildRobot(type, dir);
		Info.action_ready = rc.isActionReady();
		if (type == RobotType.BUILDER) {
			Archon.builder_urgency = 0;
		}
		if (type == RobotType.MINER) {
			Archon.miner_urgency = 0;
		}
		if (type == RobotType.SOLDIER) {
			Archon.soldier_urgency = 0;
		}
	}
	public static void mineLead(MapLocation mineLocation) throws GameActionException {
//    	rc.setIndicatorLine(Info.loc, mineLocation, 128, 128, 128);
        rc.mineLead(mineLocation);
		Info.action_ready = rc.isActionReady();
	}
	public static void mineGold(MapLocation mineLocation) throws GameActionException {
//    	rc.setIndicatorLine(Info.loc, mineLocation, 255, 255, 0);
        rc.mineGold(mineLocation);
		Info.action_ready = rc.isActionReady();
	}
	public static void attack(MapLocation loc) throws GameActionException {
//    	rc.setIndicatorLine(Info.loc, loc, 255, 0, 0);
        rc.attack(loc);
		Info.action_ready = rc.isActionReady();
	}
}
