package bug_fixes_18;
import java.util.Random;

import battlecode.common.*;

public class Info {
    public static Random rng = new Random(6147);
	public static RobotController rc;
	public static Team friendly;
	public static Team enemy;
	public static int id;
	public static int spawn_round;
	public static MapLocation spawn_loc;
	public static int base_move_cooldown;
	public static int MIN_MOVE_COOLDOWN;
	public static int VISION_DIST2;
	public static int ACTION_DIST2;
	public static int MAP_WIDTH;
	public static int MAP_HEIGHT;
	public static final double RUBBLE_ESTIMATE_DECAY = 0.2;
	public static final double PATHING_PERCOLATION_CONSTANT = 0.4;  // I don't know what the actual value is, this is a guess

	public static boolean action_ready;
	public static boolean move_ready;
	public static MapLocation loc;
	public static int x;
	public static int y;
	public static RobotType type;
	public static int round_num;
	public static Direction last_move_direction = Direction.EAST;
	public static RobotInfo[] friendly_robots;
	public static int n_friendly_archons;
	public static int n_friendly_builders;
	public static int n_friendly_laboratories;
	public static int n_friendly_miners;
	public static int n_friendly_sages;
	public static int n_friendly_soldiers;
	public static int n_friendly_watchtowers;
	public static RobotInfo[] friendly_archons;
	public static RobotInfo[] friendly_builders;
	public static RobotInfo[] friendly_laboratories;
	public static RobotInfo[] friendly_miners;
	public static RobotInfo[] friendly_sages;
	public static RobotInfo[] friendly_soldiers;
	public static RobotInfo[] friendly_watchtowers;
	public static RobotInfo[] enemy_robots;
	public static int n_enemy_archons;
	public static int n_enemy_builders;
	public static int n_enemy_laboratories;
	public static int n_enemy_miners;
	public static int n_enemy_sages;
	public static int n_enemy_soldiers;
	public static int n_enemy_watchtowers;
	public static RobotInfo[] enemy_archons;
	public static RobotInfo[] enemy_builders;
	public static RobotInfo[] enemy_laboratories;
	public static RobotInfo[] enemy_miners;
	public static RobotInfo[] enemy_sages;
	public static RobotInfo[] enemy_soldiers;
	public static RobotInfo[] enemy_watchtowers;
	public static int total_friendly_robots;
	public static int n_enemy_attackers;
	public static int n_friendly_attackers;
	public static int lead;
	public static int gold;
	public static int rubble;
	public static MapLocation[] visible_tiles;
	public static int health;
	public static boolean[][] moveable_tiles;
	public static int n_moveable_tiles;
	public static double average_rubble = 10;
	public static boolean encountered_attackers = false;
	public static boolean encountered_enemies = false;
	public static double rubble_quantile = 0;
	public static int last_move_turn = 0;
	public static double income_1 = 0;
	public static double income_t = 0;
	public static double income_i = 0;
	public static double income_tt = 0;
	public static double income_ti = 0;
	public static double income_didt = 0;
	public static double income_decay = 0.5;
	public static double income_estimate = 0;
	public static int lead_last_turn = 0;
//	public static double units_per_lab = 10;
	public static boolean not_enough_laboratories = false;
	public static int spawn_archon_index = 0;
	
	public static void initialize(RobotController rc) throws GameActionException {
		Action.rc = rc;
		Archon.rc = rc;
		Bfs.rc = rc;
		Builder.rc = rc;
		Comms.rc = rc;
		Exploration.rc = rc;
		Info.rc = rc;
		Laboratory.rc = rc;
		Micro.rc = rc;
		Miner.rc = rc;
		Pathing.rc = rc;
		Sage.rc = rc;
		Soldier.rc = rc;
		Watchtower.rc = rc;
		friendly = rc.getTeam();
		if (friendly==Team.A) {enemy=Team.B;}
		if (friendly==Team.B) {enemy=Team.A;}
		id = rc.getID();
		spawn_round = rc.getRoundNum();
		spawn_loc = rc.getLocation();
		type = rc.getType();
		base_move_cooldown = type.movementCooldown;
		MIN_MOVE_COOLDOWN = Math2.move_cooldown(GameConstants.MIN_RUBBLE, base_move_cooldown);
		VISION_DIST2 = type.visionRadiusSquared;
		ACTION_DIST2 = type.actionRadiusSquared;
		MAP_WIDTH = rc.getMapWidth();
		MAP_HEIGHT = rc.getMapHeight();
		friendly_archons = new RobotInfo[4];
		friendly_builders = new RobotInfo[30];
		friendly_laboratories = new RobotInfo[30];
		friendly_miners = new RobotInfo[50];
		friendly_sages = new RobotInfo[70];
		friendly_soldiers = new RobotInfo[70];
		friendly_watchtowers = new RobotInfo[50];
		enemy_archons = new RobotInfo[4];
		enemy_builders = new RobotInfo[20];
		enemy_laboratories = new RobotInfo[20];
		enemy_miners = new RobotInfo[30];
		enemy_sages = new RobotInfo[50];
		enemy_soldiers = new RobotInfo[50];
		enemy_watchtowers = new RobotInfo[40];
		rng = new Random(id);
	}
	
