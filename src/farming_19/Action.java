package farming_19;
import battlecode.common.*;

public class Action {
	public static RobotController rc;

	public static void move(Direction dir) throws GameActionException {
		if (dir!=Direction.CENTER) {
			Info.last_move_direction = dir;
			if (Comms.closest_enemy_dx!=dir.dx || Comms.closest_enemy_dy!=dir.dy) {
				Comms.closest_enemy_dx -= dir.dx;
				Comms.closest_enemy_dy -= dir.dy;
				Comms.closest_enemy_dist = Math.sqrt(Comms.closest_enemy_dx*Comms.closest_enemy_dx+Comms.closest_enemy_dy*Comms.closest_enemy_dy);
			}
			if (Comms.closest_attacker_dx!=dir.dx || Comms.closest_attacker_dy!=dir.dy) {
				Comms.closest_attacker_dx -= dir.dx;
				Comms.closest_attacker_dy -= dir.dy;
				Comms.closest_attacker_dist = Math.sqrt(Comms.closest_attacker_dx*Comms.closest_attacker_dx+Comms.closest_attacker_dy*Comms.closest_attacker_dy);
			}
			rc.move(dir);
			Info.loc = Info.loc.add(dir);
			Info.x = Info.loc.x;
			Info.y = Info.loc.y;
			Info.move_ready = rc.isMovementReady();
			Info.last_move_turn = Info.round_num;
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
			Archon.builder_rate = 0;
			if (Archon.builder_rate + Archon.miner_rate + Archon.soldier_rate + Archon.farmer_rate == 0) {
				Archon.stop_requesting_lead_units = true;
			}
			Archon.release_reserved_lead = true;
			Comms.set(30, ((Comms.shared_arr[30]>>1)<<1) + 1);  // builder presence flag
			Comms.set(46, Info.round_num<<1);  // builder suicide indicator
		}
		if (type == RobotType.MINER) {
			Archon.miner_urgency = 0;
			Archon.release_reserved_lead = true;
			for (int i=Math.min(20, Info.round_num+1); --i>=0;) {
				Archon.employment[i] += 0.6;
				Archon.global_employment[i] += 0.6;
				Archon.n_employment[i]++;
				Archon.n_global_employment[i]++;
			}
		}
		if (type == RobotType.SOLDIER) {
			Archon.soldier_urgency = 0;
			Archon.release_reserved_lead = true;
		}
	}
	public static void buildFarmer(Direction dir) throws GameActionException {
		rc.buildRobot(RobotType.BUILDER, dir);
		Info.action_ready = rc.isActionReady();
		Archon.farmer_urgency = 0;
		Archon.release_reserved_lead = true;
		Comms.set(30, ((Comms.shared_arr[30]>>1)<<1) + 1);  // builder presence flag
		if (Comms.shared_arr[46]%2!=0 || Comms.shared_arr[46]>>1 <= Info.round_num - 3) {
			Comms.set(46, (Info.round_num<<1) + 1);  // builder suicide indicator
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
		if (Info.type==RobotType.SAGE) {
			Sage.shot_cooldown = rc.getActionCooldownTurns()/10;
			Sage.shot_round = Info.round_num;
		}
	}
	public static void transmute() throws GameActionException {
//    	rc.setIndicatorLine(Info.loc, loc, 255, 0, 0);
        rc.transmute();
		Info.action_ready = rc.isActionReady();
	}
	public static void envision(AnomalyType type) throws GameActionException {
//    	rc.setIndicatorLine(Info.loc, loc, 255, 0, 0);
        rc.envision(type);
		Info.action_ready = rc.isActionReady();
		Sage.shot_cooldown = rc.getActionCooldownTurns()/10;
		Sage.shot_round = Info.round_num;
	}
}
