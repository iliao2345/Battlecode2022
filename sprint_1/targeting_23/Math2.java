package targeting_23;
import battlecode.common.*;



public class Math2 {
	public static Direction[] DIAGONAL_DIRECTIONS = new Direction[] {Direction.NORTHEAST, Direction.NORTHWEST, Direction.SOUTHWEST, Direction.SOUTHEAST};
	public static Direction[] UNIT_DIRECTIONS = new Direction[] {Direction.EAST, Direction.NORTHEAST, Direction.NORTH, Direction.NORTHWEST, Direction.WEST, Direction.SOUTHWEST, Direction.SOUTH, Direction.SOUTHEAST};
	public static Cycler[] memoized_offsets_within_dist2 = new Cycler[RobotType.LABORATORY.visionRadiusSquared+1];
	
	public static int length(int x, int y) {
		return Math.max(Math.abs(x), Math.abs(y));
	}
	public static int length(int x1, int y1, int x2, int y2) {
		return Math.max(Math.abs(x1-x2), Math.abs(y1-y2));
	}
	public static int length(MapLocation loc1, MapLocation loc2) {
		return Math.max(Math.abs(loc1.x-loc2.x), Math.abs(loc1.y-loc2.y));
	}
	public static int cardinal_length(int x, int y) {
		return Math.abs(x)+Math.abs(y);
	}
	public static int cardinal_length(int x1, int y1, int x2, int y2) {
		return Math.abs(x1-x2)+Math.abs(y1-y2);
	}
	public static int cardinal_length(MapLocation loc1, MapLocation loc2) {
		return Math.abs(loc1.x-loc2.x)+Math.abs(loc1.y-loc2.y);
	}
	public static int cardinal_length(Direction dir) {
		return Math.abs(dir.dx)+Math.abs(dir.dy);
	}
	public static Direction cardinalize_left(Direction dir) {
		if (dir.dx*dir.dx+dir.dy*dir.dy==2) {return dir.rotateLeft();}
		return dir;
	}
	public static Direction cardinalize_right(Direction dir) {
		if (dir.dx*dir.dx+dir.dy*dir.dy==2) {return dir.rotateRight();}
		return dir;
	}
	public static Direction direction_to(double dx, double dy) {
		return UNIT_DIRECTIONS[(int)Math.floor(Math.atan2(dy, dx)/Math.PI*4 + 8) % 8];
	}
	public static Cycler getOffsetsWithinDist2(int max_dist2) {
		if (memoized_offsets_within_dist2[max_dist2].length > 0) {
			return memoized_offsets_within_dist2[max_dist2];
		}
		Cycler<int[]> offsets = new Cycler();
		int min_dx = -(int)Math.floor(Math.sqrt(max_dist2));
		for (int dx = -min_dx+1; --dx >= min_dx;) {
			int min_dy = -(int)Math.floor(Math.sqrt(max_dist2-dx*dx));
			for (int dy = -min_dy+1; --dy >= min_dy;) {
				offsets.push(new int[] {dx, dy});
			}
		}
		memoized_offsets_within_dist2[max_dist2] = offsets;
		return offsets;
	}
	public static int move_cooldown(int rubble, int base_cooldown) {
		return (int)(Math.floor((1+rubble/10.)*base_cooldown));
	}
}