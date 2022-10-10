package priv.cdk.bomberman.charmander;

import priv.cdk.bomberman.parent.Biota;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;
import priv.cdk.bomberman.utils.RoomUtil;

/**
 * 小火龙
 * 友方，会自动抓怪
 */
public class Charmander extends Biota {
     public int moveSize = Room.CELL_WIDTH/4;
     public int moveTime = 100;

    public Charmander(Room room, int lx, int ty) {
        super(room, lx, ty);
    }

    @Override
    public boolean move(int xPx, int yPx){
        boolean move = super.move(xPx, yPx);

        room.critters.forEach(critter -> {
            RoomUtil.toDetermineDeath(critter, getActualX(), getActualY());
        });

        return move;
    }


    @Override
    public boolean canMove(int y, int x) {
        int bodyNumber = room.getBodyCellValue(y, x);
        return canMove(bodyNumber);
    }

    public boolean canMove(int bodyNumber){
        return (!IsUtil.isWall(bodyNumber)) && (!IsUtil.isBom(bodyNumber));
    }

}
