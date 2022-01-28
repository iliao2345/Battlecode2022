package sage_micro_14;

import battlecode.common.*;

public class Micro {
	public static RobotController rc;
	public static boolean chase_enemy_econ_units = false;
	public static boolean outnumbered = false;
	public static int retreat_timer = 0;
	public static int max_enemy_seen = 0;
	public static boolean winning_1v1_soldier = false;
	public static boolean losing_1v1_soldier = false;

	public static void update() throws GameActionException {
		
		chase_enemy_econ_units = Info.n_enemy_attackers == 0 && Info.n_enemy_archons == 0;
		losing_1v1_soldier = Info.n_enemy_soldiers >= 1 && Info.n_friendly_soldiers == 0 && Info.enemy_soldiers[0].health > Info.health && Info.n_enemy_archons >= Info.n_friendly_archons;
		winning_1v1_soldier = Info.n_enemy_attackers == 1 && Info.n_enemy_soldiers == 1 && Info.enemy_soldiers[0].health < Info.health && Info.n_enemy_archons <= Info.n_friendly_archons;
		retreat_timer = (Info.n_enemy_attackers > Info.n_friendly_attackers+1 || losing_1v1_soldier)? 2 : Math.max(0, retreat_timer-1);
		outnumbered = retreat_timer > 0;
	}
	
	public static boolean[][] illegal_tiles() throws GameActionException {
//		if (Info.n_enemy_soldiers+Info.n_enemy_watchtowers+Info.n_enemy_sages > 0 && Info.type == RobotType.SOLDIER) {
		if (Info.type != RobotType.SOLDIER) {
			return new boolean[3][3];
		}
		if (Info.enemy_robots.length > 0) {
			boolean[][] illegal_tiles = new boolean[3][3];
			double[][] values;
			if (Info.n_enemy_attackers > 5) {
				values = battle_tile_values_sparse();
			}
			else {
				values = battle_tile_values_sparser();
			}
			double min_value = Double.MAX_VALUE;
			for (int dx=2; --dx>=-1;) {
				for (int dy=2; --dy>=-1;) {
					min_value = Math.min(min_value, values[dx+1][dy+1]);
				}
			}
//			double illegal_threshold = 1;
			double illegal_threshold = min_value;
			for (int dx=2; --dx>=-1;) {
				for (int dy=2; --dy>=-1;) {
					illegal_tiles[dx+1][dy+1] = values[dx+1][dy+1] > illegal_threshold;
				}
			}
			return illegal_tiles;
		}
		else {
			return new boolean[3][3];
		}
	}
	
//	public static double[][] battle_tile_values_dense() throws GameActionException {
//		
//		double[][] values = new double[3][3];
//		for (Direction dir:Direction.allDirections()) {
//			MapLocation testloc = Info.loc.add(dir);
//			if (rc.onTheMap(testloc)) {
//				values[dir.dx+1][dir.dy+1] = rubble_gradient_dx*dir.dx + rubble_gradient_dy*dir.dy;
//				if (battle_ratio > 1 || average_friendly_count > 4*average_enemy_count) {
//					values[dir.dx+1][dir.dy+1] = -(enemy_x-Info.x)*dir.dx - (enemy_y-Info.y)*dir.dy;
//				}
//			}
//			else {
//				values[dir.dx+1][dir.dy+1] = Double.MAX_VALUE;
//			}
//		}
//
//		return values;
//	}
	
