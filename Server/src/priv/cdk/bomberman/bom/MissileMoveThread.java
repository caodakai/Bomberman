package priv.cdk.bomberman.bom;

import priv.cdk.bomberman.parent.MyThread;
import priv.cdk.bomberman.room.Room;

public class MissileMoveThread extends MyThread {

    public MissileMoveThread(Room room) {
        super(room);
    }

    @Override
    public void myRun() {
        while (!myRoom.close.get()){
            myRoom.missiles.forEach(missile -> {
                missile.update(null);
            });

            try {
                mySleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
