package priv.cdk.bomberman.critter;

import priv.cdk.bomberman.common.Common;
import priv.cdk.bomberman.common.MotorDirection;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;

public class BossCritter extends Critter {

    public BossCritter(Room room, int lx, int ty) {
        super(room, lx, ty);
        this.moveSize = Room.CELL_WIDTH/4;
        this.moveTime = 100;
    }

    @Override
    public boolean move(int xPx, int yPx){
        return super.move(xPx, yPx);
    }

    @Override
    public boolean canMove(int y, int x) {
        int bodyNumber = room.getBodyCellValue(y, x);
        if(canMove1(bodyNumber)){
            return !futureBom(y, x);
        }
        return false;
    }

    private boolean canMove1(int bodyNumber){
//        return !Room.isSpecialWall(bodyNumber) && !Bom.isBom(bodyNumber) && !Bom.isFire(bodyNumber);
        return !IsUtil.isWall(bodyNumber) && !IsUtil.isBom(bodyNumber) && !IsUtil.isFire(bodyNumber);
    }

    private boolean canMove2(int bodyNumber){
        return canMove1(bodyNumber) && !IsUtil.isFutureBodyFire(bodyNumber);
    }

    public boolean futureBom(int y, int x){
        int i = room.futureBody[y][x];

        if(IsUtil.isFutureBodyFire(i)){
            int thisNumber = room.futureBody[this.getTy()][this.getLx()];
            if(!IsUtil.isFutureBodyFire(thisNumber)){//如果原先不在火中，那么就不进火了
                return true;
            }else if(thisNumber == Common.FUTURE_BODY_FIRE_NUMBER_CENTRE){//如果当前处于中间火，那么往哪边都行
                return false;
            }
            switch (i){
                case Common.FUTURE_BODY_FIRE_NUMBER_TOP:
                    if (motorDirection == MotorDirection.BOTTOM) {
                        if(this.getLx() == this.getRx() && this.getTy() == this.getBy()) {//优先转弯
                            if (priorityMoveLeftOrRight(this.getTy(), this.getLx())) {
                                return true;
                            }
                        }

                        int offset = 1;
                        while (this.getTy() + offset < room.getH()){
                            int topFire = room.futureBody[this.getTy() + offset][this.getLx()];//中间一直是向上的火或者中间火
                            if(topFire != Common.FUTURE_BODY_FIRE_NUMBER_TOP && topFire != Common.FUTURE_BODY_FIRE_NUMBER_CENTRE){
                                break;
                            }else{
                                if(priorityMoveLeftOrRight(this.getTy() + offset, this.getLx())){//如果左右两边有不是火的地方，那么可以移动,标记移动方向非火
                                    return false;
                                }
                            }
                            offset++;
                        }
                        return true;
                    }else if(motorDirection == MotorDirection.TOP){
                        if (this.getLx() == this.getRx() && this.getTy() == this.getBy()) {//优先转弯
                            if (priorityMoveLeftOrRight(this.getTy(), this.getLx())) {
                                return true;
                            }
                        }
                    }
                    break;
                case Common.FUTURE_BODY_FIRE_NUMBER_BOTTOM:
                    if (motorDirection == MotorDirection.TOP) {
                        if(this.getLx() == this.getRx() && this.getTy() == this.getBy()) {//优先转弯
                            if (priorityMoveLeftOrRight(this.getTy(), this.getLx())) {
                                return true;
                            }
                        }

                        int offset = 1;
                        while (this.getTy() - offset >= 0){
                            int bottomFire = room.futureBody[this.getBy() - offset][this.getLx()];//中间一直是向下的火或者中间火
                            if(bottomFire != Common.FUTURE_BODY_FIRE_NUMBER_BOTTOM && bottomFire != Common.FUTURE_BODY_FIRE_NUMBER_CENTRE){
                                break;
                            }else{
                                if(priorityMoveLeftOrRight(this.getBy() - offset, this.getLx())){//如果左右两边有不是火的地方，那么可以移动,标记移动方向非火
                                    return false;
                                }
                            }
                            offset++;
                        }
                        return true;
                    }else if(motorDirection == MotorDirection.BOTTOM){
                        if(this.getLx() == this.getRx() && this.getTy() == this.getBy()) {//优先转弯
                            if (priorityMoveLeftOrRight(this.getTy(), this.getLx())) {
                                return true;
                            }
                        }
                    }
                    break;
                case Common.FUTURE_BODY_FIRE_NUMBER_LEFT:
                    if (motorDirection == MotorDirection.RIGHT) {
                        if(this.getLx() == this.getRx() && this.getTy() == this.getBy()) {//优先转弯
                            if (priorityMoveTopOrBottom(getTy(), getLx())) {
                                return true;
                            }
                        }

                        int offset = 1;
                        while (this.getLx() + offset < room.getW()){
                            int leftFire = room.futureBody[this.getTy()][this.getLx() + offset];//中间一直是向左的火或者中间火
                            if(leftFire != Common.FUTURE_BODY_FIRE_NUMBER_LEFT && leftFire != Common.FUTURE_BODY_FIRE_NUMBER_CENTRE){
                                break;
                            }else{
                                if(priorityMoveTopOrBottom(this.getTy(), this.getLx() + offset)){//如果上下两边有不是火的地方，那么可以移动,标记移动方向非火
                                    return false;
                                }
                            }
                            offset++;
                        }
                        return true;
                    }else if(motorDirection == MotorDirection.LEFT){//优先转弯
                        if(this.getLx() == this.getRx() && this.getTy() == this.getBy()) {//优先转弯
                            if (priorityMoveTopOrBottom(getTy(), getLx())) {
                                return true;
                            }
                        }
                    }
                    break;
                case Common.FUTURE_BODY_FIRE_NUMBER_RIGHT:
                    if (motorDirection == MotorDirection.LEFT) {
                        if(this.getLx() == this.getRx() && this.getTy() == this.getBy()) {//优先转弯
                            if (priorityMoveTopOrBottom(getTy(), getLx())) {
                                return true;
                            }
                        }

                        int offset = 1;
                        while (this.getRx() - offset >= 0){
                            int rightFire = room.futureBody[this.getTy()][this.getRx() - offset];//中间一直是向右的火或者中间火
                            if(rightFire != Common.FUTURE_BODY_FIRE_NUMBER_RIGHT && rightFire != Common.FUTURE_BODY_FIRE_NUMBER_CENTRE){
                                break;
                            }else{
                                if(priorityMoveTopOrBottom(this.getTy(), this.getRx() - offset)){//如果上下两边有不是火的地方，那么可以移动,标记移动方向非火
                                    return false;
                                }
                            }
                            offset++;
                        }
                        return true;
                    }else if(motorDirection == MotorDirection.RIGHT){//优先转弯
                        if(this.getLx() == this.getRx() && this.getTy() == this.getBy()) {//优先转弯
                            if (priorityMoveTopOrBottom(getTy(), getLx())) {
                                return true;
                            }
                        }
                    }
                    break;
                case Common.FUTURE_BODY_FIRE_NUMBER_CENTRE:
                    return false;
            }
        }
        return false;
    }

    /**
     * 优先左右转弯，
     * @return true ：可以上下转弯
     */
//    private boolean priorityMoveLeftOrRight(int offset){
//        return priorityMoveLeftOrRight(this.getTy() + offset, this.getLx());
//    }
    private boolean priorityMoveLeftOrRight(int reallyY, int reallyX){
        int l = room.futureBody[reallyY][reallyX - 1];
        int r = room.futureBody[reallyY][reallyX + 1];
        return canMove2(l) || canMove2(r);
    }

    /**
     * 优先上下转弯，
     * @return true ：可以上下转弯
     */
//    private boolean priorityMoveTopOrBottom(int offset){
//        return priorityMoveTopOrBottom(this.getTy(), this.getLx() + offset);
//    }
    private boolean priorityMoveTopOrBottom(int reallyY, int reallyX){
        int t = room.futureBody[reallyY - 1][reallyX];
        int b = room.futureBody[reallyY + 1][reallyX];
        return canMove2(t) || canMove2(b);
    }

    @Override
    public boolean die(){
        boolean die = super.die();
        if(die){
            room.addScore(5 * room.getCustomsPass());
        }
        return die;
    }
}
