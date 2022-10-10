package priv.cdk.bomberman.critter.knight;

import priv.cdk.bomberman.critter.Critter;
import priv.cdk.bomberman.parent.Biota;
import priv.cdk.bomberman.parent.BiotaDieThread;
import priv.cdk.bomberman.parent.MyThread;
import priv.cdk.bomberman.room.Room;

public class KnightDieThread extends MyThread {
    private final KnightCritter knightCritter;

    public KnightDieThread(KnightCritter knightCritter) {
        super(knightCritter.room);
        this.knightCritter = knightCritter;
    }

    @Override
    public void myRun(){
        knightCritter.setState(3);
        try {
            mySleep(500);
            mySleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        knightCritter.setState(2);
        try {
            mySleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        knightCritter.setState(1);
        try {
            mySleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        knightCritter.moveSize = Room.CELL_WIDTH/4;//修改速度
        knightCritter.moveTime = 150;

        knightCritter.subtractHP();
        knightCritter.setStageDie(false);//死亡状态改成False

        new Knight2Thread(myRoom, knightCritter).start();//启动第二阶段

        knightCritter.setState(4);//状态改为4
    }
}