	public static double[][] battle_tile_values_sparse() throws GameActionException {
//		int n_enemy_shootable_now = rc.senseNearbyRobots(Info.loc, Info.ACTION_DIST2, Info.enemy).length;
//		boolean enemy_shootable_now = n_enemy_shootable_now > 0;
		boolean enemy_shootable_now = rc.getActionCooldownTurns() > 0;
		double[][] values = new double[3][3];
		for (Direction dir:Direction.allDirections()) {
			MapLocation testloc = Info.loc.add(dir);
			if (rc.onTheMap(testloc) && !rc.isLocationOccupied(testloc)) {
				int n_enemy_shootable = rc.senseNearbyRobots(testloc, Info.ACTION_DIST2, Info.enemy).length;
				int n_enemy_visible = rc.senseNearbyRobots(testloc, Info.VISION_DIST2, Info.enemy).length;
//				if ((enemy_shootable_now || outnumbered) && !chase_miners) {
				if (enemy_shootable_now) {
					values[dir.dx+1][dir.dy+1] = +1000*n_enemy_shootable+n_enemy_visible + 2000*(Math.floor((1+rc.senseRubble(testloc)/10.)*RobotType.SOLDIER.actionCooldown)) + (Comms.attacker_seen_before? -0.0001*(Comms.closest_attacker_dx-dir.dx)*(Comms.closest_attacker_dx-dir.dx)+(Comms.closest_attacker_dy-dir.dy)*(Comms.closest_attacker_dy-dir.dy) : 0);
				}
				else {
					double neg_approx_distance = 0;
					if (Comms.attacker_seen_before) {
						neg_approx_distance = Comms.closest_attacker_dx/Comms.closest_attacker_dist*dir.dx + Comms.closest_attacker_dy/Comms.closest_attacker_dist*dir.dy;
					}
					else if (Comms.enemy_seen_before) {
						neg_approx_distance = Comms.closest_enemy_dx/Comms.closest_enemy_dist*dir.dx + Comms.closest_enemy_dy/Comms.closest_enemy_dist*dir.dy;
					}
					values[dir.dx+1][dir.dy+1] = ((n_enemy_shootable > 0)? n_enemy_shootable : 1000) + neg_approx_distance + 1000*(1+rc.senseRubble(testloc)/10.);
				}
				if (Soldier.healing) {
					boolean within_healing_range = false;
					for (int i=Info.n_friendly_archons; --i>=0;) {
						if (Info.friendly_archons[i].location.isWithinDistanceSquared(Info.loc, RobotType.ARCHON.actionRadiusSquared)) {
							within_healing_range = true;
						}
					}
					values[dir.dx+1][dir.dy+1] += within_healing_range? 0 : 100;
				}
			}
			else {
				values[dir.dx+1][dir.dy+1] = Double.MAX_VALUE;
			}
		}

		return values;
	}
	
