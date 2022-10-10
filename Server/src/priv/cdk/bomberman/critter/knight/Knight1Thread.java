package priv.cdk.bomberman.critter.knight;

import priv.cdk.bomberman.critter.CritterThreadUtil;
import priv.cdk.bomberman.critter.elite.EliteCritter;
import priv.cdk.bomberman.parent.MyThread;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;

import java.util.Random;

/**
 * 第一阶段的骑士，移动方式和普通小怪类似
 */
public class Knight1Thread extends MyThread {
    private final KnightCritter knightCritter;
    private final Random random = new Random();

    public Knight1Thread(Room room, KnightCritter knightCritter) {
        super(room);
        this.knightCritter = knightCritter;
    }

    @Override
    public void myRun(){
        while (!knightCritter.isDie() && !knightCritter.isStageDie()){

            int i = random.nextInt(4);

            int number = CritterThreadUtil.randomMove(knightCritter, i, i);

            int[] ints = CritterThreadUtil.movePx(number, knightCritter.moveSize);
            int tb = ints[0], lr = ints[1];

            if (number > 3) {
                try {
                    mySleep(knightCritter.moveTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                while (!knightCritter.isDie() && !knightCritter.isStageDie()) {
                    boolean move = knightCritter.move(tb, lr);
                    try {
                        mySleep(knightCritter.moveTime);
                        if (!move) {
                            break;
                        } else {
                            if (knightCritter.getTy() == knightCritter.getBy() && knightCritter.getLx() == knightCritter.getRx()) {//路过路口时，有33%概率会重新寻路
                                if (random.nextInt(3) == 0) {
                                    break;
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
