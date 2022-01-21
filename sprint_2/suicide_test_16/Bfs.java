package suicide_test_16;
import battlecode.common.*;

public class Bfs {
    public static RobotController rc;

    private static final Direction[] DIRECTIONS = new Direction[] {Direction.CENTER, Direction.NORTHEAST, Direction.NORTHWEST, Direction.SOUTHWEST, Direction.SOUTHEAST, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH};
	private static boolean on_map_calculated = false;
	private static boolean cheap_dists_calculated = false;
	private static boolean expensive_dists_calculated = false;
	private static double dist_55 = 0;
	private static double dist_45 = 0;
	private static double dist_54 = 0;
	private static double dist_56 = 0;
	private static double dist_65 = 0;
	private static double dist_44 = 0;
	private static double dist_46 = 0;
	private static double dist_64 = 0;
	private static double dist_66 = 0;
	private static double dist_35 = 0;
	private static double dist_53 = 0;
	private static double dist_57 = 0;
	private static double dist_75 = 0;
	private static double dist_34 = 0;
	private static double dist_36 = 0;
	private static double dist_43 = 0;
	private static double dist_47 = 0;
	private static double dist_63 = 0;
	private static double dist_67 = 0;
	private static double dist_74 = 0;
	private static double dist_76 = 0;
	private static double dist_33 = 0;
	private static double dist_37 = 0;
	private static double dist_73 = 0;
	private static double dist_77 = 0;
	private static double dist_25 = 0;
	private static double dist_52 = 0;
	private static double dist_58 = 0;
	private static double dist_85 = 0;
	private static double dist_24 = 0;
	private static double dist_26 = 0;
	private static double dist_42 = 0;
	private static double dist_48 = 0;
	private static double dist_62 = 0;
	private static double dist_68 = 0;
	private static double dist_84 = 0;
	private static double dist_86 = 0;
	private static double dist_23 = 0;
	private static double dist_27 = 0;
	private static double dist_32 = 0;
	private static double dist_38 = 0;
	private static double dist_72 = 0;
	private static double dist_78 = 0;
	private static double dist_83 = 0;
	private static double dist_87 = 0;
	private static double dist_15 = 0;
	private static double dist_51 = 0;
	private static double dist_59 = 0;
	private static double dist_95 = 0;
	private static double dist_14 = 0;
	private static double dist_16 = 0;
	private static double dist_41 = 0;
	private static double dist_49 = 0;
	private static double dist_61 = 0;
	private static double dist_69 = 0;
	private static double dist_94 = 0;
	private static double dist_96 = 0;
	private static double dist_22 = 0;
	private static double dist_28 = 0;
	private static double dist_82 = 0;
	private static double dist_88 = 0;
	private static double dist_13 = 0;
	private static double dist_17 = 0;
	private static double dist_31 = 0;
	private static double dist_39 = 0;
	private static double dist_71 = 0;
	private static double dist_79 = 0;
	private static double dist_93 = 0;
	private static double dist_97 = 0;
	private static double encoded_dist_55 = 0;
	private static double encoded_dist_45 = 0;
	private static double encoded_dist_54 = 0;
	private static double encoded_dist_56 = 0;
	private static double encoded_dist_65 = 0;
	private static double encoded_dist_44 = 0;
	private static double encoded_dist_46 = 0;
	private static double encoded_dist_64 = 0;
	private static double encoded_dist_66 = 0;
	private static double encoded_dist_35 = 0;
	private static double encoded_dist_53 = 0;
	private static double encoded_dist_57 = 0;
	private static double encoded_dist_75 = 0;
	private static double encoded_dist_34 = 0;
	private static double encoded_dist_36 = 0;
	private static double encoded_dist_43 = 0;
	private static double encoded_dist_47 = 0;
	private static double encoded_dist_63 = 0;
	private static double encoded_dist_67 = 0;
	private static double encoded_dist_74 = 0;
	private static double encoded_dist_76 = 0;
	private static double encoded_dist_33 = 0;
	private static double encoded_dist_37 = 0;
	private static double encoded_dist_73 = 0;
	private static double encoded_dist_77 = 0;
	private static double encoded_dist_25 = 0;
	private static double encoded_dist_52 = 0;
	private static double encoded_dist_58 = 0;
	private static double encoded_dist_85 = 0;
	private static double encoded_dist_24 = 0;
	private static double encoded_dist_26 = 0;
	private static double encoded_dist_42 = 0;
	private static double encoded_dist_48 = 0;
	private static double encoded_dist_62 = 0;
	private static double encoded_dist_68 = 0;
	private static double encoded_dist_84 = 0;
	private static double encoded_dist_86 = 0;
	private static double encoded_dist_23 = 0;
	private static double encoded_dist_27 = 0;
	private static double encoded_dist_32 = 0;
	private static double encoded_dist_38 = 0;
	private static double encoded_dist_72 = 0;
	private static double encoded_dist_78 = 0;
	private static double encoded_dist_83 = 0;
	private static double encoded_dist_87 = 0;
	private static double encoded_dist_15 = 0;
	private static double encoded_dist_51 = 0;
	private static double encoded_dist_59 = 0;
	private static double encoded_dist_95 = 0;
	private static double encoded_dist_14 = 0;
	private static double encoded_dist_16 = 0;
	private static double encoded_dist_41 = 0;
	private static double encoded_dist_49 = 0;
	private static double encoded_dist_61 = 0;
	private static double encoded_dist_69 = 0;
	private static double encoded_dist_94 = 0;
	private static double encoded_dist_96 = 0;
	private static double encoded_dist_22 = 0;
	private static double encoded_dist_28 = 0;
	private static double encoded_dist_82 = 0;
	private static double encoded_dist_88 = 0;
	private static double encoded_dist_13 = 0;
	private static double encoded_dist_17 = 0;
	private static double encoded_dist_31 = 0;
	private static double encoded_dist_39 = 0;
	private static double encoded_dist_71 = 0;
	private static double encoded_dist_79 = 0;
	private static double encoded_dist_93 = 0;
	private static double encoded_dist_97 = 0;
	private static boolean on_map_x_0 = true;
	private static boolean on_map_x_1 = true;
	private static boolean on_map_x_2 = true;
	private static boolean on_map_x_3 = true;
	private static boolean on_map_x_4 = true;
	private static boolean on_map_x_5 = true;
	private static boolean on_map_x_6 = true;
	private static boolean on_map_x_7 = true;
	private static boolean on_map_x_8 = true;
	private static boolean on_map_x_9 = true;
	private static boolean on_map_x_10 = true;
	private static boolean on_map_y_0 = true;
	private static boolean on_map_y_1 = true;
	private static boolean on_map_y_2 = true;
	private static boolean on_map_y_3 = true;
	private static boolean on_map_y_4 = true;
	private static boolean on_map_y_5 = true;
	private static boolean on_map_y_6 = true;
	private static boolean on_map_y_7 = true;
	private static boolean on_map_y_8 = true;
	private static boolean on_map_y_9 = true;
	private static boolean on_map_y_10 = true;


