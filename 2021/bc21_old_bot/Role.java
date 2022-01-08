package bc21_old_bot;
import battlecode.common.*;

public class Role {
	public static RobotController rc;
	public static boolean is_relay_chain = false;
	public static boolean is_guard = false;
	public static boolean is_burier = false;
	public static boolean is_targetter = false;
	public static boolean is_exterminator = false;
	
	public static void attach_to_relay_chain() throws GameActionException {
		if (!is_relay_chain) {
			is_relay_chain = true;
			is_guard = false;
			is_burier = false;
			is_targetter = false;
			is_exterminator = false;
			RelayChain.pressure = 31;
			RelayChain.update();
		}
	}
	
	public static void unassign_all() throws GameActionException {
		is_relay_chain = false;
		is_guard = false;
		is_burier = false;
		is_targetter = false;
		is_exterminator = false;
	}
	
	public static void guard(MapLocation loc) throws GameActionException {
		if (!Role.is_guard) {
			is_relay_chain = false;
			is_guard = true;
			is_burier = false;
			is_targetter = false;
			is_exterminator = false;
			Guard.outpost = loc;
			Guard.reached_outpost = false;
			Guard.update();
		}
	}
	
	public static void bury() throws GameActionException {
		if (!Role.is_burier) {
			RobotInfo target_robot = Info.closest_robot(Info.enemy, RobotType.ENLIGHTENMENT_CENTER);
			if (target_robot==null) {target_robot = Info.closest_robot(Team.NEUTRAL, RobotType.ENLIGHTENMENT_CENTER);}
			if (target_robot!=null) {
				is_relay_chain = false;
				is_guard = false;
				is_burier = true;
				is_targetter = false;
				is_exterminator = false;
				Burier.update();
			}
		}
	}
	
	public static void target(MapLocation loc) throws GameActionException {
		if (!Role.is_targetter) {
			is_relay_chain = false;
			is_guard = false;
			is_burier = false;
			is_targetter = true;
			is_exterminator = false;
			Targetter.target_loc = loc;
			Targetter.update();
		}
	}
	
	public static void exterminate() throws GameActionException {
		if (!Role.is_exterminator) {
			is_relay_chain = false;
			is_guard = false;
			is_burier = false;
			is_targetter = false;
			is_exterminator = true;
			Exterminator.update();
		}
	}
}
