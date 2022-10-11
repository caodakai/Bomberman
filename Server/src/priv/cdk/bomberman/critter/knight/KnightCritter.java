package priv.cdk.bomberman.critter.knight;

import priv.cdk.bomberman.Bom;
import priv.cdk.bomberman.critter.Critter;
import priv.cdk.bomberman.critter.dragon.DragonCritter;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 骑士怪 ， 生命值两滴
 * 需要杀死龙兵才会掉血。
 */
public class KnightCritter extends Critter {
    private final AtomicBoolean stageDie = new AtomicBoolean(false);

    /** 生命值 初始为2，到了1时，会进入两秒无敌 */
    private final AtomicInteger hp = new AtomicInteger(2);

    public KnightCritter(Room room, int lx, int ty) {
        super(room, lx, ty);

        this.moveSize = Room.CELL_WIDTH/4;
        this.moveTime = 200;
    }

    @Override
    public boolean move(int xPx, int yPx){
        boolean move = super.move(xPx, yPx);

        if (move){//如果移动成功，那么监听是否碰到了炸弹或者墙，如果有，则清除墙
            Bom.wakeUpTheBomb(room, getMoveY(), getMoveX(), 0);
            room.destroyTheWall(getMoveY(), getMoveX());
        }

        return move;
    }


    @Override
    public boolean canMove(int y, int x) {
        int bodyNumber = room.getBodyCellValue(y, x);

        return canMove(bodyNumber);
    }

    public boolean canMove(int bodyNumber){
        if (hp.get() == 0){
            return false;
        }else if (hp.get() == 1){//生命值为1时，不可以穿越墙，不可以穿越炸弹
            return !IsUtil.isBom(bodyNumber) && !IsUtil.isWall(bodyNumber) && !IsUtil.isFire(bodyNumber);//不会寻死进入火焰
        }else if(hp.get() == 2){//生命值为2时，不可以穿特殊墙
            return !IsUtil.isSpecialWall(bodyNumber);
        }else{
            return false;
        }
    }

    @Override
    public boolean die(){
        if(isDie()) {//骑士已经处于死亡状态
            return false;
        }

        if (room.close.get()) {//如果房间关闭，那么直接死亡
            return super.die();
        }
        if (stageDie.get()) {//如果处于状态死亡
            return false;
        }

        AtomicBoolean haveDragon = new AtomicBoolean(false);
        room.critters.forEach(critter -> {
            if (critter instanceof DragonCritter){
                haveDragon.set(true);
            }
        });

        if (haveDragon.get()){//龙未被消灭
            return false;
        }

        if (hp.get() <= 1){
            return super.die();
        }else{
            if (stageDie.compareAndSet(false, true)) {
                new KnightDieThread(this).start();
                return true;
            }else {
                return false;
            }
        }
    }

    @Override
    public int dieScore() {
        return 1000 * room.getCustomsPass()/2;
    }

    public void setStageDie(boolean die){
        this.stageDie.set(die);
    }

    public boolean isStageDie(){
        return this.stageDie.get();
    }

    public int getHP(){
        return hp.get();
    }

    public void subtractHP(){
        hp.addAndGet(-1);
    }
}
