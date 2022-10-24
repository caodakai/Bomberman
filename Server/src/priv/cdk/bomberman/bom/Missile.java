package priv.cdk.bomberman.bom;

import priv.cdk.bomberman.parent.Biota;
import priv.cdk.bomberman.parent.BiotaUtil;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;
import priv.cdk.bomberman.utils.RoomUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class Missile extends Biota {

    public Missile(Room room, int lx, int ty) {
        super(room, lx, ty);
    }

    @Override
    public boolean canMove(int y, int x) {
        int bodyNumber = room.getBodyCellValue(y, x);
        return !IsUtil.isSpecialWall(bodyNumber) && !IsUtil.isFire(bodyNumber);
    }

    @Override
    public boolean move(int xPx, int yPx){
        boolean move = super.move(xPx, yPx);

        if (!move){
            die();//无法移动，爆炸
        }else {
            boolean b = Bom.wakeUpTheBomb(room, getMoveY(), getMoveX(), 0);//碰到炸弹，爆炸

            if (b){
                die();
            }

            /*boolean b1 = room.dieActualXY(room.missiles, this);//碰到其它导弹，爆炸

            if (b1){
                die();
            }*/

            boolean b2 = room.dieActualXY(room.ps, this);//碰到玩家，爆炸

            if (b2){
                die();
            }

            boolean b3 = room.destroyTheWall(getMoveY(), getMoveX());//碰到普通墙，爆炸

            if (b3){
                die();
            }

            boolean b4 = room.dieActualXY(room.critters, this);//碰到小怪，爆炸

            if (b4){
                die();
            }
        }

        return move;
    }

    @Override
    public int getDieTime(){
        return 40;
    }
}
