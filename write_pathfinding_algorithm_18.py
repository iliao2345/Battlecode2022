from math import *

def get_main_block(max_r2, interior_r2):
    contents = ""
    for r2 in range(0, max_r2+1):
        for i in range(11):
            dx = i-5
            for j in range(11):
                dy = j-5
                if dx**2+dy**2==r2:
                    contents = contents + "\t\tMapLocation loc_" + str(i)+str(j) + " = this_loc.translate(" + str(dx) + ", " + str(dy) + ");\n"
    count2 = 0
    final_min = ""
    switch_contents = "\t\t\tswitch ((target_loc.x-Info.x)+11*(target_loc.y-Info.y)) {\n\t\t\t\tcase 0: return Double.MAX_VALUE;\n"
    for r2 in range(1, max_r2+1):
        for i in range(11):
            dx = i-5
            for j in range(11):
                dy = j-5
                if dx**2+dy**2==r2:
                    count = 0
                    for i_prev in range(11):
                        dx_prev = i_prev-5
                        for j_prev in range(11):
                            dy_prev = j_prev-5
                            if (dx-dx_prev)**2+(dy-dy_prev)**2<=2 and dx**2+dy**2>dx_prev**2+dy_prev**2:
                                count += 1
                                var_name = "dist_"+str(i_prev)+str(j_prev)
                                if count==1:
                                    subcontent = var_name
                                else:
                                    subcontent = "Math.min("+var_name+", " + subcontent + ")"
                    if dx**2+dy**2<=2:
#                        contents = contents + "\t\tdouble dist_" + str(i)+str(j) + " = (rc.onTheMap(loc_" + str(i)+str(j) + ") && rc.canMove(Math2.direction_to(" + str(dx) + ", " + str(dy) + ")))? " + subcontent + " + 10" + str([0,0.125,0.25,0.375,0.5,0.625,0.75,0.875][(round(atan2(dy,dx)/pi*4)+8)%8]) + " + rc.senseRubble(loc_" + str(i)+str(j) + ") : Double.MAX_VALUE;\n"
                        contents = contents + "\t\tdouble dist_" + str(i)+str(j) + " = rc.onTheMap(loc_" + str(i)+str(j) + ")? " + subcontent + " + (rc.canMove(Math2.direction_to(" + str(dx) + ", " + str(dy) + "))? 1" + str([0.0625,0.125,0.1875,0.25,0.3125,0.375,0.4375,0.5,0.5625][(round(atan2(dy,dx)/pi*4)+8)%8]) + " : 20) + rc.senseRubble(loc_" + str(i)+str(j) + ") : Double.MAX_VALUE;\n"
                    elif dx**2+dy**2 >= interior_r2:
                        count2 += 1
                        contents = contents + "\t\tdouble dist_" + str(i)+str(j) + " = rc.onTheMap(loc_" + str(i)+str(j) + ")? " + subcontent + " + 10 + rc.senseRubble(loc_" + str(i)+str(j) + ") + (int)(delay_factor*Math.sqrt(loc_" + str(i)+str(j) + ".distanceSquaredTo(target_loc))) : Double.MAX_VALUE;\n"
                        var_name = "dist_" + str(i)+str(j)
                        if count2==1:
                            final_min = var_name
                        else:
                            final_min = "Math.min("+var_name+", " + final_min + ")"
                    else:
                        contents = contents + "\t\tdouble dist_" + str(i)+str(j) + " = rc.onTheMap(loc_" + str(i)+str(j) + ")? " + subcontent + " + 10 + rc.senseRubble(loc_" + str(i)+str(j) + ") : Double.MAX_VALUE;\n"
                    switch_contents = switch_contents + "\t\t\t\tcase " + str(dx+11*dy) + ": return dist_" + str(i)+str(j) + ";\n"
    contents = contents + "\t\tif (this_loc.isWithinDistanceSquared(target_loc, " + str(max_r2) + ")) {\n" + switch_contents + "\t\t\t\tdefault: throw new Error(String.valueOf((target_loc.x-Info.x)+11*(target_loc.y-Info.y)));\n\t\t\t}\n\t\t}\n\t\telse {\n\t\t\treturn " + final_min + ";\n\t\t}\n"
    return contents

max_r2 = 20
interior_r2 = 13

#max_r2 = 10
#interior_r2 = 5

contents = """package comm_micro_21;
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
""" + get_main_block(20, 13) + """    }
    public static double cost_to_cheap(MapLocation target_loc, double average_rubble) throws GameActionException {
        MapLocation this_loc = Info.loc;
        double delay_factor = 10+average_rubble;
        double dist_55 = 0;
""" + get_main_block(10, 5) + """    }
}
"""


with open('./src/comm_micro_21/Bfs.java', 'w') as f:
    f.write(contents)

