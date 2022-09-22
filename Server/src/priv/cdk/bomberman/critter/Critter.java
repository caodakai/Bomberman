package priv.cdk.bomberman.critter;

import priv.cdk.bomberman.parent.Biota;
import priv.cdk.bomberman.room.Room;

public abstract class Critter extends Biota {
    public int moveSize;
    public int moveTime;

    public Critter(Room room, int lx, int ty) {
        super(room, lx, ty);
    }

    @Override
    public boolean move(int xPx, int yPx){
        if( (!room.suspend.get()) && (!room.critterSuspend.get())) {
            boolean move = super.move(xPx, yPx);

            if(move){
                //监听玩家死亡
                room.ps.forEach(player -> {
                    if(!player.isDie()) {
                        int x = player.getActualX() - getActualX();
                        int y = player.getActualY() - getActualY();

                        boolean b = (y >= 0 && y < Room.CELL_HEIGHT) || (y <= 0 && y > -Room.CELL_HEIGHT);
                        if ((x >= 0 && x < Room.CELL_WIDTH && b) || (x <= 0 && x > -Room.CELL_WIDTH && b)) {
                            player.die();
                        }
                    }
                });
            }
            return move;
        }else {
            return false;
        }
    }
}
