package priv.cdk.bomberman.critter.knight;

import priv.cdk.bomberman.ai.wayfinding.Point;
import priv.cdk.bomberman.ai.wayfinding.PointHelper;
import priv.cdk.bomberman.ai.wayfinding.PointInterface;
import priv.cdk.bomberman.parent.BiotaUtil;
import priv.cdk.bomberman.parent.MyThread;
import priv.cdk.bomberman.player.Player;
import priv.cdk.bomberman.room.Room;

/**
 * 第二阶段骑士，追击玩家位置
 */
public class Knight2Thread extends MyThread {
    private final KnightCritter knightCritter;

    private final int[][] movementRoutes;

    private final PointHelper<Player> pointHelper = new PointHelper<>(new PointInterface<Player>() {
        /**
         * 判断当前位置是否有怪 ，如果有，那么返回怪
         */
        @Override
        public Player hasT(int y, int x) {
            for (Player player : myRoom.ps) {
                if(BiotaUtil.haveBiota(player, x, y)){
                    return player;
                }
            }

            return null;
        }

        @Override
        public boolean canMove(int number) {
            return knightCritter.canMove(number);
        }
    });

    public Knight2Thread(Room room, KnightCritter knightCritter) {
        super(room);
        this.knightCritter = knightCritter;

        movementRoutes = new int[room.getH()][room.getW()];
    }

    @Override
    public void myRun(){
        Point<Player> point = null;

        while (!knightCritter.isDie()) {

            if(point == null){
                point = pointHelper.findShortestPaths(myRoom, movementRoutes, knightCritter);
            }

            if(point != null) {
                point = startMove(point);
            }

            try {
                mySleep(knightCritter.moveTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始移动
     */
    public Point<Player> startMove(Point<Player> point){
        Player player = point.biota;
        Point<Player> next = point.last;

        int playerTy = player.getTy();
        int playerLx = player.getLx();

        while (next != null && !player.isDie() && !knightCritter.isDie()) {
            int x;
            int y;

            if (next.x > knightCritter.getLx()) {
                x = knightCritter.moveSize;
                y = 0;
            } else if (next.x < knightCritter.getLx()) {
                x = -knightCritter.moveSize;
                y = 0;
            } else {
                x = 0;
                if (next.y > knightCritter.getTy()) {
                    y = knightCritter.moveSize;
                } else if (next.y < knightCritter.getTy()) {
                    y = -knightCritter.moveSize;
                } else {
                    break;
                }
            }

            if (x != 0 && knightCritter.getTy() != knightCritter.getBy()) {//往左右走，但是并没有处于正中间 那么先往上走
                x = 0;
                y = -knightCritter.moveSize;
            }

            if (y != 0 && knightCritter.getLx() != knightCritter.getRx()) {//往上下走，但是并没有处于正中间 那么先往右走
                x = -knightCritter.moveSize;
                y = 0;
            }

            boolean move = knightCritter.move(x, y);//移动

            if (!move) {
                break;
            }

            if (knightCritter.getLx() == next.x && knightCritter.getTy() == next.y) {//如果移动到了下一个位置，那么再次移动到另一个位置
                next = next.last;
                if(next == null){
                    break;
                }
            }

            try {
                mySleep(knightCritter.moveTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (player.getLx() != playerLx || player.getTy() != playerTy) {//如果追踪的玩家移动了一格，那么重新计算

                Point<Player> shortestPaths = pointHelper.findShortestPaths(myRoom, movementRoutes, knightCritter);

                if(shortestPaths != null) {

                    Point<Player> last = shortestPaths.last;

                    if (last != null) {
                        if (last.x != next.x || last.y != next.y) {//如果下一次移动不是当前方向，那么退出当前移动规则，执行下一个移动规则
                            return shortestPaths;
                        }
                    }
                }

            }
        }

        return null;
    }

}
