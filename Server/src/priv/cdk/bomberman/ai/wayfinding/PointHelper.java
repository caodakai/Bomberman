package priv.cdk.bomberman.ai.wayfinding;

import priv.cdk.bomberman.parent.Biota;
import priv.cdk.bomberman.room.Room;

import java.util.LinkedList;
import java.util.Queue;

public class PointHelper<T> {

    private final PointInterface<T> function;

    public PointHelper(PointInterface<T> function) {
        this.function = function;
    }

    /**
     * 反转链结构
     */
    public Point<T> reverseList(Point<T> head) {
        Point<T> prev = null;
        Point<T> curr = head;
        while (curr != null) {
            Point<T> next = curr.last;
            curr.last = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }


    /**
     * 查询一条路径
     */
    public Point<T> findShortestPaths(Room room, int[][] movementRoutes, Biota biota) {

        int[][] body = room.getBody();

        for (int i = 0; i < body.length; i++) {
            System.arraycopy(body[i], 0, movementRoutes[i], 0, body[i].length);
        }

        Point<T> bfs = bfs(movementRoutes, biota.getLx(), biota.getTy());

        if (bfs != null) {

            Point<T> point = reverseList(bfs);
            point.biota = bfs.biota;

            return point;
        }

        return null;
    }

    private static final int[] dx = {1, -1, 0, 0};
    private static final int[] dy = {0, 0, 1, -1};

    public Point<T> bfs(int[][] maze, int i, int j) {
        int m = maze.length;
        int n = maze[0].length;
        Queue<Point<T>> queue = new LinkedList<>();
        queue.offer(new Point<>(j, i, 0, null));
        maze[i][j] = -1;
        while (!queue.isEmpty()) {
            Point<T> poll = queue.poll();
            if (!(poll.x == i && poll.y == j)) {
                T t = function.hasT(poll.y, poll.x);
                if (t != null) {
                    poll.biota = t;
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
                    if (function.canMove(number)) {//可走区域
                        queue.offer(new Point<>(y, x, poll.step + 1, poll));
                        maze[y][x] = -1;
                    }
                }
            }
        }
        return null;
    }
}