	public static double[][] battle_tile_values_sparser() throws GameActionException {
	
		boolean[] soldiers_preoccupied = new boolean[Info.n_enemy_soldiers];
		if (Info.n_friendly_soldiers < 5) {
			for (int i=Info.n_enemy_soldiers; --i>=0;) {
				RobotInfo enemy = Info.enemy_soldiers[i];
				for (int j=Info.n_friendly_soldiers; --j>=0;) {
					RobotInfo friend = Info.friendly_soldiers[j];
					if (friend.location.isWithinDistanceSquared(enemy.location, 20)) {
						soldiers_preoccupied[i] = true;
						break;
					}
				}
			}
		}
		
		boolean enemy_shootable_now = false;
		for (int i=Info.n_enemy_soldiers; --i>=0;) {
			enemy_shootable_now = enemy_shootable_now || Info.loc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, Info.ACTION_DIST2);
		}
		for (int i=Info.n_enemy_watchtowers; --i>=0;) {
			enemy_shootable_now = enemy_shootable_now || Info.loc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, Info.ACTION_DIST2);
		}
		for (int i=Info.n_enemy_sages; --i>=0;) {
			enemy_shootable_now = enemy_shootable_now || Info.loc.isWithinDistanceSquared(Info.enemy_sages[i].location, Info.ACTION_DIST2);
		}
		if (!Info.action_ready && !enemy_shootable_now) {
			double[][] values = new double[3][3];
			values[1][1] = -1;
			return values;
		}
		double[][] values = new double[3][3];
		for (Direction dir:Direction.allDirections()) {
			MapLocation testloc = Info.loc.add(dir);
			if (rc.onTheMap(testloc) && !rc.isLocationOccupied(testloc)) {
				double n_enemy_shootable = rc.senseNearbyRobots(testloc, Info.ACTION_DIST2, Info.enemy).length;
				double n_enemy_visible = rc.senseNearbyRobots(testloc, Info.VISION_DIST2, Info.enemy).length;
				for (int i=Info.n_enemy_soldiers; --i>=0;) {
					if (!soldiers_preoccupied[i]) {
						n_enemy_shootable = n_enemy_shootable + (testloc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, RobotType.SOLDIER.actionRadiusSquared)? 1 : 0);
						n_enemy_visible = n_enemy_visible + (testloc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, RobotType.SOLDIER.actionRadiusSquared)? 1 : 0);
					}
					else {
						n_enemy_shootable = n_enemy_shootable + (testloc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, RobotType.SOLDIER.actionRadiusSquared)? 0.9 : 0);
						n_enemy_visible = n_enemy_visible + (testloc.isWithinDistanceSquared(Info.enemy_soldiers[i].location, RobotType.SOLDIER.actionRadiusSquared)? 0.9 : 0);
					}
				}
				for (int i=Info.n_enemy_watchtowers; --i>=0;) {
					n_enemy_shootable = n_enemy_shootable + (testloc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, RobotType.SOLDIER.actionRadiusSquared)? 1 : 0);
					n_enemy_visible = n_enemy_visible + (testloc.isWithinDistanceSquared(Info.enemy_watchtowers[i].location, RobotType.SOLDIER.visionRadiusSquared)? 1 : 0);
				}
				for (int i=Info.n_enemy_sages; --i>=0;) {
					n_enemy_shootable = n_enemy_shootable + (testloc.isWithinDistanceSquared(Info.enemy_sages[i].location, RobotType.SOLDIER.actionRadiusSquared)? 1 : 0);
					n_enemy_visible = n_enemy_visible + (testloc.isWithinDistanceSquared(Info.enemy_sages[i].location, RobotType.SOLDIER.visionRadiusSquared)? 1 : 0);
				}
				if (enemy_shootable_now) {
					values[dir.dx+1][dir.dy+1] = +1000*n_enemy_shootable+n_enemy_visible + 2000*(Math.floor((1+rc.senseRubble(testloc)/10.)*RobotType.SOLDIER.actionCooldown)) + (Comms.attacker_seen_before? -0.0001*(Comms.closest_attacker_dx-dir.dx)*(Comms.closest_attacker_dx-dir.dx)+(Comms.closest_attacker_dy-dir.dy)*(Comms.closest_attacker_dy-dir.dy) : 0);
				}
				else {
					double neg_approx_distance = 0;
					if (Comms.attacker_seen_before) {
						neg_approx_distance = Comms.closest_attacker_dx/Comms.closest_attacker_dist*dir.dx + Comms.closest_attacker_dy/Comms.closest_attacker_dist*dir.dy;
					}
					else if (Comms.enemy_seen_before) {
						neg_approx_distance = Comms.closest_enemy_dx/Comms.closest_enemy_dist*dir.dx + Comms.closest_enemy_dy/Comms.closest_enemy_dist*dir.dy;
					}
					values[dir.dx+1][dir.dy+1] = ((n_enemy_shootable > 0)? n_enemy_shootable : 1000) + neg_approx_distance + 1000*(1+rc.senseRubble(testloc)/10.);
				}
				if (Soldier.healing) {
					boolean within_healing_range = false;
					for (int i=Info.n_friendly_archons; --i>=0;) {
						if (Info.friendly_archons[i].location.isWithinDistanceSquared(Info.loc, RobotType.ARCHON.actionRadiusSquared)) {
							within_healing_range = true;
						}
					}
					values[dir.dx+1][dir.dy+1] += within_healing_range? 0 : 100;
				}
			}
			else {
				values[dir.dx+1][dir.dy+1] = Double.MAX_VALUE;
			}
		}

		return values;
	}

	public static void sage_micro(MapLocation approx_enemy_loc) throws GameActionException {
		if (!Info.move_ready && !Info.action_ready) {
			return;
		}
		double forward_dx = approx_enemy_loc.x - Info.x + 0.001;
		double forward_dy = approx_enemy_loc.y - Info.y;
		double forward_r = Math.hypot(forward_dx, forward_dy);
		if (!Info.action_ready && Info.move_ready) {
			double home_dx = Sage.healing_loc.x - Info.x + 0.001;
			double home_dy = Sage.healing_loc.y - Info.y;
			double home_r = Math.hypot(home_dx, home_dy);
			double perp_dx = forward_dy * Math.signum(forward_dx*home_dy - forward_dy*home_dx);  // points outwards away from centerline
			double perp_dy = -forward_dx * Math.signum(forward_dx*home_dy - forward_dy*home_dx);
			double perp_r = Math.hypot(perp_dx, perp_dy);
			double perp_amount = 0;
			double home_amount = 0.5;
			double away_amount = 0.5;
			MapLocation retreat_loc = Info.loc.translate((int)(perp_amount*perp_dx/perp_r+home_amount*home_dx/home_r-away_amount*forward_dx/forward_r), (int)(perp_amount*perp_dy/perp_r+home_amount*home_dy/home_r-away_amount*forward_dy/forward_r));
			Action.move(Bfs.direction(Bfs.query(retreat_loc, 100, 2000, true)));
			return;
		}
		RobotInfo[] enemy_robots = rc.senseNearbyRobots(Info.ACTION_DIST2, Info.enemy);
		if (enemy_robots.length >= 6 && Info.move_ready) {
			Direction highest_dir = Direction.CENTER;
			Direction lowest_dir = Direction.CENTER;
			double highest_n_enemies = enemy_robots.length;
			double lowest_n_enemies = enemy_robots.length;
			for (Direction dir:Math2.UNIT_DIRECTIONS) {
				if (rc.canMove(dir)) {
					double num_enemies = rc.senseNearbyRobots(rc.adjacentLocation(dir), Info.ACTION_DIST2, Info.enemy).length + 0.1*(forward_dx*dir.dx + forward_dy*dir.dy)/forward_r;
					if (num_enemies > highest_n_enemies) {
						highest_n_enemies = num_enemies;
						highest_dir = dir;
					}
					if (num_enemies < lowest_n_enemies) {
						lowest_n_enemies = num_enemies;
						lowest_dir = dir;
					}
				}
			}
			if (Math.round(highest_n_enemies) > enemy_robots.length) {
				Action.move(highest_dir);
				Action.envision(AnomalyType.CHARGE);
				return;
			}
			else {
				Action.envision(AnomalyType.CHARGE);
				Action.move(lowest_dir);
				return;
			}
		}
		if (enemy_robots.length >= 7) {
			Action.envision(AnomalyType.CHARGE);
			return;
		}

		boolean[] open = new boolean[9];
		for (int i=9; --i>=0;) {
			open[i] = rc.canMove(Direction.allDirections()[i]) || Direction.allDirections()[i] == Direction.CENTER;
		}
		double[] rubble = new double[9];
		for (int i=9; --i>=0;) {
			rubble[i] = rc.onTheMap(rc.adjacentLocation(Direction.allDirections()[i]))? rc.senseRubble(rc.adjacentLocation(Direction.allDirections()[i])) : 100;
		}
		double[] n_enemies_shootable = new double[9];
		for (int d=9; --d>=0;) {
			Direction dir = Direction.allDirections()[d];
			if (rc.canMove(dir)) {
				n_enemies_shootable[d] = rc.senseNearbyRobots(rc.adjacentLocation(dir), Info.ACTION_DIST2, Info.enemy).length + 0.1*(forward_dx*dir.dx + forward_dy*dir.dy)/forward_r;
			}
		}

		RobotInfo best_robot = null;
		int best_dir = 0;
		double best_score = Double.MAX_VALUE;
		boolean best_shoot_first = false;
		
		for (int i=Info.enemy_robots.length; --i>=0;) {
			RobotInfo robot = Info.enemy_robots[i];
			boolean can_hit = false;
			double min_rubble = 101;
			int min_rubble_dir = 0;
			for (int d=9; --d>=0;) {
				if (rc.adjacentLocation(Direction.allDirections()[d]).isWithinDistanceSquared(robot.location, Info.ACTION_DIST2)) {
					if (rubble[d] < min_rubble && open[d]) {
						can_hit = true;
						min_rubble = rubble[d];
						min_rubble_dir = d;
					}
				}
			}
			if (!can_hit) {
				continue;
			}
			double health_type_score = ((robot.health-1) / 45 + 1) * 45 - robot.health%45;
			switch (robot.type) {
			case SAGE: health_type_score += 0000; break;
			case SOLDIER: health_type_score += 0000; break;
			case WATCHTOWER: health_type_score += 0000; break;
			case LABORATORY: health_type_score += 2000; break;
			case MINER: health_type_score += 2000; break;
			case ARCHON: health_type_score += 2000; break;
			case BUILDER: health_type_score += 2000; break;
			}
			double score = health_type_score + 4000*min_rubble + 0.1*n_enemies_shootable[min_rubble_dir];
			if (score < best_score) {
				best_score = score;
				best_robot = robot;
				best_dir = min_rubble_dir;
				best_shoot_first = false;
			}

			boolean can_hit_now = Info.loc.isWithinDistanceSquared(robot.location, Info.ACTION_DIST2);
			if (can_hit_now) {
				int min_shootables_dir = 0;
				double min_shootables = 0;
				for (int d=9; --d>=0;) {
					if (n_enemies_shootable[d] < min_shootables && open[d]) {
						min_shootables = n_enemies_shootable[d];
						min_shootables_dir = d;
					}
				}
				double score2 = health_type_score + 4000*rubble[8] + 0.1*min_shootables;
				rc.setIndicatorString(String.valueOf(n_enemies_shootable[1]));
				if (score2 < best_score) {
					best_score = score2;
					best_robot = robot;
					best_dir = min_shootables_dir;
					best_shoot_first = true;
				}
			}
		}
		if (best_robot!=null) {
			boolean charge = false;
			boolean fury = false;
			if (best_robot.health <= Math.floor(0.22*best_robot.type.health) && !best_robot.type.isBuilding()) {
				charge = true;
			}
			else if (45 <= Math.floor(0.1*best_robot.type.health) && Info.n_friendly_archons + Info.n_friendly_laboratories + Info.n_friendly_watchtowers == 0 && best_robot.type.isBuilding() && best_robot.mode == RobotMode.TURRET) {
				Action.envision(AnomalyType.FURY);
				fury = true;
			}
			if (best_shoot_first) {
				rc.setIndicatorLine(Info.loc, best_robot.location, 0, 0, 0);
				if (charge) {
					Action.envision(AnomalyType.CHARGE);
				}
				else if (fury) {
					Action.envision(AnomalyType.FURY);
				}
				else {
					Action.attack(best_robot.location);
				}
				Action.move(Direction.allDirections()[best_dir]);
			}
			else {
				Action.move(Direction.allDirections()[best_dir]);
				rc.setIndicatorLine(Info.loc, best_robot.location, 255, 255, 255);
				if (!best_robot.type.canAttack()) {
					for (RobotInfo robot:rc.senseNearbyRobots(Info.VISION_DIST2, Info.enemy)) {
						if (robot.type.canAttack()) {
							return;
						}
					}
				}
				if (charge) {
					Action.envision(AnomalyType.CHARGE);
				}
				else if (fury) {
					Action.envision(AnomalyType.FURY);
				}
				else {
					Action.attack(best_robot.location);
				}
			}
			return;
		}
		else if (best_robot==null && Info.enemy_robots.length == 0) {
			Action.move(Bfs.direction(Bfs.query(approx_enemy_loc, 100, 2000, true)));
			return;
		}
		else {
			double home_dx = Sage.healing_loc.x - Info.x + 0.001;
			double home_dy = Sage.healing_loc.y - Info.y;
			double home_r = Math.hypot(home_dx, home_dy);
			double perp_dx = forward_dy * Math.signum(forward_dx*home_dy - forward_dy*home_dx);  // points outwards away from centerline
			double perp_dy = -forward_dx * Math.signum(forward_dx*home_dy - forward_dy*home_dx);
			double perp_r = Math.hypot(perp_dx, perp_dy);
			double perp_amount = 0.5;
			double home_amount = 0;
			double away_amount = 0.5;
			MapLocation retreat_loc = Info.loc.translate((int)(perp_amount*perp_dx/perp_r+home_amount*home_dx/home_r-away_amount*forward_dx/forward_r), (int)(perp_amount*perp_dy/perp_r+home_amount*home_dy/home_r-away_amount*forward_dy/forward_r));
			Action.move(Bfs.direction(Bfs.query(retreat_loc, 100, 2000, true)));
			return;
		}
	}
}