	public static void post_comms_init() throws GameActionException {
	}
	
	public static void update() throws GameActionException {
		loc = rc.getLocation();
		x = loc.x;
		y = loc.y;
		round_num = rc.getRoundNum();
		action_ready = rc.isActionReady();
		move_ready = rc.isMovementReady();
		total_friendly_robots = rc.getRobotCount();
		lead_last_turn = lead;
		lead = rc.getTeamLeadAmount(friendly);
		gold = rc.getTeamGoldAmount(friendly);
		rubble = rc.senseRubble(loc);
		visible_tiles = rc.getAllLocationsWithinRadiusSquared(loc, VISION_DIST2);
		health = rc.getHealth();

//		if (Info.round_num > Info.spawn_round + 1 && Info.round_num < Info.spawn_round + 5) {
		if (Info.round_num > Info.spawn_round + 1) {
			int min_r2 = Integer.MAX_VALUE;
			for (int i=Comms.archon_positions.length; --i>=0;) {
				if (Comms.archons_alive[i] && Info.loc.isWithinDistanceSquared(Comms.archon_positions[i], min_r2)) {
					min_r2 = Info.loc.distanceSquaredTo(Comms.archon_positions[i]);
					spawn_archon_index = i;
				}
			}
		}

		friendly_robots = rc.senseNearbyRobots(VISION_DIST2, friendly);
		n_friendly_archons = 0;
		n_friendly_builders = 0;
		n_friendly_laboratories = 0;
		n_friendly_miners = 0;
		n_friendly_sages = 0;
		n_friendly_soldiers = 0;
		n_friendly_watchtowers = 0;
		for (RobotInfo robot:friendly_robots) {
			switch (robot.getType()) {
			case ARCHON: {
				friendly_archons[n_friendly_archons] = robot;
				n_friendly_archons++;
				break;}
			case BUILDER: {
				friendly_builders[n_friendly_builders] = robot;
				n_friendly_builders++;
				break;}
			case LABORATORY: {
				friendly_laboratories[n_friendly_laboratories] = robot;
				n_friendly_laboratories++;
				break;}
			case MINER: {
				friendly_miners[n_friendly_miners] = robot;
				n_friendly_miners++;
				break;}
			case SAGE: {
				friendly_sages[n_friendly_sages] = robot;
				n_friendly_sages++;
				break;}
			case SOLDIER: {
				friendly_soldiers[n_friendly_soldiers] = robot;
				n_friendly_soldiers++;
				break;}
			case WATCHTOWER: {
				friendly_watchtowers[n_friendly_watchtowers] = robot;
				n_friendly_watchtowers++;
				break;}
			}
		}
		enemy_robots = rc.senseNearbyRobots(VISION_DIST2, enemy);
		n_enemy_archons = 0;
		n_enemy_builders = 0;
		n_enemy_laboratories = 0;
		n_enemy_miners = 0;
		n_enemy_sages = 0;
		n_enemy_soldiers = 0;
		n_enemy_watchtowers = 0;
		for (RobotInfo robot:enemy_robots) {
			switch (robot.getType()) {
			case ARCHON: {
				enemy_archons[n_enemy_archons] = robot;
				n_enemy_archons++;
				break;}
			case BUILDER: {
				enemy_builders[n_enemy_builders] = robot;
				n_enemy_builders++;
				break;}
			case LABORATORY: {
				enemy_laboratories[n_enemy_laboratories] = robot;
				n_enemy_laboratories++;
				break;}
			case MINER: {
				enemy_miners[n_enemy_miners] = robot;
				n_enemy_miners++;
				break;}
			case SAGE: {
				enemy_sages[n_enemy_sages] = robot;
				n_enemy_sages++;
				break;}
			case SOLDIER: {
				enemy_soldiers[n_enemy_soldiers] = robot;
				n_enemy_soldiers++;
				break;}
			case WATCHTOWER: {
				enemy_watchtowers[n_enemy_watchtowers] = robot;
				n_enemy_watchtowers++;
				break;}
			}
		}
		n_enemy_attackers = n_enemy_soldiers + n_enemy_watchtowers + n_enemy_sages;
		n_friendly_attackers = n_friendly_soldiers + n_friendly_watchtowers + n_friendly_sages;
		n_moveable_tiles = 0;
		moveable_tiles = new boolean[3][3];
		int max_dx = Math.min(1, Info.MAP_WIDTH-1-Info.x)+1;
		int min_dx = Math.max(-1, -Info.x);
		int max_dy = Math.min(1, Info.MAP_HEIGHT-1-Info.y)+1;
		int min_dy = Math.max(-1, -Info.y);
		for (int dx = max_dx; --dx >= min_dx;) {
			for (int dy = max_dy; --dy >= min_dy;) {
				moveable_tiles[dx+1][dy+1] = !rc.isLocationOccupied(Info.loc.translate(dx, dy));
				n_moveable_tiles += moveable_tiles[dx+1][dy+1]? 1 : 0;
			}
		}
		
		if (round_num==1 && type==RobotType.ARCHON) {
			int[] rubbles = new int[5];
			for (int i=5; --i>=0;) {
				rubbles[i] = rc.senseRubble(visible_tiles[rng.nextInt(Info.visible_tiles.length)]);
			}
			int min_rubble = Math.min(Math.min(Math.min(Math.min(rubbles[0], rubbles[1]), rubbles[2]), rubbles[3]), rubbles[4]);
			int second_min_rubble = Integer.MAX_VALUE;
			boolean min_rubble_already_iterated = false;
			for (int i=5; --i>=0;) {
				if ((rubbles[i] > min_rubble || min_rubble_already_iterated) && rubbles[i] < second_min_rubble) {
					second_min_rubble = rubbles[i];
				}
				if (rubbles[i] == min_rubble) {
					min_rubble_already_iterated = true;
				}
			}
			average_rubble = (rubbles[0] + rubbles[1] + rubbles[2] + rubbles[3] + rubbles[4])/5.;
			rubble_quantile = second_min_rubble;
		}
		
		double added_rubble = 0;
		for (int i=2; --i>=0;) {
			int rubble = rc.senseRubble(visible_tiles[Info.rng.nextInt(visible_tiles.length)]);
			added_rubble += rubble;
			if (rubble > rubble_quantile) {
				rubble_quantile += PATHING_PERCOLATION_CONSTANT / Math.sqrt(PATHING_PERCOLATION_CONSTANT*(1-PATHING_PERCOLATION_CONSTANT));
			}
			else {
				rubble_quantile -= (1-PATHING_PERCOLATION_CONSTANT) / Math.sqrt(PATHING_PERCOLATION_CONSTANT*(1-PATHING_PERCOLATION_CONSTANT));
			}
		}
		average_rubble += RUBBLE_ESTIMATE_DECAY*(added_rubble/2-average_rubble);
		
		encountered_attackers = encountered_attackers || n_enemy_attackers > 0;
		encountered_enemies = encountered_enemies || enemy_robots.length > 0;
		
		if (round_num > spawn_round && lead > lead_last_turn) {
			income_decay = 1-1/Math.sqrt(Info.round_num+4);
			income_1 *= income_decay;
			income_t *= income_decay;
			income_i *= income_decay;
			income_tt *= income_decay;
			income_ti *= income_decay;
			double i = lead - lead_last_turn;
			double t = Info.round_num;
			income_1 += 1;
			income_t += t;
			income_i += i;
			income_tt += t*t;
			income_ti += t*i;
			if (income_t*income_t+0.01 > income_1*income_tt) {  // degenerate case
				income_estimate = i;
			}
			else {
				income_didt = (income_ti*income_1-income_t*income_i)/(income_tt*income_1-income_t*income_t);
				income_estimate = income_didt*(t-income_t)+income_i;
			}
		}

		not_enough_laboratories = Comms.total_mining_miner_count*3 >= Comms.laboratory_count*4 || Info.lead > 250;
		
		//  This must be last
		Bfs.reset();
		if (rc.getType()==RobotType.ARCHON) {
			Archon.update();
			int all_guaranteed_lead = Info.lead;
			for (int i=Comms.archon_positions.length; --i>=0;) {
				all_guaranteed_lead += Comms.archons_alive[i]? Comms.archon_total_leads[i] : 0;
			}
//			not_enough_laboratories = not_enough_laboratories || all_guaranteed_lead < 220 && !Comms.builder_exists && Info.round_num >= 3;
//			rc.setIndicatorString(String.valueOf(not_enough_laboratories));
		}
		else if (type==RobotType.BUILDER) {
			Builder.update();
		}
		else if (type==RobotType.LABORATORY) {
			Laboratory.update();
		}
		else if (type==RobotType.MINER) {
			Miner.update();
		}
		else if (type==RobotType.SAGE) {
			Sage.update();
		}
		else if (type==RobotType.SOLDIER) {
			Micro.update();
			Soldier.update();
		}
		else if (type==RobotType.WATCHTOWER) {
			Watchtower.update();
		}
	}
	
	public static RobotInfo get_friendly_attacker(int idx) {
		return (idx<n_friendly_soldiers)? friendly_soldiers[idx] :
			((idx<n_friendly_soldiers+n_friendly_watchtowers)? friendly_watchtowers[idx-n_friendly_soldiers] :
				friendly_sages[idx-n_friendly_soldiers-n_friendly_watchtowers]);
	}
	
	public static RobotInfo get_enemy_attacker(int idx) {
		return (idx<n_enemy_soldiers)? enemy_soldiers[idx] :
			((idx<n_enemy_soldiers+n_enemy_watchtowers)? enemy_watchtowers[idx-n_enemy_soldiers] :
				enemy_sages[idx-n_enemy_soldiers-n_enemy_watchtowers]);
	}
}
