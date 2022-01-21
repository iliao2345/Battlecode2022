package movement_fix_1;
import battlecode.common.*;

public class Exploration {
	public static double MOMENTUM_CAP = 2;
	public static RobotController rc;
	
	public static double momentum_dx;
	public static double momentum_dy;
	public static double force_dx;
	public static double force_dy;

	public static void repel(RobotInfo[] robots, int n_robots, double coefficient) throws GameActionException {
		double component_dx = 0;
		double component_dy = 0;
		for (int i=n_robots; --i>=0;) {
			MapLocation repel_loc = robots[i].location;
			component_dx -= (double)(repel_loc.x-Info.x)/Info.loc.distanceSquaredTo(repel_loc);
			component_dy -= (double)(repel_loc.y-Info.y)/Info.loc.distanceSquaredTo(repel_loc);
		}
		if ((Info.x+1)*(Info.x+1)<=Info.VISION_DIST2) {
			component_dx -= (double)(-2*Info.x-2)/(Info.x+1)/(Info.x+1);
		}
		if ((Info.MAP_WIDTH-Info.x+1)*(Info.MAP_WIDTH-Info.x+1)<=30) {
			component_dx -= (double)(2*Info.MAP_WIDTH-2*Info.x+2)/(Info.MAP_WIDTH-Info.x+1)/(Info.MAP_WIDTH-Info.x+1);
		}
		if ((Info.y+1)*(Info.y+1)<=Info.VISION_DIST2) {
			component_dy -= (double)(-2*Info.y-2)/(Info.y+1)/(Info.y+1);
		}
		if ((Info.MAP_HEIGHT-Info.y+1)*(Info.MAP_HEIGHT-Info.y+1)<=30) {
			component_dy -= (double)(2*Info.MAP_HEIGHT-2*Info.y+2)/(Info.MAP_HEIGHT-Info.y+1)/(Info.MAP_HEIGHT-Info.y+1);
		}
		force_dx += coefficient*component_dx;
		force_dy += coefficient*component_dy;
	}

    public static boolean travel() throws GameActionException {
    	double r = Math.sqrt(force_dx*force_dx+force_dy*force_dy);
		if (r>1e-10) {
	    	force_dx = force_dx/r;
	    	force_dy = force_dy/r;
		}
		momentum_dx += force_dx;
		momentum_dy += force_dy;
		force_dx = 0;
		force_dy = 0;
    	r = Math.sqrt(momentum_dx*momentum_dx+momentum_dy*momentum_dy);
    	if (r>MOMENTUM_CAP && r>1e-10) {
	    	momentum_dx = momentum_dx/r*MOMENTUM_CAP;
	    	momentum_dy = momentum_dy/r*MOMENTUM_CAP;
    	}
    	MapLocation pseudotarget = Info.loc.translate((int)(1000*momentum_dx), (int)(1000*momentum_dy));
    	boolean[][] illegal_tiles = new boolean[3][3];
		return Pathing.approach(pseudotarget, illegal_tiles);
    }
}
