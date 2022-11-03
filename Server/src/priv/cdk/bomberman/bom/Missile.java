package priv.cdk.bomberman.bom;

import priv.cdk.bomberman.common.MotorDirection;
import priv.cdk.bomberman.parent.Biota;
import priv.cdk.bomberman.player.Player;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;
import priv.cdk.bomberman.utils.RoomUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class Missile extends Biota implements BomInterface{
    private final int moveSize;
    private final Biota possessor;//发射导弹的目标
    private final long id;
    private static final int MOVE_TIME = 20;
    private final int moveXSize;
    private final int moveYSize;

    private long lastUpdateTime = -1;

    public Missile(Room room, int lx, int ty, Biota possessor, MotorDirection motorDirection) {
        super(room, lx, ty);
        this.moveSize = Room.CELL_WIDTH / 10;
        this.possessor = possessor;
        this.id =  (possessor instanceof Player) ? ((Player)possessor).getId() : -1;

        this.motorDirection = motorDirection;
        switch (motorDirection) {
            case TOP:
                moveYSize = -moveSize;
                moveXSize = 0;
                break;
            case BOTTOM:
                moveYSize = moveSize;
                moveXSize = 0;
                break;
            case LEFT:
                moveYSize = 0;
                moveXSize = -moveSize;
                break;
            case RIGHT:
                moveYSize = 0;
                moveXSize = moveSize;
                break;
            default:
                moveYSize = 0;
                moveXSize = 0;
        }
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
            boolean b = Bom.wakeUpTheBomb(room, getMoveY(), getMoveX(), null);//碰到炸弹，爆炸

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

    @Override
    public void update(int[][] bomBody) {
        if (!isDie() && System.currentTimeMillis() - lastUpdateTime >= MOVE_TIME) {
            lastUpdateTime = System.currentTimeMillis();
            this.move(moveXSize, moveYSize);
        }
    }

    @Override
    public boolean die(){
        boolean die = super.die();
        if (die){
            if (possessor instanceof Player){
                ((Player) possessor).bomNumberAdd(id);
            }
        }
        return die;
    }
}
