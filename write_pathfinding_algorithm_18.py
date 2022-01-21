from math import *

def get_statics(max_r2):
    contents = "\tprivate static boolean on_map_calculated = false;\n\tprivate static boolean cheap_dists_calculated = false;\n\tprivate static boolean expensive_dists_calculated = false;\n"
    for r2 in range(0, max_r2+1):
        for i in range(11):
            dx = i-5
            for j in range(11):
                dy = j-5
                if dx**2+dy**2==r2:
                    contents = contents + "\tprivate static double dist_" + str(i)+str(j) + " = 0;\n"
    for r2 in range(0, max_r2+1):
        for i in range(11):
            dx = i-5
            for j in range(11):
                dy = j-5
                if dx**2+dy**2==r2:
                    contents = contents + "\tprivate static double encoded_dist_" + str(i)+str(j) + " = 0;\n"
    for i in range(11):
        dx = i-5
        contents = contents + "\tprivate static boolean on_map_x_" + str(i) + " = true;\n"
    for j in range(11):
        dy = j-5
        contents = contents + "\tprivate static boolean on_map_y_" + str(j) + " = true;\n"
    return contents


def get_compute_on_map_values():
    contents = "\t\tint width = Info.MAP_WIDTH;\n\t\tint height = Info.MAP_HEIGHT;\n\t\tint x = Info.x;\n\t\tint y = Info.y;\n"
    for i in range(11):
        dx = i-5
        if dx<0:
            contents = contents + "\t\ton_map_x_" + str(i) + " = x >= " + str(-dx) + ";\n"
        if dx==0: pass
        if dx>0:
            contents = contents + "\t\ton_map_x_" + str(i) + " = x < width - " + str(dx) + ";\n"
    for j in range(11):
        dy = j-5
        if dy<0:
            contents = contents + "\t\ton_map_y_" + str(j) + " = y >= " + str(-dy) + ";\n"
        if dy==0: pass
        if dy>0:
            contents = contents + "\t\ton_map_y_" + str(j) + " = y < height - " + str(dy) + ";\n"
    return contents


def get_compute(min_r2=0, max_r2=20):
    contents = "\t\tMapLocation this_loc = Info.loc;\n"
    count2 = 0
    final_min = ""
    for r2 in range(min_r2, max_r2+1):
        if r2==0:
            continue
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
                        contents = contents + "\t\tdist_" + str(i)+str(j) + " = (on_map_x_" + str(i) + " && on_map_y_" + str(j) + ")? " + subcontent + " + (rc.canMove(Math2.direction_to(" + str(dx) + ", " + str(dy) + "))? 1" + str([5/16, 1/16, 6/16, 2/16, 7/16, 3/16, 8/16, 4/16][(round(atan2(dy,dx)/pi*4)+8)%8]) + " : wait_cost) + rc.senseRubble(this_loc.translate(" + str(dx) + ", " + str(dy) + ")) : Double.POSITIVE_INFINITY;\n"
                    else:
                        contents = contents + "\t\tdist_" + str(i)+str(j) + " = (on_map_x_" + str(i) + " && on_map_y_" + str(j) + ")? " + subcontent + " + 10. + rc.senseRubble(this_loc.translate(" + str(dx) + ", " + str(dy) + ")) : Double.POSITIVE_INFINITY;\n"

    for r2 in range(min_r2, max_r2+1):
        for i in range(11):
            dx = i-5
            for j in range(11):
                dy = j-5
                if dx**2+dy**2==r2:
                    contents = contents + "\t\tencoded_dist_"+str(i)+str(j) + " = " + "dist_"+str(i)+str(j) + " + " + str(((i+11*j)/2**(4+7))) + ";\n"
    return contents


