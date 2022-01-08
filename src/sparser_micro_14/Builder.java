package sparser_micro_14;

import battlecode.common.*;

public class Builder {
	public static RobotController rc;
	public static double FARM_ENEMY_REPULSION = 0.1;
	
	public static void update() throws GameActionException {
	}
	
	public static boolean act() throws GameActionException {
		if (rc.senseLead(Info.loc) == 0 && Info.enemy_robots.length == 0 && Info.n_friendly_miners > 0) {
			rc.disintegrate();  // make lead farm
		}
		if (Info.move_ready) {  // explore, but also repel the nearest enemy
			double normalized_enemy_dx = Comms.closest_enemy_dx / Comms.closest_enemy_dist;
			double normalized_enemy_dy = Comms.closest_enemy_dy / Comms.closest_enemy_dist;
			Exploration.momentum_dx -= FARM_ENEMY_REPULSION*normalized_enemy_dx;
			Exploration.momentum_dy -= FARM_ENEMY_REPULSION*normalized_enemy_dy;
			double momentum_r = Exploration.momentum_dx*Exploration.momentum_dx+Exploration.momentum_dy*Exploration.momentum_dy;
			Exploration.momentum_dx /= momentum_r;
			Exploration.momentum_dy /= momentum_r;
			return Exploration.avoid(Info.friendly_builders, Info.n_friendly_builders);
		}
		return true;
	}

}
