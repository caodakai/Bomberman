package priv.cdk.bomberman.charmander;

import priv.cdk.bomberman.common.Common;
import priv.cdk.bomberman.parent.Biota;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;
import priv.cdk.bomberman.utils.RoomUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 小火龙
 * 友方，会自动抓怪
 */
public class Charmander extends Biota {
     public int moveSize = Room.CELL_WIDTH/4;
     public int moveTime = 10;

    public Charmander(Room room, int lx, int ty) {
        super(room, lx, ty);
    }

    @Override
    public boolean move(int xPx, int yPx){
        boolean move = super.move(xPx, yPx);

        AtomicBoolean hasCritter = new AtomicBoolean(false);
        room.critters.forEach(critter -> {
            if (!critter.isDie()) {
                hasCritter.set(true);
                RoomUtil.toDetermineDeath(critter, getActualX(), getActualY());
            }
        });

        int moveX = getMoveX();
        int moveY = getMoveY();
        if (move && !hasCritter.get()){
            int bodyNumber = room.getBodyCellValue(moveY, moveX);

            if (bodyNumber == Common.PROP_DOOR){
                die();//如果怪都死了，并且到了门的地方，那么小火龙死亡
            }
        }

        room.destroyTheWall(moveY, moveX);//清除墙

        return move;
    }


    @Override
    public boolean canMove(int y, int x) {
        int bodyNumber = room.getBodyCellValue(y, x);
        return canMove(bodyNumber);
    }

    public boolean canMove(int bodyNumber){
//        return !IsUtil.isWall(bodyNumber) && !IsUtil.isBom(bodyNumber);
        return !IsUtil.isSpecialWall(bodyNumber);
    }

    @Override
    public int getDieTime(){
        return 0;
    }
}
