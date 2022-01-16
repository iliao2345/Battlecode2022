package retreat_12;
import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws Exception {
    	
        RobotPlayer.rc = rc;
        Info.initialize(rc);
        Comms.initialize();

        while (true) {
            try {
            	Info.update();
            	Comms.update();
//            	if (rc.getRoundNum()>80) {rc.resign();}
        		switch (rc.getType()) {
        		case ARCHON: Archon.act(); break;
        		case BUILDER: Builder.act(); break;
        		case LABORATORY: Laboratory.act(); break;
        		case MINER: Miner.act(); break;
        		case SAGE: Sage.act(); break;
        		case SOLDIER: Soldier.post_comms_update(); Soldier.act(); break;
        		case WATCHTOWER: Watchtower.act(); break;
        		}
            	
            	Comms.compute();
            	Comms.send();
            	
            	if (rc.getRoundNum()>Info.round_num && rc.getRoundNum() > 10 + Info.spawn_round) {throw new Exception("OUT OF BYTECODE!");}

                Clock.yield();
            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                System.out.println(Info.x);
                System.out.println(Info.y);
                e.printStackTrace();
                if (rc.getTeam() == Team.A) {
                	rc.setIndicatorLine(rc.getLocation(), new MapLocation(0,0), 255, 128, 128);
                }
                else {
                	rc.setIndicatorLine(rc.getLocation(), new MapLocation(Info.MAP_WIDTH-1,Info.MAP_HEIGHT-1), 128, 128, 255);
                }
//                Clock.yield();
//                Clock.yield();
//                rc.resign();
//                throw e;
            }
        }
    }
}
