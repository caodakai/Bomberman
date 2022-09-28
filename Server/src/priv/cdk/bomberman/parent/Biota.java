package priv.cdk.bomberman.parent;

import priv.cdk.bomberman.common.Common;
import priv.cdk.bomberman.common.MotorDirection;
import priv.cdk.bomberman.room.Room;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Biota implements Movement {

    private final AtomicBoolean die = new AtomicBoolean(false);

    public MotorDirection motorDirection = MotorDirection.NOT_MOVE;//1:上、2:下、3:左、4:右 0:未运动过
    private int state = 4;

    private int lx;//左边位置
    private int rx;//右边位置
    private int ty;//上面位置
    private int by;//下面位置
    private int actualX;//实际X坐标像素位置
    private int actualY;//实际Y坐标像素位置

    private int moveX;//上一次移动的x坐标 仅保存成功移动的坐标，没有成功移动的不保存 （非像素）
    private int moveY;//上一次移动的y坐标

    public Room room;
//    public StringBuilder moveRecord = new StringBuilder();

    public Biota(Room room,int lx, int ty){
        this.lx = lx;
        this.rx = lx;
        this.ty = ty;
        this.by = ty;
        this.actualX = Room.CELL_WIDTH * lx + Common.interfaceStartX;
        this.actualY = Room.CELL_HEIGHT * ty + Common.interfaceStartY;
        this.room = room;
    }

    /**
     * 设置位置
     */
    public void setPosition(int lx, int ty){
        this.lx = lx;
        this.rx = lx;
        this.ty = ty;
        this.by = ty;
        this.actualX = Room.CELL_WIDTH * lx + Common.interfaceStartX;
        this.actualY = Room.CELL_HEIGHT * ty + Common.interfaceStartY;
    }

    public boolean move(int xPx, int yPx){
//        moveRecord.append(xPx).append(":").append(yPx).append("\n");

        if(xPx > 0){//设置运动方向
            motorDirection = MotorDirection.RIGHT;
        }else if(xPx < 0){
            motorDirection = MotorDirection.LEFT;
        }else{
            if(yPx > 0){
                motorDirection = MotorDirection.BOTTOM;
            }else if(yPx < 0){
                motorDirection = MotorDirection.TOP;
            }else{
                motorDirection = MotorDirection.NOT_MOVE;
            }
        }

        if(xPx != 0 && yPx != 0) {//不允许斜方向走动
            return false;
        }
        int actualXPX = getActualX() + xPx;
        int actualYPX = getActualY() + yPx;

        int li = (actualXPX - Common.interfaceStartX) / Room.CELL_WIDTH;
        int ri;
        if ((actualXPX - Common.interfaceStartX) % Room.CELL_WIDTH == 0) {
            ri = li;
        } else {
            ri = li + 1;
        }
        int tj = (actualYPX - Common.interfaceStartY) / Room.CELL_HEIGHT;
        int bj;
        if((actualYPX - Common.interfaceStartY) % Room.CELL_HEIGHT == 0){
            bj = tj;
        }else {
            bj = tj + 1;
        }
        if(xPx != 0) {
            if(getTy() != getBy()){//非正规移动
                return false;
            }else if(xPx > 0) {
                if (!beganToMove(tj,ri)) {
                    return false;
                }
            }else{
                if (!beganToMove(tj,li)) {
                    return false;
                }
            }
        }else if(yPx != 0){
            if(getLx() != getRx()) {
                return false;
            }else if(yPx > 0) {
                if (!beganToMove(bj,li)) {
                    return false;
                }
            }else {
                if (!beganToMove(tj,li)) {
                    return false;
                }
            }
        }

        this.actualX = actualXPX;
        this.actualY = actualYPX;
        this.lx = li;
        this.rx = ri;
        this.ty = tj;
        this.by = bj;

//        moveRecord.append(true);

        return true;
    }

    @Override
    public synchronized boolean die() {
        if(die.compareAndSet(false, true)){
            room.refreshFutureBody();
            new BiotaDieThread(this).start();
            return true;
        }else {
            return false;
        }
    }

    /**
     * 开始移动，记录移动后的坐标。
     * 修改目标状态
     */
    private boolean beganToMove(int y, int x){
        switch (motorDirection) {
            case NOT_MOVE:
                break;
            case TOP:
                setState(getState() == 5 ? 4 : 5);
                break;
            case BOTTOM:
                setState(getState() == 7 ? 6 : 7);
                break;
            case LEFT:
                setState(getState() == 9 ? 8 : 9);
                break;
            case RIGHT:
                setState(getState() == 11 ? 10 : 11);
                break;
        }


        if(!canMove(y,x)){
            return false;
        }else{
            moveY = y;
            moveX = x;
        }
        return true;
    }

    public boolean isDie() {
        return die.get();
    }

    public int getState() {
        return state;
    }

    public void setState(int state){
        this.state = state;
    }

    public int getActualX() {
        return actualX;
    }

    public int getActualY() {
        return actualY;
    }

    public int getLx() {
        return lx;
    }

    public int getRx() {
        return rx;
    }

    public int getTy() {
        return ty;
    }

    public int getBy() {
        return by;
    }

    public int getMoveX() {
        return moveX;
    }

    public int getMoveY() {
        return moveY;
    }

    public MotorDirection getMotorDirection() {
        return motorDirection;
    }
}
