package priv.cdk.bomberman.critter;

import priv.cdk.bomberman.critter.basic.BasicsCritter;
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
     * @param type  0：普通  1： elite
     */
    public CritterThread(Room room, int lx, int ty, int type){
        super(room);

        if (type == 1) {
            critter = new EliteCritter(room, lx, ty);
        } else {
            critter = new BasicsCritter(room, lx, ty);
        }
    }

    @Override
    public void myRun(){
        int moveSize = critter.moveSize;
        int moveTime = critter.moveTime;

        while (!critter.isDie()) {
            int i = random.nextInt(4);

            int number = randomMove(i, i);

            int tb, lr;
            switch (number) {
                case 0:
                    tb = - moveSize;
                    lr = 0;
                    break;
                case 1:
                    tb = moveSize;
                    lr = 0;
                    break;
                case 2:
                    tb = 0;
                    lr = - moveSize;
                    break;
                case 3:
                    tb = 0;
                    lr = moveSize;
                    break;
                default:
                    tb = 0;
                    lr = 0;
                    break;
            }

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
                        mySleep(moveTime);
                        if (!move) {
                            break;
                        } else {
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

    /**
     * 随机移动，如果随机的路线不可走，那么依次判断路口
     */
    public int randomMove(int startNumber, int number){

        int tb, lr;
        switch (number) {
            case 0:
                critter.setState(7);
                tb = - critter.moveSize;
                lr = 0;
                break;
            case 1:
                critter.setState(4);
                tb = critter.moveSize;
                lr = 0;
                break;
            case 2:
                critter.setState(5);
                tb = 0;
                lr = - critter.moveSize;
                break;
            case 3:
                critter.setState(6);
                tb = 0;
                lr = critter.moveSize;
                break;
            default:
                tb = 0;
                lr = 0;
                break;
        }

        if(!critter.isDie()){
            boolean move = critter.move(tb, lr);
            if(!move){
                if(startNumber == (number == 3 ? 0 : (number + 1))){
                    return 5;//全没找到
                }else{
                    return randomMove(startNumber, (number == 3 ? 0 : (number + 1)));
                }
            }else{
                return number;
            }
        }else {
            return 4;//已经死亡
        }
    }
}
