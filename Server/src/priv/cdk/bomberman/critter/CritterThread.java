package priv.cdk.bomberman.critter;

import priv.cdk.bomberman.critter.basic.BasicsCritter;
import priv.cdk.bomberman.critter.dragon.DragonCritter;
import priv.cdk.bomberman.critter.elite.EliteCritter;
import priv.cdk.bomberman.parent.MyThread;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;

import java.util.Random;

public class CritterThread extends MyThread {
    public Critter critter;
    private final Random random = new Random();

    /**
     *
     * @param type  0：普通  1： elite  3: dragon
     */
    public CritterThread(Room room, int lx, int ty, int type){
        super(room);

        if (type == 0) {
            critter = new BasicsCritter(room, lx, ty);
        } else if (type == 1){
            critter = new EliteCritter(room, lx, ty);
        }else {
            critter = new DragonCritter(room, lx, ty);
        }
    }

    @Override
    public void myRun(){
        int moveSize = critter.moveSize;
        int moveTime = critter.moveTime;

        while (!critter.isDie()) {
            int i = random.nextInt(4);

            int number = CritterThreadUtil.randomMove(critter, i, i);

            int[] ints = CritterThreadUtil.movePx(number, moveSize);
            int tb = ints[0], lr = ints[1];

            if(number > 3){
                try {
                    mySleep(moveTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else {
                while (!critter.isDie()) {
                    boolean move = critter.move(tb, lr);
                    try {
                        if (!move){
                            if(critter instanceof BasicsCritter) {//如果为普通怪，那么降低转向速度
                                for (int t = 0; !critter.isDie() && t < 3; t++) {
                                    critter.move(tb, lr);
                                    mySleep(moveTime);//如果不可移动，那么再睡眠几次移动速度，然后再重新寻路，防止转弯太快了
                                }
                            }
                            break;
                        }else{
                            mySleep(moveTime);
                            if (critter.getTy() == critter.getBy() && critter.getLx() == critter.getRx()) {//路过路口时，有20%概率会重新寻路
                                if(critter instanceof EliteCritter) {
                                    int futureBodyNumber = critter.room.futureBody[critter.getTy()][critter.getLx()];
                                    if (!IsUtil.isFutureBodyFire(futureBodyNumber)) {//不在火中，那么随机转向
                                        if (random.nextInt(5) == 0) {
                                            break;
                                        }
                                    }else{
                                        if(tb == 0 && lr == 0){//如果在火中，是突然新增的火，那么跳出，然后随机移动
                                            if (random.nextInt(5) == 0) {
                                                break;
                                            }
                                        }
                                    }
                                }else{
                                    if (random.nextInt(5) == 0) {
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
