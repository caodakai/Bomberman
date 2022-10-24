package priv.cdk.bomberman.bom;

import priv.cdk.bomberman.common.MotorDirection;
import priv.cdk.bomberman.parent.MyThread;
import priv.cdk.bomberman.player.Player;
import priv.cdk.bomberman.room.Room;

public class MissileMoveThread extends MyThread {
    private final Missile missile;
    private final Player player;
    private final int moveTime;
    private final int moveXSize;
    private final int moveYSize;

    public MissileMoveThread(Room room, Missile missile,Player player, MotorDirection motorDirection) {
        super(room);
        this.missile = missile;
        this.player = player;

        int moveSize = Room.CELL_WIDTH / 10;
        this.moveTime = 20;

        switch (motorDirection) {
            case TOP:
                moveYSize = -moveSize;
                moveXSize = 0;
                break;
            case BOTTOM:
                moveYSize = moveSize;
                moveXSize = 0;
                break;
            case LEFT:
                moveYSize = 0;
                moveXSize = -moveSize;
                break;
            case RIGHT:
                moveYSize = 0;
                moveXSize = moveSize;
                break;
            default:
                moveYSize = 0;
                moveXSize = 0;
        }
    }

    @Override
    public void myRun() {
        while (!missile.isDie()){
            boolean move = missile.move(moveXSize, moveYSize);

            if (!move){
                break;
            }

            try {
                mySleep(moveTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        player.bomNumberAdd();
    }
}