    public static void reset() {
        on_map_calculated = false;
        cheap_dists_calculated = false;
        expensive_dists_calculated = false;
    }
    
    private static void compute_on_map() {
        if (on_map_calculated) {
            return;
        }
        on_map_calculated = true;
		int width = Info.MAP_WIDTH;
		int height = Info.MAP_HEIGHT;
		int x = Info.x;
		int y = Info.y;
		on_map_x_0 = x >= 5;
		on_map_x_1 = x >= 4;
		on_map_x_2 = x >= 3;
		on_map_x_3 = x >= 2;
		on_map_x_4 = x >= 1;
		on_map_x_6 = x < width - 1;
		on_map_x_7 = x < width - 2;
		on_map_x_8 = x < width - 3;
		on_map_x_9 = x < width - 4;
		on_map_x_10 = x < width - 5;
		on_map_y_0 = y >= 5;
		on_map_y_1 = y >= 4;
		on_map_y_2 = y >= 3;
		on_map_y_3 = y >= 2;
		on_map_y_4 = y >= 1;
		on_map_y_6 = y < height - 1;
		on_map_y_7 = y < height - 2;
		on_map_y_8 = y < height - 3;
		on_map_y_9 = y < height - 4;
		on_map_y_10 = y < height - 5;
    }

    private static void compute_cheap_dists(double wait_cost) throws GameActionException {
        if (cheap_dists_calculated) {
            return;
        }
        cheap_dists_calculated = true;
        compute_on_map();
		MapLocation this_loc = Info.loc;
		dist_45 = (on_map_x_4 && on_map_y_5)? dist_55 + (rc.canMove(Math2.direction_to(-1, 0))? 10.4375 : wait_cost) + rc.senseRubble(this_loc.translate(-1, 0)) : Double.POSITIVE_INFINITY;
		dist_54 = (on_map_x_5 && on_map_y_4)? dist_55 + (rc.canMove(Math2.direction_to(0, -1))? 10.5 : wait_cost) + rc.senseRubble(this_loc.translate(0, -1)) : Double.POSITIVE_INFINITY;
		dist_56 = (on_map_x_5 && on_map_y_6)? dist_55 + (rc.canMove(Math2.direction_to(0, 1))? 10.375 : wait_cost) + rc.senseRubble(this_loc.translate(0, 1)) : Double.POSITIVE_INFINITY;
		dist_65 = (on_map_x_6 && on_map_y_5)? dist_55 + (rc.canMove(Math2.direction_to(1, 0))? 10.3125 : wait_cost) + rc.senseRubble(this_loc.translate(1, 0)) : Double.POSITIVE_INFINITY;
		dist_44 = (on_map_x_4 && on_map_y_4)? Math.min(dist_55, Math.min(dist_54, dist_45)) + (rc.canMove(Math2.direction_to(-1, -1))? 10.1875 : wait_cost) + rc.senseRubble(this_loc.translate(-1, -1)) : Double.POSITIVE_INFINITY;
		dist_46 = (on_map_x_4 && on_map_y_6)? Math.min(dist_56, Math.min(dist_55, dist_45)) + (rc.canMove(Math2.direction_to(-1, 1))? 10.125 : wait_cost) + rc.senseRubble(this_loc.translate(-1, 1)) : Double.POSITIVE_INFINITY;
		dist_64 = (on_map_x_6 && on_map_y_4)? Math.min(dist_65, Math.min(dist_55, dist_54)) + (rc.canMove(Math2.direction_to(1, -1))? 10.25 : wait_cost) + rc.senseRubble(this_loc.translate(1, -1)) : Double.POSITIVE_INFINITY;
		dist_66 = (on_map_x_6 && on_map_y_6)? Math.min(dist_65, Math.min(dist_56, dist_55)) + (rc.canMove(Math2.direction_to(1, 1))? 10.0625 : wait_cost) + rc.senseRubble(this_loc.translate(1, 1)) : Double.POSITIVE_INFINITY;
		dist_35 = (on_map_x_3 && on_map_y_5)? Math.min(dist_46, Math.min(dist_45, dist_44)) + 10. + rc.senseRubble(this_loc.translate(-2, 0)) : Double.POSITIVE_INFINITY;
		dist_53 = (on_map_x_5 && on_map_y_3)? Math.min(dist_64, Math.min(dist_54, dist_44)) + 10. + rc.senseRubble(this_loc.translate(0, -2)) : Double.POSITIVE_INFINITY;
		dist_57 = (on_map_x_5 && on_map_y_7)? Math.min(dist_66, Math.min(dist_56, dist_46)) + 10. + rc.senseRubble(this_loc.translate(0, 2)) : Double.POSITIVE_INFINITY;
		dist_75 = (on_map_x_7 && on_map_y_5)? Math.min(dist_66, Math.min(dist_65, dist_64)) + 10. + rc.senseRubble(this_loc.translate(2, 0)) : Double.POSITIVE_INFINITY;
		dist_34 = (on_map_x_3 && on_map_y_4)? Math.min(dist_45, Math.min(dist_44, dist_35)) + 10. + rc.senseRubble(this_loc.translate(-2, -1)) : Double.POSITIVE_INFINITY;
		dist_36 = (on_map_x_3 && on_map_y_6)? Math.min(dist_46, Math.min(dist_45, dist_35)) + 10. + rc.senseRubble(this_loc.translate(-2, 1)) : Double.POSITIVE_INFINITY;
		dist_43 = (on_map_x_4 && on_map_y_3)? Math.min(dist_54, Math.min(dist_53, dist_44)) + 10. + rc.senseRubble(this_loc.translate(-1, -2)) : Double.POSITIVE_INFINITY;
		dist_47 = (on_map_x_4 && on_map_y_7)? Math.min(dist_57, Math.min(dist_56, dist_46)) + 10. + rc.senseRubble(this_loc.translate(-1, 2)) : Double.POSITIVE_INFINITY;
		dist_63 = (on_map_x_6 && on_map_y_3)? Math.min(dist_64, Math.min(dist_54, dist_53)) + 10. + rc.senseRubble(this_loc.translate(1, -2)) : Double.POSITIVE_INFINITY;
		dist_67 = (on_map_x_6 && on_map_y_7)? Math.min(dist_66, Math.min(dist_57, dist_56)) + 10. + rc.senseRubble(this_loc.translate(1, 2)) : Double.POSITIVE_INFINITY;
		dist_74 = (on_map_x_7 && on_map_y_4)? Math.min(dist_75, Math.min(dist_65, dist_64)) + 10. + rc.senseRubble(this_loc.translate(2, -1)) : Double.POSITIVE_INFINITY;
		dist_76 = (on_map_x_7 && on_map_y_6)? Math.min(dist_75, Math.min(dist_66, dist_65)) + 10. + rc.senseRubble(this_loc.translate(2, 1)) : Double.POSITIVE_INFINITY;
		dist_33 = (on_map_x_3 && on_map_y_3)? Math.min(dist_44, Math.min(dist_43, dist_34)) + 10. + rc.senseRubble(this_loc.translate(-2, -2)) : Double.POSITIVE_INFINITY;
		dist_37 = (on_map_x_3 && on_map_y_7)? Math.min(dist_47, Math.min(dist_46, dist_36)) + 10. + rc.senseRubble(this_loc.translate(-2, 2)) : Double.POSITIVE_INFINITY;
		dist_73 = (on_map_x_7 && on_map_y_3)? Math.min(dist_74, Math.min(dist_64, dist_63)) + 10. + rc.senseRubble(this_loc.translate(2, -2)) : Double.POSITIVE_INFINITY;
		dist_77 = (on_map_x_7 && on_map_y_7)? Math.min(dist_76, Math.min(dist_67, dist_66)) + 10. + rc.senseRubble(this_loc.translate(2, 2)) : Double.POSITIVE_INFINITY;
		dist_25 = (on_map_x_2 && on_map_y_5)? Math.min(dist_36, Math.min(dist_35, dist_34)) + 10. + rc.senseRubble(this_loc.translate(-3, 0)) : Double.POSITIVE_INFINITY;
		dist_52 = (on_map_x_5 && on_map_y_2)? Math.min(dist_63, Math.min(dist_53, dist_43)) + 10. + rc.senseRubble(this_loc.translate(0, -3)) : Double.POSITIVE_INFINITY;
		dist_58 = (on_map_x_5 && on_map_y_8)? Math.min(dist_67, Math.min(dist_57, dist_47)) + 10. + rc.senseRubble(this_loc.translate(0, 3)) : Double.POSITIVE_INFINITY;
		dist_85 = (on_map_x_8 && on_map_y_5)? Math.min(dist_76, Math.min(dist_75, dist_74)) + 10. + rc.senseRubble(this_loc.translate(3, 0)) : Double.POSITIVE_INFINITY;
		dist_24 = (on_map_x_2 && on_map_y_4)? Math.min(dist_35, Math.min(dist_34, Math.min(dist_33, dist_25))) + 10. + rc.senseRubble(this_loc.translate(-3, -1)) : Double.POSITIVE_INFINITY;
		dist_26 = (on_map_x_2 && on_map_y_6)? Math.min(dist_37, Math.min(dist_36, Math.min(dist_35, dist_25))) + 10. + rc.senseRubble(this_loc.translate(-3, 1)) : Double.POSITIVE_INFINITY;
		dist_42 = (on_map_x_4 && on_map_y_2)? Math.min(dist_53, Math.min(dist_52, Math.min(dist_43, dist_33))) + 10. + rc.senseRubble(this_loc.translate(-1, -3)) : Double.POSITIVE_INFINITY;
		dist_48 = (on_map_x_4 && on_map_y_8)? Math.min(dist_58, Math.min(dist_57, Math.min(dist_47, dist_37))) + 10. + rc.senseRubble(this_loc.translate(-1, 3)) : Double.POSITIVE_INFINITY;
		dist_62 = (on_map_x_6 && on_map_y_2)? Math.min(dist_73, Math.min(dist_63, Math.min(dist_53, dist_52))) + 10. + rc.senseRubble(this_loc.translate(1, -3)) : Double.POSITIVE_INFINITY;
		dist_68 = (on_map_x_6 && on_map_y_8)? Math.min(dist_77, Math.min(dist_67, Math.min(dist_58, dist_57))) + 10. + rc.senseRubble(this_loc.translate(1, 3)) : Double.POSITIVE_INFINITY;
		dist_84 = (on_map_x_8 && on_map_y_4)? Math.min(dist_85, Math.min(dist_75, Math.min(dist_74, dist_73))) + 10. + rc.senseRubble(this_loc.translate(3, -1)) : Double.POSITIVE_INFINITY;
		dist_86 = (on_map_x_8 && on_map_y_6)? Math.min(dist_85, Math.min(dist_77, Math.min(dist_76, dist_75))) + 10. + rc.senseRubble(this_loc.translate(3, 1)) : Double.POSITIVE_INFINITY;
		encoded_dist_55 = dist_55 + 0.029296875;
		encoded_dist_45 = dist_45 + 0.02880859375;
		encoded_dist_54 = dist_54 + 0.02392578125;
		encoded_dist_56 = dist_56 + 0.03466796875;
		encoded_dist_65 = dist_65 + 0.02978515625;
		encoded_dist_44 = dist_44 + 0.0234375;
		encoded_dist_46 = dist_46 + 0.0341796875;
		encoded_dist_64 = dist_64 + 0.0244140625;
		encoded_dist_66 = dist_66 + 0.03515625;
		encoded_dist_35 = dist_35 + 0.0283203125;
		encoded_dist_53 = dist_53 + 0.0185546875;
		encoded_dist_57 = dist_57 + 0.0400390625;
		encoded_dist_75 = dist_75 + 0.0302734375;
		encoded_dist_34 = dist_34 + 0.02294921875;
		encoded_dist_36 = dist_36 + 0.03369140625;
		encoded_dist_43 = dist_43 + 0.01806640625;
		encoded_dist_47 = dist_47 + 0.03955078125;
		encoded_dist_63 = dist_63 + 0.01904296875;
		encoded_dist_67 = dist_67 + 0.04052734375;
		encoded_dist_74 = dist_74 + 0.02490234375;
		encoded_dist_76 = dist_76 + 0.03564453125;
		encoded_dist_33 = dist_33 + 0.017578125;
		encoded_dist_37 = dist_37 + 0.0390625;
		encoded_dist_73 = dist_73 + 0.01953125;
		encoded_dist_77 = dist_77 + 0.041015625;
		encoded_dist_25 = dist_25 + 0.02783203125;
		encoded_dist_52 = dist_52 + 0.01318359375;
		encoded_dist_58 = dist_58 + 0.04541015625;
		encoded_dist_85 = dist_85 + 0.03076171875;
		encoded_dist_24 = dist_24 + 0.0224609375;
		encoded_dist_26 = dist_26 + 0.033203125;
		encoded_dist_42 = dist_42 + 0.0126953125;
		encoded_dist_48 = dist_48 + 0.044921875;
		encoded_dist_62 = dist_62 + 0.013671875;
		encoded_dist_68 = dist_68 + 0.0458984375;
		encoded_dist_84 = dist_84 + 0.025390625;
		encoded_dist_86 = dist_86 + 0.0361328125;
  }
    private static void compute_expensive_dists(double wait_cost) throws GameActionException {
        if (expensive_dists_calculated) {
            return;
        }
        expensive_dists_calculated = true;
        compute_cheap_dists(wait_cost);
		MapLocation this_loc = Info.loc;
		dist_24 = (on_map_x_2 && on_map_y_4)? Math.min(dist_35, Math.min(dist_34, Math.min(dist_33, dist_25))) + 10. + rc.senseRubble(this_loc.translate(-3, -1)) : Double.POSITIVE_INFINITY;
		dist_26 = (on_map_x_2 && on_map_y_6)? Math.min(dist_37, Math.min(dist_36, Math.min(dist_35, dist_25))) + 10. + rc.senseRubble(this_loc.translate(-3, 1)) : Double.POSITIVE_INFINITY;
		dist_42 = (on_map_x_4 && on_map_y_2)? Math.min(dist_53, Math.min(dist_52, Math.min(dist_43, dist_33))) + 10. + rc.senseRubble(this_loc.translate(-1, -3)) : Double.POSITIVE_INFINITY;
		dist_48 = (on_map_x_4 && on_map_y_8)? Math.min(dist_58, Math.min(dist_57, Math.min(dist_47, dist_37))) + 10. + rc.senseRubble(this_loc.translate(-1, 3)) : Double.POSITIVE_INFINITY;
		dist_62 = (on_map_x_6 && on_map_y_2)? Math.min(dist_73, Math.min(dist_63, Math.min(dist_53, dist_52))) + 10. + rc.senseRubble(this_loc.translate(1, -3)) : Double.POSITIVE_INFINITY;
		dist_68 = (on_map_x_6 && on_map_y_8)? Math.min(dist_77, Math.min(dist_67, Math.min(dist_58, dist_57))) + 10. + rc.senseRubble(this_loc.translate(1, 3)) : Double.POSITIVE_INFINITY;
		dist_84 = (on_map_x_8 && on_map_y_4)? Math.min(dist_85, Math.min(dist_75, Math.min(dist_74, dist_73))) + 10. + rc.senseRubble(this_loc.translate(3, -1)) : Double.POSITIVE_INFINITY;
		dist_86 = (on_map_x_8 && on_map_y_6)? Math.min(dist_85, Math.min(dist_77, Math.min(dist_76, dist_75))) + 10. + rc.senseRubble(this_loc.translate(3, 1)) : Double.POSITIVE_INFINITY;
		dist_23 = (on_map_x_2 && on_map_y_3)? Math.min(dist_34, Math.min(dist_33, dist_24)) + 10. + rc.senseRubble(this_loc.translate(-3, -2)) : Double.POSITIVE_INFINITY;
		dist_27 = (on_map_x_2 && on_map_y_7)? Math.min(dist_37, Math.min(dist_36, dist_26)) + 10. + rc.senseRubble(this_loc.translate(-3, 2)) : Double.POSITIVE_INFINITY;
		dist_32 = (on_map_x_3 && on_map_y_2)? Math.min(dist_43, Math.min(dist_42, dist_33)) + 10. + rc.senseRubble(this_loc.translate(-2, -3)) : Double.POSITIVE_INFINITY;
		dist_38 = (on_map_x_3 && on_map_y_8)? Math.min(dist_48, Math.min(dist_47, dist_37)) + 10. + rc.senseRubble(this_loc.translate(-2, 3)) : Double.POSITIVE_INFINITY;
		dist_72 = (on_map_x_7 && on_map_y_2)? Math.min(dist_73, Math.min(dist_63, dist_62)) + 10. + rc.senseRubble(this_loc.translate(2, -3)) : Double.POSITIVE_INFINITY;
		dist_78 = (on_map_x_7 && on_map_y_8)? Math.min(dist_77, Math.min(dist_68, dist_67)) + 10. + rc.senseRubble(this_loc.translate(2, 3)) : Double.POSITIVE_INFINITY;
		dist_83 = (on_map_x_8 && on_map_y_3)? Math.min(dist_84, Math.min(dist_74, dist_73)) + 10. + rc.senseRubble(this_loc.translate(3, -2)) : Double.POSITIVE_INFINITY;
		dist_87 = (on_map_x_8 && on_map_y_7)? Math.min(dist_86, Math.min(dist_77, dist_76)) + 10. + rc.senseRubble(this_loc.translate(3, 2)) : Double.POSITIVE_INFINITY;
		dist_15 = (on_map_x_1 && on_map_y_5)? Math.min(dist_26, Math.min(dist_25, dist_24)) + 10. + rc.senseRubble(this_loc.translate(-4, 0)) : Double.POSITIVE_INFINITY;
		dist_51 = (on_map_x_5 && on_map_y_1)? Math.min(dist_62, Math.min(dist_52, dist_42)) + 10. + rc.senseRubble(this_loc.translate(0, -4)) : Double.POSITIVE_INFINITY;
		dist_59 = (on_map_x_5 && on_map_y_9)? Math.min(dist_68, Math.min(dist_58, dist_48)) + 10. + rc.senseRubble(this_loc.translate(0, 4)) : Double.POSITIVE_INFINITY;
		dist_95 = (on_map_x_9 && on_map_y_5)? Math.min(dist_86, Math.min(dist_85, dist_84)) + 10. + rc.senseRubble(this_loc.translate(4, 0)) : Double.POSITIVE_INFINITY;
		dist_14 = (on_map_x_1 && on_map_y_4)? Math.min(dist_25, Math.min(dist_24, Math.min(dist_23, dist_15))) + 10. + rc.senseRubble(this_loc.translate(-4, -1)) : Double.POSITIVE_INFINITY;
		dist_16 = (on_map_x_1 && on_map_y_6)? Math.min(dist_27, Math.min(dist_26, Math.min(dist_25, dist_15))) + 10. + rc.senseRubble(this_loc.translate(-4, 1)) : Double.POSITIVE_INFINITY;
		dist_41 = (on_map_x_4 && on_map_y_1)? Math.min(dist_52, Math.min(dist_51, Math.min(dist_42, dist_32))) + 10. + rc.senseRubble(this_loc.translate(-1, -4)) : Double.POSITIVE_INFINITY;
		dist_49 = (on_map_x_4 && on_map_y_9)? Math.min(dist_59, Math.min(dist_58, Math.min(dist_48, dist_38))) + 10. + rc.senseRubble(this_loc.translate(-1, 4)) : Double.POSITIVE_INFINITY;
		dist_61 = (on_map_x_6 && on_map_y_1)? Math.min(dist_72, Math.min(dist_62, Math.min(dist_52, dist_51))) + 10. + rc.senseRubble(this_loc.translate(1, -4)) : Double.POSITIVE_INFINITY;
		dist_69 = (on_map_x_6 && on_map_y_9)? Math.min(dist_78, Math.min(dist_68, Math.min(dist_59, dist_58))) + 10. + rc.senseRubble(this_loc.translate(1, 4)) : Double.POSITIVE_INFINITY;
		dist_94 = (on_map_x_9 && on_map_y_4)? Math.min(dist_95, Math.min(dist_85, Math.min(dist_84, dist_83))) + 10. + rc.senseRubble(this_loc.translate(4, -1)) : Double.POSITIVE_INFINITY;
		dist_96 = (on_map_x_9 && on_map_y_6)? Math.min(dist_95, Math.min(dist_87, Math.min(dist_86, dist_85))) + 10. + rc.senseRubble(this_loc.translate(4, 1)) : Double.POSITIVE_INFINITY;
		dist_22 = (on_map_x_2 && on_map_y_2)? Math.min(dist_33, Math.min(dist_32, dist_23)) + 10. + rc.senseRubble(this_loc.translate(-3, -3)) : Double.POSITIVE_INFINITY;
		dist_28 = (on_map_x_2 && on_map_y_8)? Math.min(dist_38, Math.min(dist_37, dist_27)) + 10. + rc.senseRubble(this_loc.translate(-3, 3)) : Double.POSITIVE_INFINITY;
		dist_82 = (on_map_x_8 && on_map_y_2)? Math.min(dist_83, Math.min(dist_73, dist_72)) + 10. + rc.senseRubble(this_loc.translate(3, -3)) : Double.POSITIVE_INFINITY;
		dist_88 = (on_map_x_8 && on_map_y_8)? Math.min(dist_87, Math.min(dist_78, dist_77)) + 10. + rc.senseRubble(this_loc.translate(3, 3)) : Double.POSITIVE_INFINITY;
		dist_13 = (on_map_x_1 && on_map_y_3)? Math.min(dist_24, Math.min(dist_23, Math.min(dist_22, dist_14))) + 10. + rc.senseRubble(this_loc.translate(-4, -2)) : Double.POSITIVE_INFINITY;
		dist_17 = (on_map_x_1 && on_map_y_7)? Math.min(dist_28, Math.min(dist_27, Math.min(dist_26, dist_16))) + 10. + rc.senseRubble(this_loc.translate(-4, 2)) : Double.POSITIVE_INFINITY;
		dist_31 = (on_map_x_3 && on_map_y_1)? Math.min(dist_42, Math.min(dist_41, Math.min(dist_32, dist_22))) + 10. + rc.senseRubble(this_loc.translate(-2, -4)) : Double.POSITIVE_INFINITY;
		dist_39 = (on_map_x_3 && on_map_y_9)? Math.min(dist_49, Math.min(dist_48, Math.min(dist_38, dist_28))) + 10. + rc.senseRubble(this_loc.translate(-2, 4)) : Double.POSITIVE_INFINITY;
		dist_71 = (on_map_x_7 && on_map_y_1)? Math.min(dist_82, Math.min(dist_72, Math.min(dist_62, dist_61))) + 10. + rc.senseRubble(this_loc.translate(2, -4)) : Double.POSITIVE_INFINITY;
		dist_79 = (on_map_x_7 && on_map_y_9)? Math.min(dist_88, Math.min(dist_78, Math.min(dist_69, dist_68))) + 10. + rc.senseRubble(this_loc.translate(2, 4)) : Double.POSITIVE_INFINITY;
		dist_93 = (on_map_x_9 && on_map_y_3)? Math.min(dist_94, Math.min(dist_84, Math.min(dist_83, dist_82))) + 10. + rc.senseRubble(this_loc.translate(4, -2)) : Double.POSITIVE_INFINITY;
		dist_97 = (on_map_x_9 && on_map_y_7)? Math.min(dist_96, Math.min(dist_88, Math.min(dist_87, dist_86))) + 10. + rc.senseRubble(this_loc.translate(4, 2)) : Double.POSITIVE_INFINITY;
		encoded_dist_24 = dist_24 + 0.0224609375;
		encoded_dist_26 = dist_26 + 0.033203125;
		encoded_dist_42 = dist_42 + 0.0126953125;
		encoded_dist_48 = dist_48 + 0.044921875;
		encoded_dist_62 = dist_62 + 0.013671875;
		encoded_dist_68 = dist_68 + 0.0458984375;
		encoded_dist_84 = dist_84 + 0.025390625;
		encoded_dist_86 = dist_86 + 0.0361328125;
		encoded_dist_23 = dist_23 + 0.01708984375;
		encoded_dist_27 = dist_27 + 0.03857421875;
		encoded_dist_32 = dist_32 + 0.01220703125;
		encoded_dist_38 = dist_38 + 0.04443359375;
		encoded_dist_72 = dist_72 + 0.01416015625;
		encoded_dist_78 = dist_78 + 0.04638671875;
		encoded_dist_83 = dist_83 + 0.02001953125;
		encoded_dist_87 = dist_87 + 0.04150390625;
		encoded_dist_15 = dist_15 + 0.02734375;
		encoded_dist_51 = dist_51 + 0.0078125;
		encoded_dist_59 = dist_59 + 0.05078125;
		encoded_dist_95 = dist_95 + 0.03125;
		encoded_dist_14 = dist_14 + 0.02197265625;
		encoded_dist_16 = dist_16 + 0.03271484375;
		encoded_dist_41 = dist_41 + 0.00732421875;
		encoded_dist_49 = dist_49 + 0.05029296875;
		encoded_dist_61 = dist_61 + 0.00830078125;
		encoded_dist_69 = dist_69 + 0.05126953125;
		encoded_dist_94 = dist_94 + 0.02587890625;
		encoded_dist_96 = dist_96 + 0.03662109375;
		encoded_dist_22 = dist_22 + 0.01171875;
		encoded_dist_28 = dist_28 + 0.0439453125;
		encoded_dist_82 = dist_82 + 0.0146484375;
		encoded_dist_88 = dist_88 + 0.046875;
		encoded_dist_13 = dist_13 + 0.0166015625;
		encoded_dist_17 = dist_17 + 0.0380859375;
		encoded_dist_31 = dist_31 + 0.0068359375;
		encoded_dist_39 = dist_39 + 0.0498046875;
		encoded_dist_71 = dist_71 + 0.0087890625;
		encoded_dist_79 = dist_79 + 0.0517578125;
		encoded_dist_93 = dist_93 + 0.0205078125;
		encoded_dist_97 = dist_97 + 0.0419921875;
 }

