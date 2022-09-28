package priv.cdk.bomberman.critter.knight;

import priv.cdk.bomberman.critter.Critter;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;

/**
 * 骑士怪 ， 生命值两滴， 第一次被火焰烧后会有3秒无敌时间 。
 * 需要杀死两个龙兵才会掉血。
 */
public class KnightCritter extends Critter {

    public KnightCritter(Room room, int lx, int ty) {
        super(room, lx, ty);
    }

    @Override
    public boolean canMove(int y, int x) {
        int bodyNumber = room.getBodyCellValue(y, x);
        return !IsUtil.isSpecialWall(bodyNumber);
    }

    @Override
    public int dieScore() {
        return 1000;
    }
}
