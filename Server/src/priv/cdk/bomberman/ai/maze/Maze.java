package priv.cdk.bomberman.ai.maze;

import java.util.Random;

/**
 * 迷宫类， 随机生成一个迷宫的二维数组
 */
public class Maze {
    /**
     * 空地
     */
    public static final int SPACE = 0;

    /**
     * 墙壁
     */
    public static final int WALL = -1;


    // 初始化一个地图 默认所有路不通
    //最终产生的二维数组大小实际为(2width+1) * (2height+1)
    private final int width;
    private final int height;
    public final int[][] map;// 存放迷宫的数组
    private final int r;
    private final int c;

    public Maze(int r0, int c0) {
        width = r0;
        height = c0;
        r = 2 * width + 1;
        c = 2 * height + 1;
        map = new int[r][c];
    }

    public int[][] inIt() {
        for (int i = 0; i < r; i++) // 将所有格子都设为墙
            for (int j = 0; j < c; j++)
                map[i][j] = WALL;// -1 为墙 0为路
        // 中间格子放为1
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                map[2 * i + 1][2 * j + 1] = SPACE;// -1 为墙 0为路
        // 普里姆算法
        rdPrime();
        return map;
    }

    public void rdPrime() {
        // ok存放已访问队列，not存放没有访问队列
        int[] ok, not;
        int sum = width * height;
        int count = 0;// 记录访问过点的数量
        ok = new int[sum];
        not = new int[sum];
        // width上各方向的偏移 height各方向的偏移 0左 1右 3上 2下
        int[] offR = {-1, 1, 0, 0};
        int[] offC = {0, 0, 1, -1};

        // 四个方向的偏移 左右上下
        int[] offS = {-1, 1, width, -width}; // 向上向下移动都是变化一行
        // 初始化 ok中0代表未访问,not中0代表未访问
        for (int i = 0; i < sum; i++) {
            ok[i] =0;
            not[i] = 0;
        }
        // 起点
        Random rd = new Random();
        ok[0] = rd.nextInt(sum);// 起始点
        int pos = ok[0];
        // 第一个点存入
        not[pos] = 1;
        while (count < sum) {
            // 取出现在的点
            int x = pos % width;
            int y = pos / width;// 该点的坐标
            int offpos = -1;
            int w = 0;
            // 四个方向都尝试一遍 直到挖通为止
            while (++w < 5) {
                // 随机访问最近的点
                int point = rd.nextInt(4); // 0-3
                int repos;
                int move_x, move_y;
                // 计算出移动方位
                repos = pos + offS[point];// 移动后的下标
                move_x = x + offR[point];// 移动后的方位
                move_y = y + offC[point];
                if (move_y >= 0 && move_x >= 0 && move_x < width && move_y < height && repos >= 0 && repos < sum
                        && not[repos] != 1) {
                    not[repos] = 1;// 把该点标记为已访问
                    ok[++count] = repos;// ++count代表第几个已经访问的点,repos代表该点的下标
                    pos = repos;// 把该点作为起点
                    offpos = point;
                    // 相邻的格子中间的位置放0
                    map[2 * x + 1 + offR[point]][2 * y + 1 + offC[point]] = SPACE;
                    break;
                } else {
                    if (count == sum - 1)
                        return;
                }
            }
            if (offpos < 0) {// 周边没有找到能走的路了 从走过的路里重新找个起点
                pos = ok[rd.nextInt(count + 1)];
            }
        }
    }

    public int getR() {
        return r;
    }

    public int getC() {
        return c;
    }
}