    public static double query_cheap(MapLocation target_loc, double average_rubble, double wait_cost) throws GameActionException {
        compute_cheap_dists(wait_cost);
        MapLocation this_loc = Info.loc;
		if (this_loc.isWithinDistanceSquared(target_loc, 10)) {
			switch ((target_loc.x-Info.x)+11*(target_loc.y-Info.y)) {
				case 0: return encoded_dist_55;
				case -1: return rc.canMove(Math2.direction_to(-1, 0))? encoded_dist_55 + 0.4375 : wait_cost;
				case -11: return rc.canMove(Math2.direction_to(0, -1))? encoded_dist_55 + 0.5 : wait_cost;
				case 11: return rc.canMove(Math2.direction_to(0, 1))? encoded_dist_55 + 0.375 : wait_cost;
				case 1: return rc.canMove(Math2.direction_to(1, 0))? encoded_dist_55 + 0.3125 : wait_cost;
				case -12: return rc.canMove(Math2.direction_to(-1, -1))? encoded_dist_55 + 0.1875 : wait_cost;
				case 10: return rc.canMove(Math2.direction_to(-1, 1))? encoded_dist_55 + 0.125 : wait_cost;
				case -10: return rc.canMove(Math2.direction_to(1, -1))? encoded_dist_55 + 0.25 : wait_cost;
				case 12: return rc.canMove(Math2.direction_to(1, 1))? encoded_dist_55 + 0.0625 : wait_cost;
				case -2: return Math.min(encoded_dist_46, Math.min(encoded_dist_45, encoded_dist_44));
				case -22: return Math.min(encoded_dist_64, Math.min(encoded_dist_54, encoded_dist_44));
				case 22: return Math.min(encoded_dist_66, Math.min(encoded_dist_56, encoded_dist_46));
				case 2: return Math.min(encoded_dist_66, Math.min(encoded_dist_65, encoded_dist_64));
				case -13: return Math.min(encoded_dist_45, Math.min(encoded_dist_44, encoded_dist_35));
				case 9: return Math.min(encoded_dist_46, Math.min(encoded_dist_45, encoded_dist_35));
				case -23: return Math.min(encoded_dist_54, Math.min(encoded_dist_53, encoded_dist_44));
				case 21: return Math.min(encoded_dist_57, Math.min(encoded_dist_56, encoded_dist_46));
				case -21: return Math.min(encoded_dist_64, Math.min(encoded_dist_54, encoded_dist_53));
				case 23: return Math.min(encoded_dist_66, Math.min(encoded_dist_57, encoded_dist_56));
				case -9: return Math.min(encoded_dist_75, Math.min(encoded_dist_65, encoded_dist_64));
				case 13: return Math.min(encoded_dist_75, Math.min(encoded_dist_66, encoded_dist_65));
				case -24: return Math.min(encoded_dist_44, Math.min(encoded_dist_43, encoded_dist_34));
				case 20: return Math.min(encoded_dist_47, Math.min(encoded_dist_46, encoded_dist_36));
				case -20: return Math.min(encoded_dist_74, Math.min(encoded_dist_64, encoded_dist_63));
				case 24: return Math.min(encoded_dist_76, Math.min(encoded_dist_67, encoded_dist_66));
				case -3: return Math.min(encoded_dist_36, Math.min(encoded_dist_35, encoded_dist_34));
				case -33: return Math.min(encoded_dist_63, Math.min(encoded_dist_53, encoded_dist_43));
				case 33: return Math.min(encoded_dist_67, Math.min(encoded_dist_57, encoded_dist_47));
				case 3: return Math.min(encoded_dist_76, Math.min(encoded_dist_75, encoded_dist_74));
				case -14: return Math.min(encoded_dist_35, Math.min(encoded_dist_34, Math.min(encoded_dist_33, encoded_dist_25)));
				case 8: return Math.min(encoded_dist_37, Math.min(encoded_dist_36, Math.min(encoded_dist_35, encoded_dist_25)));
				case -34: return Math.min(encoded_dist_53, Math.min(encoded_dist_52, Math.min(encoded_dist_43, encoded_dist_33)));
				case 32: return Math.min(encoded_dist_58, Math.min(encoded_dist_57, Math.min(encoded_dist_47, encoded_dist_37)));
				case -32: return Math.min(encoded_dist_73, Math.min(encoded_dist_63, Math.min(encoded_dist_53, encoded_dist_52)));
				case 34: return Math.min(encoded_dist_77, Math.min(encoded_dist_67, Math.min(encoded_dist_58, encoded_dist_57)));
				case -8: return Math.min(encoded_dist_85, Math.min(encoded_dist_75, Math.min(encoded_dist_74, encoded_dist_73)));
				case 14: return Math.min(encoded_dist_85, Math.min(encoded_dist_77, Math.min(encoded_dist_76, encoded_dist_75)));
				default: throw new Error(String.valueOf((target_loc.x-Info.x)+11*(target_loc.y-Info.y)));
			}
		}
		else {
			double delay_factor = 10+average_rubble;
			return Math.min(encoded_dist_86 + (int)(delay_factor*Math.sqrt(this_loc.translate(3, 1).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_84 + (int)(delay_factor*Math.sqrt(this_loc.translate(3, -1).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_68 + (int)(delay_factor*Math.sqrt(this_loc.translate(1, 3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_62 + (int)(delay_factor*Math.sqrt(this_loc.translate(1, -3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_48 + (int)(delay_factor*Math.sqrt(this_loc.translate(-1, 3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_42 + (int)(delay_factor*Math.sqrt(this_loc.translate(-1, -3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_26 + (int)(delay_factor*Math.sqrt(this_loc.translate(-3, 1).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_24 + (int)(delay_factor*Math.sqrt(this_loc.translate(-3, -1).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_85 + (int)(delay_factor*Math.sqrt(this_loc.translate(3, 0).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_58 + (int)(delay_factor*Math.sqrt(this_loc.translate(0, 3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_52 + (int)(delay_factor*Math.sqrt(this_loc.translate(0, -3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_25 + (int)(delay_factor*Math.sqrt(this_loc.translate(-3, 0).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_77 + (int)(delay_factor*Math.sqrt(this_loc.translate(2, 2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_73 + (int)(delay_factor*Math.sqrt(this_loc.translate(2, -2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_37 + (int)(delay_factor*Math.sqrt(this_loc.translate(-2, 2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_33 + (int)(delay_factor*Math.sqrt(this_loc.translate(-2, -2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_76 + (int)(delay_factor*Math.sqrt(this_loc.translate(2, 1).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_74 + (int)(delay_factor*Math.sqrt(this_loc.translate(2, -1).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_67 + (int)(delay_factor*Math.sqrt(this_loc.translate(1, 2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_63 + (int)(delay_factor*Math.sqrt(this_loc.translate(1, -2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_47 + (int)(delay_factor*Math.sqrt(this_loc.translate(-1, 2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_43 + (int)(delay_factor*Math.sqrt(this_loc.translate(-1, -2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_36 + (int)(delay_factor*Math.sqrt(this_loc.translate(-2, 1).distanceSquaredTo(target_loc))),
			                encoded_dist_34 + (int)(delay_factor*Math.sqrt(this_loc.translate(-2, -1).distanceSquaredTo(target_loc))))))))))))))))))))))))));
		}
    }

    public static double query_expensive(MapLocation target_loc, double average_rubble, double wait_cost) throws GameActionException {
        MapLocation this_loc = Info.loc;
        if (this_loc.isWithinDistanceSquared(target_loc, 10)) {
            return query_cheap(target_loc, average_rubble, wait_cost);
        }
        compute_expensive_dists(wait_cost);
		if (this_loc.isWithinDistanceSquared(target_loc, 20)) {
			switch ((target_loc.x-Info.x)+11*(target_loc.y-Info.y)) {
				case 0: return encoded_dist_55;
				case -1: return rc.canMove(Math2.direction_to(-1, 0))? encoded_dist_55 + 0.4375 : wait_cost;
				case -11: return rc.canMove(Math2.direction_to(0, -1))? encoded_dist_55 + 0.5 : wait_cost;
				case 11: return rc.canMove(Math2.direction_to(0, 1))? encoded_dist_55 + 0.375 : wait_cost;
				case 1: return rc.canMove(Math2.direction_to(1, 0))? encoded_dist_55 + 0.3125 : wait_cost;
				case -12: return rc.canMove(Math2.direction_to(-1, -1))? encoded_dist_55 + 0.1875 : wait_cost;
				case 10: return rc.canMove(Math2.direction_to(-1, 1))? encoded_dist_55 + 0.125 : wait_cost;
				case -10: return rc.canMove(Math2.direction_to(1, -1))? encoded_dist_55 + 0.25 : wait_cost;
				case 12: return rc.canMove(Math2.direction_to(1, 1))? encoded_dist_55 + 0.0625 : wait_cost;
				case -2: return Math.min(encoded_dist_46, Math.min(encoded_dist_45, encoded_dist_44));
				case -22: return Math.min(encoded_dist_64, Math.min(encoded_dist_54, encoded_dist_44));
				case 22: return Math.min(encoded_dist_66, Math.min(encoded_dist_56, encoded_dist_46));
				case 2: return Math.min(encoded_dist_66, Math.min(encoded_dist_65, encoded_dist_64));
				case -13: return Math.min(encoded_dist_45, Math.min(encoded_dist_44, encoded_dist_35));
				case 9: return Math.min(encoded_dist_46, Math.min(encoded_dist_45, encoded_dist_35));
				case -23: return Math.min(encoded_dist_54, Math.min(encoded_dist_53, encoded_dist_44));
				case 21: return Math.min(encoded_dist_57, Math.min(encoded_dist_56, encoded_dist_46));
				case -21: return Math.min(encoded_dist_64, Math.min(encoded_dist_54, encoded_dist_53));
				case 23: return Math.min(encoded_dist_66, Math.min(encoded_dist_57, encoded_dist_56));
				case -9: return Math.min(encoded_dist_75, Math.min(encoded_dist_65, encoded_dist_64));
				case 13: return Math.min(encoded_dist_75, Math.min(encoded_dist_66, encoded_dist_65));
				case -24: return Math.min(encoded_dist_44, Math.min(encoded_dist_43, encoded_dist_34));
				case 20: return Math.min(encoded_dist_47, Math.min(encoded_dist_46, encoded_dist_36));
				case -20: return Math.min(encoded_dist_74, Math.min(encoded_dist_64, encoded_dist_63));
				case 24: return Math.min(encoded_dist_76, Math.min(encoded_dist_67, encoded_dist_66));
				case -3: return Math.min(encoded_dist_36, Math.min(encoded_dist_35, encoded_dist_34));
				case -33: return Math.min(encoded_dist_63, Math.min(encoded_dist_53, encoded_dist_43));
				case 33: return Math.min(encoded_dist_67, Math.min(encoded_dist_57, encoded_dist_47));
				case 3: return Math.min(encoded_dist_76, Math.min(encoded_dist_75, encoded_dist_74));
				case -14: return Math.min(encoded_dist_35, Math.min(encoded_dist_34, Math.min(encoded_dist_33, encoded_dist_25)));
				case 8: return Math.min(encoded_dist_37, Math.min(encoded_dist_36, Math.min(encoded_dist_35, encoded_dist_25)));
				case -34: return Math.min(encoded_dist_53, Math.min(encoded_dist_52, Math.min(encoded_dist_43, encoded_dist_33)));
				case 32: return Math.min(encoded_dist_58, Math.min(encoded_dist_57, Math.min(encoded_dist_47, encoded_dist_37)));
				case -32: return Math.min(encoded_dist_73, Math.min(encoded_dist_63, Math.min(encoded_dist_53, encoded_dist_52)));
				case 34: return Math.min(encoded_dist_77, Math.min(encoded_dist_67, Math.min(encoded_dist_58, encoded_dist_57)));
				case -8: return Math.min(encoded_dist_85, Math.min(encoded_dist_75, Math.min(encoded_dist_74, encoded_dist_73)));
				case 14: return Math.min(encoded_dist_85, Math.min(encoded_dist_77, Math.min(encoded_dist_76, encoded_dist_75)));
				case -25: return Math.min(encoded_dist_34, Math.min(encoded_dist_33, encoded_dist_24));
				case 19: return Math.min(encoded_dist_37, Math.min(encoded_dist_36, encoded_dist_26));
				case -35: return Math.min(encoded_dist_43, Math.min(encoded_dist_42, encoded_dist_33));
				case 31: return Math.min(encoded_dist_48, Math.min(encoded_dist_47, encoded_dist_37));
				case -31: return Math.min(encoded_dist_73, Math.min(encoded_dist_63, encoded_dist_62));
				case 35: return Math.min(encoded_dist_77, Math.min(encoded_dist_68, encoded_dist_67));
				case -19: return Math.min(encoded_dist_84, Math.min(encoded_dist_74, encoded_dist_73));
				case 25: return Math.min(encoded_dist_86, Math.min(encoded_dist_77, encoded_dist_76));
				case -4: return Math.min(encoded_dist_26, Math.min(encoded_dist_25, encoded_dist_24));
				case -44: return Math.min(encoded_dist_62, Math.min(encoded_dist_52, encoded_dist_42));
				case 44: return Math.min(encoded_dist_68, Math.min(encoded_dist_58, encoded_dist_48));
				case 4: return Math.min(encoded_dist_86, Math.min(encoded_dist_85, encoded_dist_84));
				case -15: return Math.min(encoded_dist_25, Math.min(encoded_dist_24, Math.min(encoded_dist_23, encoded_dist_15)));
				case 7: return Math.min(encoded_dist_27, Math.min(encoded_dist_26, Math.min(encoded_dist_25, encoded_dist_15)));
				case -45: return Math.min(encoded_dist_52, Math.min(encoded_dist_51, Math.min(encoded_dist_42, encoded_dist_32)));
				case 43: return Math.min(encoded_dist_59, Math.min(encoded_dist_58, Math.min(encoded_dist_48, encoded_dist_38)));
				case -43: return Math.min(encoded_dist_72, Math.min(encoded_dist_62, Math.min(encoded_dist_52, encoded_dist_51)));
				case 45: return Math.min(encoded_dist_78, Math.min(encoded_dist_68, Math.min(encoded_dist_59, encoded_dist_58)));
				case -7: return Math.min(encoded_dist_95, Math.min(encoded_dist_85, Math.min(encoded_dist_84, encoded_dist_83)));
				case 15: return Math.min(encoded_dist_95, Math.min(encoded_dist_87, Math.min(encoded_dist_86, encoded_dist_85)));
				case -36: return Math.min(encoded_dist_33, Math.min(encoded_dist_32, encoded_dist_23));
				case 30: return Math.min(encoded_dist_38, Math.min(encoded_dist_37, encoded_dist_27));
				case -30: return Math.min(encoded_dist_83, Math.min(encoded_dist_73, encoded_dist_72));
				case 36: return Math.min(encoded_dist_87, Math.min(encoded_dist_78, encoded_dist_77));
				case -26: return Math.min(encoded_dist_24, Math.min(encoded_dist_23, Math.min(encoded_dist_22, encoded_dist_14)));
				case 18: return Math.min(encoded_dist_28, Math.min(encoded_dist_27, Math.min(encoded_dist_26, encoded_dist_16)));
				case -46: return Math.min(encoded_dist_42, Math.min(encoded_dist_41, Math.min(encoded_dist_32, encoded_dist_22)));
				case 42: return Math.min(encoded_dist_49, Math.min(encoded_dist_48, Math.min(encoded_dist_38, encoded_dist_28)));
				case -42: return Math.min(encoded_dist_82, Math.min(encoded_dist_72, Math.min(encoded_dist_62, encoded_dist_61)));
				case 46: return Math.min(encoded_dist_88, Math.min(encoded_dist_78, Math.min(encoded_dist_69, encoded_dist_68)));
				case -18: return Math.min(encoded_dist_94, Math.min(encoded_dist_84, Math.min(encoded_dist_83, encoded_dist_82)));
				case 26: return Math.min(encoded_dist_96, Math.min(encoded_dist_88, Math.min(encoded_dist_87, encoded_dist_86)));
				default: throw new Error(String.valueOf((target_loc.x-Info.x)+11*(target_loc.y-Info.y)));
			}
		}
		else {
			double delay_factor = 10+average_rubble;
			return Math.min(encoded_dist_97 + (int)(delay_factor*Math.sqrt(this_loc.translate(4, 2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_93 + (int)(delay_factor*Math.sqrt(this_loc.translate(4, -2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_79 + (int)(delay_factor*Math.sqrt(this_loc.translate(2, 4).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_71 + (int)(delay_factor*Math.sqrt(this_loc.translate(2, -4).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_39 + (int)(delay_factor*Math.sqrt(this_loc.translate(-2, 4).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_31 + (int)(delay_factor*Math.sqrt(this_loc.translate(-2, -4).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_17 + (int)(delay_factor*Math.sqrt(this_loc.translate(-4, 2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_13 + (int)(delay_factor*Math.sqrt(this_loc.translate(-4, -2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_88 + (int)(delay_factor*Math.sqrt(this_loc.translate(3, 3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_82 + (int)(delay_factor*Math.sqrt(this_loc.translate(3, -3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_28 + (int)(delay_factor*Math.sqrt(this_loc.translate(-3, 3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_22 + (int)(delay_factor*Math.sqrt(this_loc.translate(-3, -3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_96 + (int)(delay_factor*Math.sqrt(this_loc.translate(4, 1).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_94 + (int)(delay_factor*Math.sqrt(this_loc.translate(4, -1).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_69 + (int)(delay_factor*Math.sqrt(this_loc.translate(1, 4).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_61 + (int)(delay_factor*Math.sqrt(this_loc.translate(1, -4).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_49 + (int)(delay_factor*Math.sqrt(this_loc.translate(-1, 4).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_41 + (int)(delay_factor*Math.sqrt(this_loc.translate(-1, -4).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_16 + (int)(delay_factor*Math.sqrt(this_loc.translate(-4, 1).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_14 + (int)(delay_factor*Math.sqrt(this_loc.translate(-4, -1).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_95 + (int)(delay_factor*Math.sqrt(this_loc.translate(4, 0).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_59 + (int)(delay_factor*Math.sqrt(this_loc.translate(0, 4).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_51 + (int)(delay_factor*Math.sqrt(this_loc.translate(0, -4).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_15 + (int)(delay_factor*Math.sqrt(this_loc.translate(-4, 0).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_87 + (int)(delay_factor*Math.sqrt(this_loc.translate(3, 2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_83 + (int)(delay_factor*Math.sqrt(this_loc.translate(3, -2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_78 + (int)(delay_factor*Math.sqrt(this_loc.translate(2, 3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_72 + (int)(delay_factor*Math.sqrt(this_loc.translate(2, -3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_38 + (int)(delay_factor*Math.sqrt(this_loc.translate(-2, 3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_32 + (int)(delay_factor*Math.sqrt(this_loc.translate(-2, -3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_27 + (int)(delay_factor*Math.sqrt(this_loc.translate(-3, 2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_23 + (int)(delay_factor*Math.sqrt(this_loc.translate(-3, -2).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_86 + (int)(delay_factor*Math.sqrt(this_loc.translate(3, 1).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_84 + (int)(delay_factor*Math.sqrt(this_loc.translate(3, -1).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_68 + (int)(delay_factor*Math.sqrt(this_loc.translate(1, 3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_62 + (int)(delay_factor*Math.sqrt(this_loc.translate(1, -3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_48 + (int)(delay_factor*Math.sqrt(this_loc.translate(-1, 3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_42 + (int)(delay_factor*Math.sqrt(this_loc.translate(-1, -3).distanceSquaredTo(target_loc))),
			       Math.min(encoded_dist_26 + (int)(delay_factor*Math.sqrt(this_loc.translate(-3, 1).distanceSquaredTo(target_loc))),
			                encoded_dist_24 + (int)(delay_factor*Math.sqrt(this_loc.translate(-3, -1).distanceSquaredTo(target_loc))))))))))))))))))))))))))))))))))))))))));
		}
   }

    public static double query(MapLocation target_loc, double average_rubble, double wait_cost, boolean expensive) throws GameActionException {
        wait_cost = (int)(wait_cost*10) + 2.2351741790771484e-07;
        if (expensive) {
            return query_expensive(target_loc, average_rubble, wait_cost);
        }
        else {
            return query_cheap(target_loc, average_rubble, wait_cost);
        }
    }

    public static MapLocation end_location(double dist) {
        if (dist==Double.POSITIVE_INFINITY) {
            return Info.loc;
        }
        int offset_number = (int)(dist * 2048 % 128);
        int dx = offset_number % 11 - 5;
        int dy = offset_number / 11 - 5;
        return Info.loc.translate(dx, dy);
    }

    public static Direction direction(double dist) {
        if (dist==Double.POSITIVE_INFINITY) {
            return Direction.CENTER;
        }
        return DIRECTIONS[(int)(dist * 16 % 16)];
    }
}
