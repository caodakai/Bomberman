package priv.cdk.bomberman.critter;

import priv.cdk.bomberman.parent.Biota;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.RoomUtil;

public abstract class Critter extends Biota implements CritterDie{
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
                    RoomUtil.toDetermineDeath(player, getActualX(), getActualY());
                });
            }
            return move;
        }else {
            return false;
        }
    }

    @Override
    public boolean die(){
        boolean die = super.die();
        if(die) {
            room.addScore( dieScore() );
        }
        return die;
    }
}
