package priv.cdk.bomberman.charmander;

import priv.cdk.bomberman.critter.Critter;
import priv.cdk.bomberman.parent.MyThread;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 小火龙移动线程，自动追踪小怪,不包括骑士
 */
public class CharmanderThread extends MyThread {
    private final Charmander charmander;

    private final int[][] movementRoutes;

    public CharmanderThread(Room room, Charmander charmander) {
        super(room);

        this.charmander = charmander;

        movementRoutes = new int[room.getH()][room.getW()];
    }

    @Override
    public void myRun() {
        Point point = null;

        while (!charmander.isDie()) {

            if(point == null){
                point = findShortestPaths();
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
     * 反转链结构
     */
    public Point reverseList(Point head) {
        Point prev = null;
        Point curr = head;
        while (curr != null) {
            Point next = curr.last;
            curr.last = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }

    /**
     * 查询一条路径
     */
    public Point findShortestPaths() {

        int[][] body = myRoom.getBody();

        for (int i = 0; i < body.length; i++) {
            System.arraycopy(body[i], 0, movementRoutes[i], 0, body[i].length);
        }

        Point bfs = bfs(movementRoutes, charmander.getLx(), charmander.getTy());

        if (bfs != null) {

            Point point = reverseList(bfs);
            point.critter = bfs.critter;

            return point;
        }

        return null;
    }

    /**
     * 开始移动
     */
    public Point startMove(Point point){
        Critter critter = point.critter;
        Point next = point.last;

        while (next != null && !critter.isDie()) {
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

            if (critter.getLx() == critter.getRx() && critter.getTy() == critter.getBy()) {//如果追踪的怪移动了一格，那么重新计算

                Point shortestPaths = findShortestPaths();

                if(shortestPaths != null) {

                    Point last = shortestPaths.last;

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



    static class Point {
        int x;
        int y;
        int step;
        Point last;
        Critter critter;

        public Point(int y, int x, int step, Point last) {
            this.x = x;
            this.y = y;
            this.step = step;
            this.last = last;
        }
    }

    int[] dx = {1, -1, 0, 0};
    int[] dy = {0, 0, 1, -1};

    //    public int nearestExit(int[][] maze, int[] entrance) {
//        return bfs(maze,i,entrance[1]);
//    }
    public Point bfs(int[][] maze, int i, int j) {
        int m = maze.length;
        int n = maze[0].length;
        Queue<Point> queue = new LinkedList<>();
        queue.offer(new Point(j, i, 0, null));
        maze[i][j] = -1;
        while (!queue.isEmpty()) {
            Point poll = queue.poll();
            if (!(poll.x == i && poll.y == j)) {
                Critter critter = hasCritter(poll.y, poll.x);
                if (critter != null) {
                    poll.critter = critter;
                    return poll;
                }
                /*if(poll.x == 0 || poll.x == m - 1 || poll.y == 0 || poll.y == n - 1) {//终点
                    return poll.step;
                }*/
            }
            for (int k = 0; k < dx.length; k++) {
                int x = poll.x + dx[k];
                int y = poll.y + dy[k];
                if (x >= 0 && x < m && y >= 0 && y < n) {
                    int number = maze[y][x];
                    if (canMove(number)) {//可走区域
                        queue.offer(new Point(y, x, poll.step + 1, poll));
                        maze[y][x] = -1;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 判断当前位置是否有怪 ，如果有，那么返回怪
     */
    public Critter hasCritter(int y, int x) {
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

    public boolean canMove(int number) {
        return !IsUtil.isWall(number) && !IsUtil.isBom(number);
    }

}
