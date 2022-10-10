package priv.cdk.bomberman.charmander;

import priv.cdk.bomberman.ai.wayfinding.Point;
import priv.cdk.bomberman.ai.wayfinding.PointHelper;
import priv.cdk.bomberman.ai.wayfinding.PointInterface;
import priv.cdk.bomberman.critter.Critter;
import priv.cdk.bomberman.parent.MyThread;
import priv.cdk.bomberman.room.Room;

/**
 * 小火龙移动线程，自动追踪小怪,不包括骑士
 */
public class CharmanderThread extends MyThread {
    private final Charmander charmander;

    private final int[][] movementRoutes;

    private final PointHelper<Critter> pointHelper = new PointHelper<>(new PointInterface<Critter>() {
        /**
         * 判断当前位置是否有怪 ，如果有，那么返回怪
         */
        @Override
        public Critter hasT(int y, int x) {
            for (Critter critter : myRoom.critters) {
                if (!critter.isDie()) {
                    if (critter.getLx() == x) {
                        if (critter.getTy() == y || critter.getBy() == y) {
                            return critter;
                        }
                    } else if (critter.getRx() == x) {
                        if (critter.getTy() == y || critter.getBy() == y) {
                            return critter;
                        }
                    }
                }
            }

            return null;
        }

        @Override
        public boolean canMove(int number) {
            return charmander.canMove(number);
        }
    });

    public CharmanderThread(Room room, Charmander charmander) {
        super(room);

        this.charmander = charmander;

        movementRoutes = new int[room.getH()][room.getW()];
    }

    @Override
    public void myRun() {
        Point<Critter> point = null;

        while (!charmander.isDie()) {

            if(point == null){
                point = pointHelper.findShortestPaths(myRoom, movementRoutes, charmander);
            }

            if(point != null) {
                point = startMove(point);
            }

            try {
                mySleep(charmander.moveTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * 开始移动
     */
    public Point<Critter> startMove(Point<Critter> point){
        Critter critter = point.biota;
        Point<Critter> next = point.last;

        int critterTy = critter.getTy();
        int critterLx = critter.getLx();

        while (next != null && !critter.isDie() && !charmander.isDie()) {
            int x;
            int y;

            if (next.x > charmander.getLx()) {
                x = charmander.moveSize;
                y = 0;
            } else if (next.x < charmander.getLx()) {
                x = -charmander.moveSize;
                y = 0;
            } else {
                x = 0;
                if (next.y > charmander.getTy()) {
                    y = charmander.moveSize;
                } else if (next.y < charmander.getTy()) {
                    y = -charmander.moveSize;
                } else {
                    break;
                }
            }

            if (x != 0 && charmander.getTy() != charmander.getBy()) {//往左右走，但是并没有处于正中间 那么先往上走
                x = 0;
                y = -charmander.moveSize;
            }

            if (y != 0 && charmander.getLx() != charmander.getRx()) {//往上下走，但是并没有处于正中间 那么先往右走
                x = -charmander.moveSize;
                y = 0;
            }

            boolean move = charmander.move(x, y);//移动

            if (!move) {
                break;
            }

            if (charmander.getLx() == next.x && charmander.getTy() == next.y) {//如果移动到了下一个位置，那么再次移动到另一个位置
                next = next.last;
                if(next == null){
                    break;
                }
            }

            try {
                mySleep(charmander.moveTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (critter.getLx() != critterLx || critter.getTy() != critterTy) {//如果追踪的怪移动了一格，那么重新计算

                Point<Critter> shortestPaths = pointHelper.findShortestPaths(myRoom, movementRoutes, charmander);

                if(shortestPaths != null) {

                    Point<Critter> last = shortestPaths.last;

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
