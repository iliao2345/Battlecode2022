package early_aggro_26;
import battlecode.common.*;

public class Bfs {
    public static RobotController rc;

    public static Direction way_to(MapLocation target_loc, double average_rubble, boolean expensive) throws GameActionException {
        double cost = expensive? cost_to_expensive(target_loc, average_rubble) : cost_to_cheap(target_loc, average_rubble);
        if (cost == Double.MAX_VALUE || cost%1==0) {
            return Direction.CENTER;
        }
        else {
            return Math2.UNIT_DIRECTIONS[(int)(cost*16)%16-1];
        }
    }
    public static double cost_to_expensive(MapLocation target_loc, double average_rubble) throws GameActionException {
        MapLocation this_loc = Info.loc;
        double delay_factor = 10+average_rubble;
        double dist_55 = 0;
		MapLocation loc_55 = this_loc.translate(0, 0);
		MapLocation loc_45 = this_loc.translate(-1, 0);
		MapLocation loc_54 = this_loc.translate(0, -1);
		MapLocation loc_56 = this_loc.translate(0, 1);
		MapLocation loc_65 = this_loc.translate(1, 0);
		MapLocation loc_44 = this_loc.translate(-1, -1);
		MapLocation loc_46 = this_loc.translate(-1, 1);
		MapLocation loc_64 = this_loc.translate(1, -1);
		MapLocation loc_66 = this_loc.translate(1, 1);
		MapLocation loc_35 = this_loc.translate(-2, 0);
		MapLocation loc_53 = this_loc.translate(0, -2);
		MapLocation loc_57 = this_loc.translate(0, 2);
		MapLocation loc_75 = this_loc.translate(2, 0);
		MapLocation loc_34 = this_loc.translate(-2, -1);
		MapLocation loc_36 = this_loc.translate(-2, 1);
		MapLocation loc_43 = this_loc.translate(-1, -2);
		MapLocation loc_47 = this_loc.translate(-1, 2);
		MapLocation loc_63 = this_loc.translate(1, -2);
		MapLocation loc_67 = this_loc.translate(1, 2);
		MapLocation loc_74 = this_loc.translate(2, -1);
		MapLocation loc_76 = this_loc.translate(2, 1);
		MapLocation loc_33 = this_loc.translate(-2, -2);
		MapLocation loc_37 = this_loc.translate(-2, 2);
		MapLocation loc_73 = this_loc.translate(2, -2);
		MapLocation loc_77 = this_loc.translate(2, 2);
		MapLocation loc_25 = this_loc.translate(-3, 0);
		MapLocation loc_52 = this_loc.translate(0, -3);
		MapLocation loc_58 = this_loc.translate(0, 3);
		MapLocation loc_85 = this_loc.translate(3, 0);
		MapLocation loc_24 = this_loc.translate(-3, -1);
		MapLocation loc_26 = this_loc.translate(-3, 1);
		MapLocation loc_42 = this_loc.translate(-1, -3);
		MapLocation loc_48 = this_loc.translate(-1, 3);
		MapLocation loc_62 = this_loc.translate(1, -3);
		MapLocation loc_68 = this_loc.translate(1, 3);
		MapLocation loc_84 = this_loc.translate(3, -1);
		MapLocation loc_86 = this_loc.translate(3, 1);
		MapLocation loc_23 = this_loc.translate(-3, -2);
		MapLocation loc_27 = this_loc.translate(-3, 2);
		MapLocation loc_32 = this_loc.translate(-2, -3);
		MapLocation loc_38 = this_loc.translate(-2, 3);
		MapLocation loc_72 = this_loc.translate(2, -3);
		MapLocation loc_78 = this_loc.translate(2, 3);
		MapLocation loc_83 = this_loc.translate(3, -2);
		MapLocation loc_87 = this_loc.translate(3, 2);
		MapLocation loc_15 = this_loc.translate(-4, 0);
		MapLocation loc_51 = this_loc.translate(0, -4);
		MapLocation loc_59 = this_loc.translate(0, 4);
		MapLocation loc_95 = this_loc.translate(4, 0);
		MapLocation loc_14 = this_loc.translate(-4, -1);
		MapLocation loc_16 = this_loc.translate(-4, 1);
		MapLocation loc_41 = this_loc.translate(-1, -4);
		MapLocation loc_49 = this_loc.translate(-1, 4);
		MapLocation loc_61 = this_loc.translate(1, -4);
		MapLocation loc_69 = this_loc.translate(1, 4);
		MapLocation loc_94 = this_loc.translate(4, -1);
		MapLocation loc_96 = this_loc.translate(4, 1);
		MapLocation loc_22 = this_loc.translate(-3, -3);
		MapLocation loc_28 = this_loc.translate(-3, 3);
		MapLocation loc_82 = this_loc.translate(3, -3);
		MapLocation loc_88 = this_loc.translate(3, 3);
		MapLocation loc_13 = this_loc.translate(-4, -2);
		MapLocation loc_17 = this_loc.translate(-4, 2);
		MapLocation loc_31 = this_loc.translate(-2, -4);
		MapLocation loc_39 = this_loc.translate(-2, 4);
		MapLocation loc_71 = this_loc.translate(2, -4);
		MapLocation loc_79 = this_loc.translate(2, 4);
		MapLocation loc_93 = this_loc.translate(4, -2);
		MapLocation loc_97 = this_loc.translate(4, 2);
		double dist_45 = rc.onTheMap(loc_45)? dist_55 + (rc.canMove(Math2.direction_to(-1, 0))? 10.3125 : 20) + rc.senseRubble(loc_45) : Double.MAX_VALUE;
		double dist_54 = rc.onTheMap(loc_54)? dist_55 + (rc.canMove(Math2.direction_to(0, -1))? 10.4375 : 20) + rc.senseRubble(loc_54) : Double.MAX_VALUE;
		double dist_56 = rc.onTheMap(loc_56)? dist_55 + (rc.canMove(Math2.direction_to(0, 1))? 10.1875 : 20) + rc.senseRubble(loc_56) : Double.MAX_VALUE;
		double dist_65 = rc.onTheMap(loc_65)? dist_55 + (rc.canMove(Math2.direction_to(1, 0))? 10.0625 : 20) + rc.senseRubble(loc_65) : Double.MAX_VALUE;
		double dist_44 = rc.onTheMap(loc_44)? Math.min(dist_55, Math.min(dist_54, dist_45)) + (rc.canMove(Math2.direction_to(-1, -1))? 10.375 : 20) + rc.senseRubble(loc_44) : Double.MAX_VALUE;
		double dist_46 = rc.onTheMap(loc_46)? Math.min(dist_56, Math.min(dist_55, dist_45)) + (rc.canMove(Math2.direction_to(-1, 1))? 10.25 : 20) + rc.senseRubble(loc_46) : Double.MAX_VALUE;
		double dist_64 = rc.onTheMap(loc_64)? Math.min(dist_65, Math.min(dist_55, dist_54)) + (rc.canMove(Math2.direction_to(1, -1))? 10.5 : 20) + rc.senseRubble(loc_64) : Double.MAX_VALUE;
		double dist_66 = rc.onTheMap(loc_66)? Math.min(dist_65, Math.min(dist_56, dist_55)) + (rc.canMove(Math2.direction_to(1, 1))? 10.125 : 20) + rc.senseRubble(loc_66) : Double.MAX_VALUE;
		double dist_35 = rc.onTheMap(loc_35)? Math.min(dist_46, Math.min(dist_45, dist_44)) + 10 + rc.senseRubble(loc_35) : Double.MAX_VALUE;
		double dist_53 = rc.onTheMap(loc_53)? Math.min(dist_64, Math.min(dist_54, dist_44)) + 10 + rc.senseRubble(loc_53) : Double.MAX_VALUE;
		double dist_57 = rc.onTheMap(loc_57)? Math.min(dist_66, Math.min(dist_56, dist_46)) + 10 + rc.senseRubble(loc_57) : Double.MAX_VALUE;
		double dist_75 = rc.onTheMap(loc_75)? Math.min(dist_66, Math.min(dist_65, dist_64)) + 10 + rc.senseRubble(loc_75) : Double.MAX_VALUE;
		double dist_34 = rc.onTheMap(loc_34)? Math.min(dist_45, Math.min(dist_44, dist_35)) + 10 + rc.senseRubble(loc_34) : Double.MAX_VALUE;
		double dist_36 = rc.onTheMap(loc_36)? Math.min(dist_46, Math.min(dist_45, dist_35)) + 10 + rc.senseRubble(loc_36) : Double.MAX_VALUE;
		double dist_43 = rc.onTheMap(loc_43)? Math.min(dist_54, Math.min(dist_53, dist_44)) + 10 + rc.senseRubble(loc_43) : Double.MAX_VALUE;
		double dist_47 = rc.onTheMap(loc_47)? Math.min(dist_57, Math.min(dist_56, dist_46)) + 10 + rc.senseRubble(loc_47) : Double.MAX_VALUE;
		double dist_63 = rc.onTheMap(loc_63)? Math.min(dist_64, Math.min(dist_54, dist_53)) + 10 + rc.senseRubble(loc_63) : Double.MAX_VALUE;
		double dist_67 = rc.onTheMap(loc_67)? Math.min(dist_66, Math.min(dist_57, dist_56)) + 10 + rc.senseRubble(loc_67) : Double.MAX_VALUE;
		double dist_74 = rc.onTheMap(loc_74)? Math.min(dist_75, Math.min(dist_65, dist_64)) + 10 + rc.senseRubble(loc_74) : Double.MAX_VALUE;
		double dist_76 = rc.onTheMap(loc_76)? Math.min(dist_75, Math.min(dist_66, dist_65)) + 10 + rc.senseRubble(loc_76) : Double.MAX_VALUE;
		double dist_33 = rc.onTheMap(loc_33)? Math.min(dist_44, Math.min(dist_43, dist_34)) + 10 + rc.senseRubble(loc_33) : Double.MAX_VALUE;
		double dist_37 = rc.onTheMap(loc_37)? Math.min(dist_47, Math.min(dist_46, dist_36)) + 10 + rc.senseRubble(loc_37) : Double.MAX_VALUE;
		double dist_73 = rc.onTheMap(loc_73)? Math.min(dist_74, Math.min(dist_64, dist_63)) + 10 + rc.senseRubble(loc_73) : Double.MAX_VALUE;
		double dist_77 = rc.onTheMap(loc_77)? Math.min(dist_76, Math.min(dist_67, dist_66)) + 10 + rc.senseRubble(loc_77) : Double.MAX_VALUE;
		double dist_25 = rc.onTheMap(loc_25)? Math.min(dist_36, Math.min(dist_35, dist_34)) + 10 + rc.senseRubble(loc_25) : Double.MAX_VALUE;
		double dist_52 = rc.onTheMap(loc_52)? Math.min(dist_63, Math.min(dist_53, dist_43)) + 10 + rc.senseRubble(loc_52) : Double.MAX_VALUE;
		double dist_58 = rc.onTheMap(loc_58)? Math.min(dist_67, Math.min(dist_57, dist_47)) + 10 + rc.senseRubble(loc_58) : Double.MAX_VALUE;
		double dist_85 = rc.onTheMap(loc_85)? Math.min(dist_76, Math.min(dist_75, dist_74)) + 10 + rc.senseRubble(loc_85) : Double.MAX_VALUE;
		double dist_24 = rc.onTheMap(loc_24)? Math.min(dist_35, Math.min(dist_34, Math.min(dist_33, dist_25))) + 10 + rc.senseRubble(loc_24) : Double.MAX_VALUE;
		double dist_26 = rc.onTheMap(loc_26)? Math.min(dist_37, Math.min(dist_36, Math.min(dist_35, dist_25))) + 10 + rc.senseRubble(loc_26) : Double.MAX_VALUE;
		double dist_42 = rc.onTheMap(loc_42)? Math.min(dist_53, Math.min(dist_52, Math.min(dist_43, dist_33))) + 10 + rc.senseRubble(loc_42) : Double.MAX_VALUE;
		double dist_48 = rc.onTheMap(loc_48)? Math.min(dist_58, Math.min(dist_57, Math.min(dist_47, dist_37))) + 10 + rc.senseRubble(loc_48) : Double.MAX_VALUE;
		double dist_62 = rc.onTheMap(loc_62)? Math.min(dist_73, Math.min(dist_63, Math.min(dist_53, dist_52))) + 10 + rc.senseRubble(loc_62) : Double.MAX_VALUE;
		double dist_68 = rc.onTheMap(loc_68)? Math.min(dist_77, Math.min(dist_67, Math.min(dist_58, dist_57))) + 10 + rc.senseRubble(loc_68) : Double.MAX_VALUE;
		double dist_84 = rc.onTheMap(loc_84)? Math.min(dist_85, Math.min(dist_75, Math.min(dist_74, dist_73))) + 10 + rc.senseRubble(loc_84) : Double.MAX_VALUE;
		double dist_86 = rc.onTheMap(loc_86)? Math.min(dist_85, Math.min(dist_77, Math.min(dist_76, dist_75))) + 10 + rc.senseRubble(loc_86) : Double.MAX_VALUE;
		double dist_23 = rc.onTheMap(loc_23)? Math.min(dist_34, Math.min(dist_33, dist_24)) + 10 + rc.senseRubble(loc_23) + (int)(delay_factor*Math.sqrt(loc_23.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_27 = rc.onTheMap(loc_27)? Math.min(dist_37, Math.min(dist_36, dist_26)) + 10 + rc.senseRubble(loc_27) + (int)(delay_factor*Math.sqrt(loc_27.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_32 = rc.onTheMap(loc_32)? Math.min(dist_43, Math.min(dist_42, dist_33)) + 10 + rc.senseRubble(loc_32) + (int)(delay_factor*Math.sqrt(loc_32.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_38 = rc.onTheMap(loc_38)? Math.min(dist_48, Math.min(dist_47, dist_37)) + 10 + rc.senseRubble(loc_38) + (int)(delay_factor*Math.sqrt(loc_38.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_72 = rc.onTheMap(loc_72)? Math.min(dist_73, Math.min(dist_63, dist_62)) + 10 + rc.senseRubble(loc_72) + (int)(delay_factor*Math.sqrt(loc_72.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_78 = rc.onTheMap(loc_78)? Math.min(dist_77, Math.min(dist_68, dist_67)) + 10 + rc.senseRubble(loc_78) + (int)(delay_factor*Math.sqrt(loc_78.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_83 = rc.onTheMap(loc_83)? Math.min(dist_84, Math.min(dist_74, dist_73)) + 10 + rc.senseRubble(loc_83) + (int)(delay_factor*Math.sqrt(loc_83.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_87 = rc.onTheMap(loc_87)? Math.min(dist_86, Math.min(dist_77, dist_76)) + 10 + rc.senseRubble(loc_87) + (int)(delay_factor*Math.sqrt(loc_87.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_15 = rc.onTheMap(loc_15)? Math.min(dist_26, Math.min(dist_25, dist_24)) + 10 + rc.senseRubble(loc_15) + (int)(delay_factor*Math.sqrt(loc_15.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_51 = rc.onTheMap(loc_51)? Math.min(dist_62, Math.min(dist_52, dist_42)) + 10 + rc.senseRubble(loc_51) + (int)(delay_factor*Math.sqrt(loc_51.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_59 = rc.onTheMap(loc_59)? Math.min(dist_68, Math.min(dist_58, dist_48)) + 10 + rc.senseRubble(loc_59) + (int)(delay_factor*Math.sqrt(loc_59.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_95 = rc.onTheMap(loc_95)? Math.min(dist_86, Math.min(dist_85, dist_84)) + 10 + rc.senseRubble(loc_95) + (int)(delay_factor*Math.sqrt(loc_95.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_14 = rc.onTheMap(loc_14)? Math.min(dist_25, Math.min(dist_24, Math.min(dist_23, dist_15))) + 10 + rc.senseRubble(loc_14) + (int)(delay_factor*Math.sqrt(loc_14.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_16 = rc.onTheMap(loc_16)? Math.min(dist_27, Math.min(dist_26, Math.min(dist_25, dist_15))) + 10 + rc.senseRubble(loc_16) + (int)(delay_factor*Math.sqrt(loc_16.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_41 = rc.onTheMap(loc_41)? Math.min(dist_52, Math.min(dist_51, Math.min(dist_42, dist_32))) + 10 + rc.senseRubble(loc_41) + (int)(delay_factor*Math.sqrt(loc_41.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_49 = rc.onTheMap(loc_49)? Math.min(dist_59, Math.min(dist_58, Math.min(dist_48, dist_38))) + 10 + rc.senseRubble(loc_49) + (int)(delay_factor*Math.sqrt(loc_49.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_61 = rc.onTheMap(loc_61)? Math.min(dist_72, Math.min(dist_62, Math.min(dist_52, dist_51))) + 10 + rc.senseRubble(loc_61) + (int)(delay_factor*Math.sqrt(loc_61.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_69 = rc.onTheMap(loc_69)? Math.min(dist_78, Math.min(dist_68, Math.min(dist_59, dist_58))) + 10 + rc.senseRubble(loc_69) + (int)(delay_factor*Math.sqrt(loc_69.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_94 = rc.onTheMap(loc_94)? Math.min(dist_95, Math.min(dist_85, Math.min(dist_84, dist_83))) + 10 + rc.senseRubble(loc_94) + (int)(delay_factor*Math.sqrt(loc_94.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_96 = rc.onTheMap(loc_96)? Math.min(dist_95, Math.min(dist_87, Math.min(dist_86, dist_85))) + 10 + rc.senseRubble(loc_96) + (int)(delay_factor*Math.sqrt(loc_96.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_22 = rc.onTheMap(loc_22)? Math.min(dist_33, Math.min(dist_32, dist_23)) + 10 + rc.senseRubble(loc_22) + (int)(delay_factor*Math.sqrt(loc_22.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_28 = rc.onTheMap(loc_28)? Math.min(dist_38, Math.min(dist_37, dist_27)) + 10 + rc.senseRubble(loc_28) + (int)(delay_factor*Math.sqrt(loc_28.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_82 = rc.onTheMap(loc_82)? Math.min(dist_83, Math.min(dist_73, dist_72)) + 10 + rc.senseRubble(loc_82) + (int)(delay_factor*Math.sqrt(loc_82.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_88 = rc.onTheMap(loc_88)? Math.min(dist_87, Math.min(dist_78, dist_77)) + 10 + rc.senseRubble(loc_88) + (int)(delay_factor*Math.sqrt(loc_88.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_13 = rc.onTheMap(loc_13)? Math.min(dist_24, Math.min(dist_23, Math.min(dist_22, dist_14))) + 10 + rc.senseRubble(loc_13) + (int)(delay_factor*Math.sqrt(loc_13.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_17 = rc.onTheMap(loc_17)? Math.min(dist_28, Math.min(dist_27, Math.min(dist_26, dist_16))) + 10 + rc.senseRubble(loc_17) + (int)(delay_factor*Math.sqrt(loc_17.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_31 = rc.onTheMap(loc_31)? Math.min(dist_42, Math.min(dist_41, Math.min(dist_32, dist_22))) + 10 + rc.senseRubble(loc_31) + (int)(delay_factor*Math.sqrt(loc_31.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_39 = rc.onTheMap(loc_39)? Math.min(dist_49, Math.min(dist_48, Math.min(dist_38, dist_28))) + 10 + rc.senseRubble(loc_39) + (int)(delay_factor*Math.sqrt(loc_39.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_71 = rc.onTheMap(loc_71)? Math.min(dist_82, Math.min(dist_72, Math.min(dist_62, dist_61))) + 10 + rc.senseRubble(loc_71) + (int)(delay_factor*Math.sqrt(loc_71.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_79 = rc.onTheMap(loc_79)? Math.min(dist_88, Math.min(dist_78, Math.min(dist_69, dist_68))) + 10 + rc.senseRubble(loc_79) + (int)(delay_factor*Math.sqrt(loc_79.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_93 = rc.onTheMap(loc_93)? Math.min(dist_94, Math.min(dist_84, Math.min(dist_83, dist_82))) + 10 + rc.senseRubble(loc_93) + (int)(delay_factor*Math.sqrt(loc_93.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_97 = rc.onTheMap(loc_97)? Math.min(dist_96, Math.min(dist_88, Math.min(dist_87, dist_86))) + 10 + rc.senseRubble(loc_97) + (int)(delay_factor*Math.sqrt(loc_97.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		if (this_loc.isWithinDistanceSquared(target_loc, 20)) {
			switch ((target_loc.x-Info.x)+11*(target_loc.y-Info.y)) {
				case 0: return Double.MAX_VALUE;
				case -1: return dist_45;
				case -11: return dist_54;
				case 11: return dist_56;
				case 1: return dist_65;
				case -12: return dist_44;
				case 10: return dist_46;
				case -10: return dist_64;
				case 12: return dist_66;
				case -2: return dist_35;
				case -22: return dist_53;
				case 22: return dist_57;
				case 2: return dist_75;
				case -13: return dist_34;
				case 9: return dist_36;
				case -23: return dist_43;
				case 21: return dist_47;
				case -21: return dist_63;
				case 23: return dist_67;
				case -9: return dist_74;
				case 13: return dist_76;
				case -24: return dist_33;
				case 20: return dist_37;
				case -20: return dist_73;
				case 24: return dist_77;
				case -3: return dist_25;
				case -33: return dist_52;
				case 33: return dist_58;
				case 3: return dist_85;
				case -14: return dist_24;
				case 8: return dist_26;
				case -34: return dist_42;
				case 32: return dist_48;
				case -32: return dist_62;
				case 34: return dist_68;
				case -8: return dist_84;
				case 14: return dist_86;
				case -25: return dist_23;
				case 19: return dist_27;
				case -35: return dist_32;
				case 31: return dist_38;
				case -31: return dist_72;
				case 35: return dist_78;
				case -19: return dist_83;
				case 25: return dist_87;
				case -4: return dist_15;
				case -44: return dist_51;
				case 44: return dist_59;
				case 4: return dist_95;
				case -15: return dist_14;
				case 7: return dist_16;
				case -45: return dist_41;
				case 43: return dist_49;
				case -43: return dist_61;
				case 45: return dist_69;
				case -7: return dist_94;
				case 15: return dist_96;
				case -36: return dist_22;
				case 30: return dist_28;
				case -30: return dist_82;
				case 36: return dist_88;
				case -26: return dist_13;
				case 18: return dist_17;
				case -46: return dist_31;
				case 42: return dist_39;
				case -42: return dist_71;
				case 46: return dist_79;
				case -18: return dist_93;
				case 26: return dist_97;
				default: throw new Error(String.valueOf((target_loc.x-Info.x)+11*(target_loc.y-Info.y)));
			}
		}
		else {
			return Math.min(dist_97, Math.min(dist_93, Math.min(dist_79, Math.min(dist_71, Math.min(dist_39, Math.min(dist_31, Math.min(dist_17, Math.min(dist_13, Math.min(dist_88, Math.min(dist_82, Math.min(dist_28, Math.min(dist_22, Math.min(dist_96, Math.min(dist_94, Math.min(dist_69, Math.min(dist_61, Math.min(dist_49, Math.min(dist_41, Math.min(dist_16, Math.min(dist_14, Math.min(dist_95, Math.min(dist_59, Math.min(dist_51, Math.min(dist_15, Math.min(dist_87, Math.min(dist_83, Math.min(dist_78, Math.min(dist_72, Math.min(dist_38, Math.min(dist_32, Math.min(dist_27, dist_23)))))))))))))))))))))))))))))));
		}
    }
    public static double cost_to_cheap(MapLocation target_loc, double average_rubble) throws GameActionException {
        MapLocation this_loc = Info.loc;
        double delay_factor = 10+average_rubble;
        double dist_55 = 0;
		MapLocation loc_55 = this_loc.translate(0, 0);
		MapLocation loc_45 = this_loc.translate(-1, 0);
		MapLocation loc_54 = this_loc.translate(0, -1);
		MapLocation loc_56 = this_loc.translate(0, 1);
		MapLocation loc_65 = this_loc.translate(1, 0);
		MapLocation loc_44 = this_loc.translate(-1, -1);
		MapLocation loc_46 = this_loc.translate(-1, 1);
		MapLocation loc_64 = this_loc.translate(1, -1);
		MapLocation loc_66 = this_loc.translate(1, 1);
		MapLocation loc_35 = this_loc.translate(-2, 0);
		MapLocation loc_53 = this_loc.translate(0, -2);
		MapLocation loc_57 = this_loc.translate(0, 2);
		MapLocation loc_75 = this_loc.translate(2, 0);
		MapLocation loc_34 = this_loc.translate(-2, -1);
		MapLocation loc_36 = this_loc.translate(-2, 1);
		MapLocation loc_43 = this_loc.translate(-1, -2);
		MapLocation loc_47 = this_loc.translate(-1, 2);
		MapLocation loc_63 = this_loc.translate(1, -2);
		MapLocation loc_67 = this_loc.translate(1, 2);
		MapLocation loc_74 = this_loc.translate(2, -1);
		MapLocation loc_76 = this_loc.translate(2, 1);
		MapLocation loc_33 = this_loc.translate(-2, -2);
		MapLocation loc_37 = this_loc.translate(-2, 2);
		MapLocation loc_73 = this_loc.translate(2, -2);
		MapLocation loc_77 = this_loc.translate(2, 2);
		MapLocation loc_25 = this_loc.translate(-3, 0);
		MapLocation loc_52 = this_loc.translate(0, -3);
		MapLocation loc_58 = this_loc.translate(0, 3);
		MapLocation loc_85 = this_loc.translate(3, 0);
		MapLocation loc_24 = this_loc.translate(-3, -1);
		MapLocation loc_26 = this_loc.translate(-3, 1);
		MapLocation loc_42 = this_loc.translate(-1, -3);
		MapLocation loc_48 = this_loc.translate(-1, 3);
		MapLocation loc_62 = this_loc.translate(1, -3);
		MapLocation loc_68 = this_loc.translate(1, 3);
		MapLocation loc_84 = this_loc.translate(3, -1);
		MapLocation loc_86 = this_loc.translate(3, 1);
		double dist_45 = rc.onTheMap(loc_45)? dist_55 + (rc.canMove(Math2.direction_to(-1, 0))? 10.3125 : 20) + rc.senseRubble(loc_45) : Double.MAX_VALUE;
		double dist_54 = rc.onTheMap(loc_54)? dist_55 + (rc.canMove(Math2.direction_to(0, -1))? 10.4375 : 20) + rc.senseRubble(loc_54) : Double.MAX_VALUE;
		double dist_56 = rc.onTheMap(loc_56)? dist_55 + (rc.canMove(Math2.direction_to(0, 1))? 10.1875 : 20) + rc.senseRubble(loc_56) : Double.MAX_VALUE;
		double dist_65 = rc.onTheMap(loc_65)? dist_55 + (rc.canMove(Math2.direction_to(1, 0))? 10.0625 : 20) + rc.senseRubble(loc_65) : Double.MAX_VALUE;
		double dist_44 = rc.onTheMap(loc_44)? Math.min(dist_55, Math.min(dist_54, dist_45)) + (rc.canMove(Math2.direction_to(-1, -1))? 10.375 : 20) + rc.senseRubble(loc_44) : Double.MAX_VALUE;
		double dist_46 = rc.onTheMap(loc_46)? Math.min(dist_56, Math.min(dist_55, dist_45)) + (rc.canMove(Math2.direction_to(-1, 1))? 10.25 : 20) + rc.senseRubble(loc_46) : Double.MAX_VALUE;
		double dist_64 = rc.onTheMap(loc_64)? Math.min(dist_65, Math.min(dist_55, dist_54)) + (rc.canMove(Math2.direction_to(1, -1))? 10.5 : 20) + rc.senseRubble(loc_64) : Double.MAX_VALUE;
		double dist_66 = rc.onTheMap(loc_66)? Math.min(dist_65, Math.min(dist_56, dist_55)) + (rc.canMove(Math2.direction_to(1, 1))? 10.125 : 20) + rc.senseRubble(loc_66) : Double.MAX_VALUE;
		double dist_35 = rc.onTheMap(loc_35)? Math.min(dist_46, Math.min(dist_45, dist_44)) + 10 + rc.senseRubble(loc_35) : Double.MAX_VALUE;
		double dist_53 = rc.onTheMap(loc_53)? Math.min(dist_64, Math.min(dist_54, dist_44)) + 10 + rc.senseRubble(loc_53) : Double.MAX_VALUE;
		double dist_57 = rc.onTheMap(loc_57)? Math.min(dist_66, Math.min(dist_56, dist_46)) + 10 + rc.senseRubble(loc_57) : Double.MAX_VALUE;
		double dist_75 = rc.onTheMap(loc_75)? Math.min(dist_66, Math.min(dist_65, dist_64)) + 10 + rc.senseRubble(loc_75) : Double.MAX_VALUE;
		double dist_34 = rc.onTheMap(loc_34)? Math.min(dist_45, Math.min(dist_44, dist_35)) + 10 + rc.senseRubble(loc_34) + (int)(delay_factor*Math.sqrt(loc_34.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_36 = rc.onTheMap(loc_36)? Math.min(dist_46, Math.min(dist_45, dist_35)) + 10 + rc.senseRubble(loc_36) + (int)(delay_factor*Math.sqrt(loc_36.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_43 = rc.onTheMap(loc_43)? Math.min(dist_54, Math.min(dist_53, dist_44)) + 10 + rc.senseRubble(loc_43) + (int)(delay_factor*Math.sqrt(loc_43.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_47 = rc.onTheMap(loc_47)? Math.min(dist_57, Math.min(dist_56, dist_46)) + 10 + rc.senseRubble(loc_47) + (int)(delay_factor*Math.sqrt(loc_47.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_63 = rc.onTheMap(loc_63)? Math.min(dist_64, Math.min(dist_54, dist_53)) + 10 + rc.senseRubble(loc_63) + (int)(delay_factor*Math.sqrt(loc_63.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_67 = rc.onTheMap(loc_67)? Math.min(dist_66, Math.min(dist_57, dist_56)) + 10 + rc.senseRubble(loc_67) + (int)(delay_factor*Math.sqrt(loc_67.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_74 = rc.onTheMap(loc_74)? Math.min(dist_75, Math.min(dist_65, dist_64)) + 10 + rc.senseRubble(loc_74) + (int)(delay_factor*Math.sqrt(loc_74.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_76 = rc.onTheMap(loc_76)? Math.min(dist_75, Math.min(dist_66, dist_65)) + 10 + rc.senseRubble(loc_76) + (int)(delay_factor*Math.sqrt(loc_76.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_33 = rc.onTheMap(loc_33)? Math.min(dist_44, Math.min(dist_43, dist_34)) + 10 + rc.senseRubble(loc_33) + (int)(delay_factor*Math.sqrt(loc_33.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_37 = rc.onTheMap(loc_37)? Math.min(dist_47, Math.min(dist_46, dist_36)) + 10 + rc.senseRubble(loc_37) + (int)(delay_factor*Math.sqrt(loc_37.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_73 = rc.onTheMap(loc_73)? Math.min(dist_74, Math.min(dist_64, dist_63)) + 10 + rc.senseRubble(loc_73) + (int)(delay_factor*Math.sqrt(loc_73.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_77 = rc.onTheMap(loc_77)? Math.min(dist_76, Math.min(dist_67, dist_66)) + 10 + rc.senseRubble(loc_77) + (int)(delay_factor*Math.sqrt(loc_77.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_25 = rc.onTheMap(loc_25)? Math.min(dist_36, Math.min(dist_35, dist_34)) + 10 + rc.senseRubble(loc_25) + (int)(delay_factor*Math.sqrt(loc_25.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_52 = rc.onTheMap(loc_52)? Math.min(dist_63, Math.min(dist_53, dist_43)) + 10 + rc.senseRubble(loc_52) + (int)(delay_factor*Math.sqrt(loc_52.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_58 = rc.onTheMap(loc_58)? Math.min(dist_67, Math.min(dist_57, dist_47)) + 10 + rc.senseRubble(loc_58) + (int)(delay_factor*Math.sqrt(loc_58.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_85 = rc.onTheMap(loc_85)? Math.min(dist_76, Math.min(dist_75, dist_74)) + 10 + rc.senseRubble(loc_85) + (int)(delay_factor*Math.sqrt(loc_85.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_24 = rc.onTheMap(loc_24)? Math.min(dist_35, Math.min(dist_34, Math.min(dist_33, dist_25))) + 10 + rc.senseRubble(loc_24) + (int)(delay_factor*Math.sqrt(loc_24.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_26 = rc.onTheMap(loc_26)? Math.min(dist_37, Math.min(dist_36, Math.min(dist_35, dist_25))) + 10 + rc.senseRubble(loc_26) + (int)(delay_factor*Math.sqrt(loc_26.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_42 = rc.onTheMap(loc_42)? Math.min(dist_53, Math.min(dist_52, Math.min(dist_43, dist_33))) + 10 + rc.senseRubble(loc_42) + (int)(delay_factor*Math.sqrt(loc_42.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_48 = rc.onTheMap(loc_48)? Math.min(dist_58, Math.min(dist_57, Math.min(dist_47, dist_37))) + 10 + rc.senseRubble(loc_48) + (int)(delay_factor*Math.sqrt(loc_48.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_62 = rc.onTheMap(loc_62)? Math.min(dist_73, Math.min(dist_63, Math.min(dist_53, dist_52))) + 10 + rc.senseRubble(loc_62) + (int)(delay_factor*Math.sqrt(loc_62.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_68 = rc.onTheMap(loc_68)? Math.min(dist_77, Math.min(dist_67, Math.min(dist_58, dist_57))) + 10 + rc.senseRubble(loc_68) + (int)(delay_factor*Math.sqrt(loc_68.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_84 = rc.onTheMap(loc_84)? Math.min(dist_85, Math.min(dist_75, Math.min(dist_74, dist_73))) + 10 + rc.senseRubble(loc_84) + (int)(delay_factor*Math.sqrt(loc_84.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		double dist_86 = rc.onTheMap(loc_86)? Math.min(dist_85, Math.min(dist_77, Math.min(dist_76, dist_75))) + 10 + rc.senseRubble(loc_86) + (int)(delay_factor*Math.sqrt(loc_86.distanceSquaredTo(target_loc))) : Double.MAX_VALUE;
		if (this_loc.isWithinDistanceSquared(target_loc, 10)) {
			switch ((target_loc.x-Info.x)+11*(target_loc.y-Info.y)) {
				case 0: return Double.MAX_VALUE;
				case -1: return dist_45;
				case -11: return dist_54;
				case 11: return dist_56;
				case 1: return dist_65;
				case -12: return dist_44;
				case 10: return dist_46;
				case -10: return dist_64;
				case 12: return dist_66;
				case -2: return dist_35;
				case -22: return dist_53;
				case 22: return dist_57;
				case 2: return dist_75;
				case -13: return dist_34;
				case 9: return dist_36;
				case -23: return dist_43;
				case 21: return dist_47;
				case -21: return dist_63;
				case 23: return dist_67;
				case -9: return dist_74;
				case 13: return dist_76;
				case -24: return dist_33;
				case 20: return dist_37;
				case -20: return dist_73;
				case 24: return dist_77;
				case -3: return dist_25;
				case -33: return dist_52;
				case 33: return dist_58;
				case 3: return dist_85;
				case -14: return dist_24;
				case 8: return dist_26;
				case -34: return dist_42;
				case 32: return dist_48;
				case -32: return dist_62;
				case 34: return dist_68;
				case -8: return dist_84;
				case 14: return dist_86;
				default: throw new Error(String.valueOf((target_loc.x-Info.x)+11*(target_loc.y-Info.y)));
			}
		}
		else {
			return Math.min(dist_86, Math.min(dist_84, Math.min(dist_68, Math.min(dist_62, Math.min(dist_48, Math.min(dist_42, Math.min(dist_26, Math.min(dist_24, Math.min(dist_85, Math.min(dist_58, Math.min(dist_52, Math.min(dist_25, Math.min(dist_77, Math.min(dist_73, Math.min(dist_37, Math.min(dist_33, Math.min(dist_76, Math.min(dist_74, Math.min(dist_67, Math.min(dist_63, Math.min(dist_47, Math.min(dist_43, Math.min(dist_36, dist_34)))))))))))))))))))))));
		}
    }
}
