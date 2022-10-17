package priv.cdk.bomberman.critter.dragon;

import priv.cdk.bomberman.critter.elite.EliteCritter;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;

import java.util.Random;

/**
 * 龙类,跟随骑士出现，如果龙存在，那么则不能进行杀死骑士
 */
public class DragonCritter extends EliteCritter {
    private final DragonType dragonType;

    public DragonCritter(Room room, int lx, int ty) {
        super(room, lx, ty);

        if (new Random().nextInt(2) == 0) {
            dragonType = DragonType.BLUE;

            this.moveSize = Room.CELL_WIDTH/4;
            this.moveTime = Math.max( 100 - room.getCustomsPass() * 2, 10);
        }else{
            dragonType = DragonType.GOLDEN;
            this.moveSize = Room.CELL_WIDTH/4;
            this.moveTime = Math.max( 200 - room.getCustomsPass() * 4, 20);
        }
    }

    @Override
    public boolean move(int xPx, int yPx){
        boolean move = super.move(xPx, yPx);

        if (move && dragonType == DragonType.GOLDEN){//如果移动成功，那么监听是否碰到了墙，如果有，则清除墙
            room.destroyTheWall(getMoveY(), getMoveX());
        }

        return move;
    }




    @Override
    public int dieScore() {
        return 25 * room.getCustomsPass();
    }

    @Override
    protected boolean canMove1(int bodyNumber){
        if(dragonType == DragonType.BLUE) {
            return !IsUtil.isWall(bodyNumber) && !IsUtil.isBom(bodyNumber) && !IsUtil.isFire(bodyNumber);
        }else{
            return !IsUtil.isSpecialWall(bodyNumber) && !IsUtil.isBom(bodyNumber) && !IsUtil.isFire(bodyNumber);
        }
    }

    public boolean isBLUE() {
        return dragonType == DragonType.BLUE;
    }

    public boolean isGOLDEN(){return dragonType == DragonType.GOLDEN;}

    /**
     * 龙的类型枚举
     * 蓝色的龙移动速度快，金色的龙移速慢，但是会破坏墙
     */
    enum DragonType{
        BLUE,GOLDEN
    }
}
