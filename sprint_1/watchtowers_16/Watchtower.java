package watchtowers_16;

import battlecode.common.*;

public class Watchtower {
	public static RobotController rc;
	public static RobotMode mode;
	
	public static void update() throws GameActionException {
		mode = rc.getMode();
	}
	
	public static void try_to_shoot()  throws GameActionException {
		if (!Info.action_ready) {
			return;
		}
//		if (rc.senseNearbyRobots(Info.ACTION_DIST2, Info.enemy).length == 0) {
//			return;
//		}
		RobotInfo weakest_robot = null;
		int weakest_health = Integer.MAX_VALUE;
		for (int i=Info.n_enemy_soldiers; --i>=0;) {
			if (Info.enemy_soldiers[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, Info.ACTION_DIST2)) {
				weakest_robot = Info.enemy_soldiers[i];
				weakest_health = Info.enemy_soldiers[i].health;
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_watchtowers; --i>=0;) {
				if (Info.enemy_watchtowers[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_watchtowers[i];
					weakest_health = Info.enemy_watchtowers[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_sages; --i>=0;) {
				if (Info.enemy_sages[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_sages[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_sages[i];
					weakest_health = Info.enemy_sages[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_miners; --i>=0;) {
				if (Info.enemy_miners[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_miners[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_miners[i];
					weakest_health = Info.enemy_miners[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_archons; --i>=0;) {
				if (Info.enemy_archons[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_archons[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_archons[i];
					weakest_health = Info.enemy_archons[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_laboratories; --i>=0;) {
				if (Info.enemy_laboratories[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_laboratories[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_laboratories[i];
					weakest_health = Info.enemy_laboratories[i].health;
				}
			}
		}
		if (weakest_robot == null) {
			for (int i=Info.n_enemy_builders; --i>=0;) {
				if (Info.enemy_builders[i].health < weakest_health && Info.loc.isWithinDistanceSquared(Info.enemy_builders[i].location, Info.ACTION_DIST2)) {
					weakest_robot = Info.enemy_builders[i];
					weakest_health = Info.enemy_builders[i].health;
				}
			}
		}
		if (weakest_robot != null) {
			Action.attack(weakest_robot.location);
		}
		else {
			RobotInfo[] shootable_robots = rc.senseNearbyRobots(Info.ACTION_DIST2, Info.enemy);
			if (shootable_robots.length > 0) {
				Action.attack(shootable_robots[0].location);
			}
		}
	}
	
	public static void act() throws GameActionException {
		boolean crowded = Info.n_moveable_tiles < 4;
		boolean in_nomans_land = rc.senseNearbyRobots(Info.ACTION_DIST2, Info.enemy).length > 0;
		Direction to_enemy = Math2.direction_to(Comms.closest_enemy_dy, Comms.closest_enemy_dx);
		boolean[][] walkable = new boolean[3][3];
		int max_dx = Math.min(1, Info.MAP_WIDTH-1-Info.x)+1;
		int min_dx = Math.max(-1, -Info.x);
		int max_dy = Math.min(1, Info.MAP_HEIGHT-1-Info.y)+1;
		int min_dy = Math.max(-1, -Info.y);
		for (int dx = max_dx; --dx >= min_dx;) {
			for (int dy = max_dy; --dy >= min_dy;) {
				MapLocation testloc = Info.loc.translate(dx, dy);
//				walkable[dx+1][dy+1] = !rc.isLocationOccupied(testloc);
				if (!rc.isLocationOccupied(testloc)) {
					walkable[dx+1][dy+1] = true;
				}
				else if (rc.senseRobotAtLocation(testloc).getMode() != RobotMode.TURRET && rc.senseRobotAtLocation(testloc).getMode() != RobotMode.PROTOTYPE) {
					walkable[dx+1][dy+1] = true;
				}
			}
		}
		boolean left_blocking = false;
		boolean right_blocking = false;
		for (Direction dir:Direction.cardinalDirections()) {
			if (!walkable[dir.dx+1][dir.dy+1]) {
				rc.setIndicatorDot(Info.loc.add(dir), 0, 0, 0);
				if (Comms.closest_enemy_dy*dir.dx - Comms.closest_enemy_dx*dir.dy > 0) {
					left_blocking = true;
				}
				else {
					right_blocking = true;
				}
			}
			else {
				rc.setIndicatorDot(Info.loc.add(dir), 255, 255, 255);
			}
		}
		boolean blocking = left_blocking && right_blocking;
		boolean spot_appropriate = !blocking || in_nomans_land && Info.n_friendly_watchtowers < 0.25*Info.visible_tiles.length || rc.senseNearbyRobots(Info.loc, 2, Info.enemy).length > 0;
		boolean can_turret = spot_appropriate;
		boolean can_port = !spot_appropriate && Comms.closest_enemy_dist > 8;
		if (mode == RobotMode.TURRET && (Info.enemy_robots.length == 0 || can_port) && rc.isTransformReady()) {
			rc.transform();
			return;
		}
		if (mode == RobotMode.PORTABLE && Info.enemy_robots.length != 0 && can_turret && rc.isTransformReady()) {
			rc.transform();
			return;
		}
		if (Info.action_ready && mode == RobotMode.TURRET) {
			try_to_shoot();
			return;
		}
		if (Info.move_ready && mode == RobotMode.PORTABLE && Comms.enemy_seen_before) {
			if (Info.n_friendly_soldiers > 2) {
				Exploration.repel(Info.friendly_soldiers, Info.n_friendly_soldiers);
				double force_dist = Math.sqrt(Exploration.force_dx*Exploration.force_dx+Exploration.force_dy*Exploration.force_dy);
				double cross = Comms.closest_enemy_dy*Exploration.force_dx - Comms.closest_enemy_dx*Exploration.force_dy;
				double sign = Math.signum(cross);
				double transverse_dx = -sign*Exploration.force_dy / force_dist;
				double transverse_dy = sign*Exploration.force_dx / force_dist;
				double normal_dx = Exploration.force_dx / force_dist;
				double normal_dy = Exploration.force_dy / force_dist;
				double force_ratio = 0.5;
				double normal_sign = (force_dist>0.5)? 1 : -1;
				Exploration.force_dx = normal_sign*force_ratio*normal_dx + (1-force_ratio)*transverse_dx;
				Exploration.force_dy = normal_sign*force_ratio*normal_dy + (1-force_ratio)*transverse_dy;
			}
			else {
				double normalized_enemy_dx = Comms.closest_enemy_dx / Comms.closest_enemy_dist;
				double normalized_enemy_dy = Comms.closest_enemy_dy / Comms.closest_enemy_dist;
				Exploration.momentum_dx += 0.5*normalized_enemy_dx;
				Exploration.momentum_dy += 0.5*normalized_enemy_dy;
				double momentum_r = Exploration.momentum_dx*Exploration.momentum_dx+Exploration.momentum_dy*Exploration.momentum_dy;
				Exploration.momentum_dx /= momentum_r;
				Exploration.momentum_dy /= momentum_r;
//				if (crowded) {
//					Exploration.repel(Info.friendly_soldiers, Info.n_friendly_soldiers);
//				}
//				Exploration.repel(Info.friendly_watchtowers, Info.n_friendly_watchtowers);
			}
			Exploration.travel();
			return;
		}
	}

}
