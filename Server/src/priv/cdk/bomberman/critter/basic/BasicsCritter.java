package priv.cdk.bomberman.critter.basic;

import priv.cdk.bomberman.critter.Critter;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;

/**
 * 小怪
 */
public class BasicsCritter extends Critter {

    public BasicsCritter(Room room, int lx, int ty) {
        super(room, lx, ty);
        this.moveSize = Room.CELL_WIDTH/6;
        this.moveTime = 200;
    }

    @Override
    public boolean canMove(int y, int x) {
        int bodyNumber = room.getBodyCellValue(y, x);
        return (!IsUtil.isWall(bodyNumber)) && (!IsUtil.isBom(bodyNumber));
    }


    @Override
    public int dieScore() {
        return room.getCustomsPass();
    }
}
