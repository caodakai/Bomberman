package priv.cdk.bomberman.player;

import priv.cdk.bomberman.common.Common;
import priv.cdk.bomberman.room.Room;

public class MoveThread extends Thread {
    private int xPx = 0;
    private int yPx = 0;

    Player player;
    private long id;

    @Override
    public void run() {
        while (!player.isDie(id)) {
            try {
                synchronized (this) {
                    if (!player.isDie(id)) {
                        this.wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!player.playerMoveOnce(xPx, yPx, true)) {

                //为了不让返回重新走，修改移动规则
                int i = (player.getActualX() + xPx / 3 - Common.interfaceStartX) % Room.CELL_WIDTH;

                if ((i > 0 && i < player.speed / 3 + 1) || (i < 0 && i > -player.speed / 3 - 1)) {
                    xPx = (xPx / 3 - i) * 3;
                }

                i = Room.CELL_WIDTH - i;
                if ((i > 0 && i < player.speed / 3 + 1) || (i < 0 && i > -player.speed / 3 - 1)) {
                    xPx = (xPx / 3 + i) * 3;
                }

                int j = (player.getActualY() + yPx / 3 - Common.interfaceStartY) % Room.CELL_HEIGHT;

                if ((j > 0 && j < player.speed / 3 + 1) || (j < 0 && j > -player.speed / 3 - 1)) {
                    yPx = (yPx / 3 - j) * 3;
                }

                j = Room.CELL_HEIGHT - j;

                if ((j > 0 && j < player.speed / 3 + 1) || (j < 0 && j > -player.speed / 3 - 1)) {
                    yPx = (yPx / 3 + j) * 3;
                }

                player.playerMoveOnce(xPx, yPx, true);
            }

            //校准转弯的路口
               /* int i = (player.getActualX() - Common.interfaceStartX) % Room.CELL_WIDTH;

                if ((i > 0 && i < player.speed / 3 + 1) || (i < 0 && i > -player.speed / 3 - 1)) {
                    player.playerMove(-i, 0);
                }

                i = Room.CELL_WIDTH - i;
                if ((i > 0 && i < player.speed / 3 + 1) || (i < 0 && i > -player.speed / 3 - 1)) {
                    player.playerMove(i, 0);
                }

                int j = (player.getActualY() - Common.interfaceStartY) % Room.CELL_HEIGHT;

                if ((j > 0 && j < player.speed / 3 + 1) || (j < 0 && j > -player.speed / 3 - 1)) {
                    player.playerMove(0, -j);
                }

                j = Room.CELL_HEIGHT - j;

                if ((j > 0 && j < player.speed / 3 + 1) || (j < 0 && j > -player.speed / 3 - 1)) {
                    player.playerMove(0, j);
                }*/

            player.canMoveThread.set(true);
        }
    }


    public void init(int xPx, int yPx) {
        this.xPx = xPx;
        this.yPx = yPx;
    }

    public MoveThread(Player player) {
        this.player = player;
        this.id = player.getId();
    }

    public void updateId(){
        this.id = player.getId();
    }
}