def get_query(min_r2, max_r2):
    count2 = 0
    switch_contents = "\t\t\tswitch ((target_loc.x-Info.x)+11*(target_loc.y-Info.y)) {\n\t\t\t\tcase 0: return encoded_dist_55;\n"
    for r2 in range(1, max_r2+1):
        for i in range(11):
            dx = i-5
            for j in range(11):
                dy = j-5
                if dx**2+dy**2==r2:
                    if dx*dx+dy*dy<=2:
                        subcontent = "encoded_dist_55 + " + str([5/16, 1/16, 6/16, 2/16, 7/16, 3/16, 8/16, 4/16][(round(atan2(dy,dx)/pi*4)+8)%8])
                    else:
                        count = 0
                        for i_prev in range(11):
                            dx_prev = i_prev-5
                            for j_prev in range(11):
                                dy_prev = j_prev-5
                                if (dx-dx_prev)**2+(dy-dy_prev)**2<=2 and dx**2+dy**2>dx_prev**2+dy_prev**2:
                                    count += 1
                                    subterm_name = "encoded_dist_"+str(i_prev)+str(j_prev)
                                    if count==1:
                                        subcontent = subterm_name
                                    else:
                                        subcontent = "Math.min("+subterm_name+", " + subcontent + ")"

                    term = "encoded_dist_" + str(i)+str(j)
#                    switch_contents = switch_contents + "\t\t\t\tcase " + str(dx+11*dy) + ": return " + term + ";\n"
                    if dx*dx+dy*dy<=2:
                        switch_contents = switch_contents + "\t\t\t\tcase " + str(dx+11*dy) + ": return rc.canMove(Math2.direction_to(" + str(dx) + ", " + str(dy) + "))? " + subcontent + " : wait_cost;\n"
                    else:
                        switch_contents = switch_contents + "\t\t\t\tcase " + str(dx+11*dy) + ": return " + subcontent + ";\n"

                    if r2 >= min_r2:
                        min_term = term + " + (int)(delay_factor*Math.sqrt(this_loc.translate(" + str(dx) + ", " + str(dy) + ").distanceSquaredTo(target_loc)))"
                        count2 += 1
                        if count2==1:
                            final_min = "\t\t\t                " + min_term
                        else:
                            final_min = "\t\t\t       Math.min("+min_term+",\n" + final_min + ")"
    contents = "\t\tif (this_loc.isWithinDistanceSquared(target_loc, " + str(max_r2) + ")) {\n" + switch_contents + "\t\t\t\tdefault: throw new Error(String.valueOf((target_loc.x-Info.x)+11*(target_loc.y-Info.y)));\n\t\t\t}\n\t\t}\n\t\telse {\n\t\t\tdouble delay_factor = 10+average_rubble;\n\t\t\treturn " + final_min[10:] + ";\n\t\t}\n"
    return contents

bot_name = "pathfinding_13"

contents = "package " + bot_name + """;
import battlecode.common.*;

public class Bfs {
    public static RobotController rc;

    private static final Direction[] DIRECTIONS = new Direction[] {Direction.CENTER, Direction.NORTHEAST, Direction.NORTHWEST, Direction.SOUTHWEST, Direction.SOUTHEAST, Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH};
""" + get_statics(20) + """

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
""" + get_compute_on_map_values() + """    }

    private static void compute_cheap_dists(double wait_cost) throws GameActionException {
        if (cheap_dists_calculated) {
            return;
        }
        cheap_dists_calculated = true;
        compute_on_map();
""" + get_compute(0, 10) + """  }
    private static void compute_expensive_dists(double wait_cost) throws GameActionException {
        if (expensive_dists_calculated) {
            return;
        }
        expensive_dists_calculated = true;
        compute_cheap_dists(wait_cost);
""" + get_compute(10, 20) + """ }

    public static double query_cheap(MapLocation target_loc, double average_rubble, double wait_cost) throws GameActionException {
        compute_cheap_dists(wait_cost);
        MapLocation this_loc = Info.loc;
""" + get_query(5, 10) + """    }

    public static double query_expensive(MapLocation target_loc, double average_rubble, double wait_cost) throws GameActionException {
        MapLocation this_loc = Info.loc;
        if (this_loc.isWithinDistanceSquared(target_loc, 10)) {
            return query_cheap(target_loc, average_rubble, wait_cost);
        }
        compute_expensive_dists(wait_cost);
""" + get_query(10, 20) + """   }

    public static double query(MapLocation target_loc, double average_rubble, double wait_cost, boolean expensive) throws GameActionException {
        wait_cost = (int)(wait_cost*10) + """ + str((5+11*5)/2**(4*7)) + """;
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
        int offset_number = (int)(dist * """ + str(2**(4+7)) + """ % 128);
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
"""

with open('./src/' + bot_name + '/Bfs.java', 'w') as f:
    f.write(contents)

