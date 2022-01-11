package farm_2;
import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws Exception {
    	
        RobotPlayer.rc = rc;
        Info.initialize(rc);

        while (true) {
            try {
            	Info.update();
//            	if (rc.getRoundNum()>2000) {rc.resign();}
        		switch (rc.getType()) {
        		case ARCHON: Archon.act(); break;
        		case BUILDER: Builder.act(); break;
        		case LABORATORY: Laboratory.act(); break;
        		case MINER: Miner.act(); break;
        		case SAGE: Sage.act(); break;
        		case SOLDIER: Soldier.act(); break;
        		case WATCHTOWER: Watchtower.act(); break;
        		}
            	
            	Comms.compute();
            	Comms.send();
            	
//            	if (rc.getRoundNum()>Info.round_num) {throw new Exception("OUT OF BYTECODE!");}

                Clock.yield();
            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                System.out.println(Info.x);
                System.out.println(Info.y);
                e.printStackTrace();
                rc.setIndicatorDot(rc.getLocation(), 255, 128, 128);
                Clock.yield();
                throw e;
//                rc.resign();
            }
        }
    }
}
