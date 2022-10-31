package priv.cdk.bomberman.bom;

import priv.cdk.bomberman.parent.Biota;
import priv.cdk.bomberman.parent.BiotaUtil;
import priv.cdk.bomberman.player.Player;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;
import priv.cdk.bomberman.utils.RoomUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class Missile extends Biota {
    private final int moveSize;
    private final Biota possessor;//发射导弹的目标

    public Missile(Room room, int lx, int ty, Biota possessor) {
        super(room, lx, ty);
        this.moveSize = Room.CELL_WIDTH / 10;
        this.possessor = possessor;
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

            boolean b1 = missileToMissile();//碰到其它导弹，爆炸

            if (b1){
                die();
            }

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

    /**
     * 判断导弹是否碰到了其它导弹
     * 碰到就爆炸，碰到同一目标发射的导弹不爆炸
     */
    public boolean missileToMissile(){
        int actualX = this.getActualX();
        int actualY = this.getActualY();
        AtomicBoolean hasMissile = new AtomicBoolean(false);
        room.missiles.forEach(missile -> {
            if (missile.possessor != this.possessor && RoomUtil.toDetermineDeath(missile, actualX, actualY)) {
                hasMissile.set(true);
            }
        });
        return hasMissile.get();
    }

    @Override
    public int getDieTime(){
        return 40;
    }

    public int getMoveSize() {
        return moveSize;
    }
}
