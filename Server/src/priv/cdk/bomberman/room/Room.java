package priv.cdk.bomberman.room;

import priv.cdk.bomberman.bom.Bom;
import priv.cdk.bomberman.ai.maze.Maze;
import priv.cdk.bomberman.bom.BomThread;
import priv.cdk.bomberman.bom.Missile;
import priv.cdk.bomberman.bom.MissileMoveThread;
import priv.cdk.bomberman.charmander.Charmander;
import priv.cdk.bomberman.charmander.CharmanderThread;
import priv.cdk.bomberman.common.Common;
import priv.cdk.bomberman.critter.Critter;
import priv.cdk.bomberman.critter.CritterThread;
import priv.cdk.bomberman.critter.knight.Knight1Thread;
import priv.cdk.bomberman.critter.knight.KnightCritter;
import priv.cdk.bomberman.game.Game;
import priv.cdk.bomberman.parent.Biota;
import priv.cdk.bomberman.parent.MyThread;
import priv.cdk.bomberman.player.Player;
import priv.cdk.bomberman.utils.IsUtil;
import priv.cdk.bomberman.utils.RoomUtil;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

public class Room {
    public static final int CELL_WIDTH = 40;
    public static final int CELL_HEIGHT = 40;
    private final int w;
    private final int h;
    private int blank;//所有空白格的数量

    public final int wSize;
    public final int hSize;

    private final Game game;
    public CopyOnWriteArrayList<Player> ps;//所有玩家集合

    private final int[][] body;
    public int[][] futureBody;
    private final AtomicBoolean canDoor = new AtomicBoolean(true);//能否添加门
    public final AtomicBoolean suspend = new AtomicBoolean(false);//游戏暂停
    public final AtomicBoolean close = new AtomicBoolean(false);//房间关闭
    public final AtomicBoolean critterSuspend = new AtomicBoolean(false);//小怪移动

    private final Bom[][] bobs;

    public final CopyOnWriteArraySet<Critter> critters = new CopyOnWriteArraySet<>();
    public final CopyOnWriteArraySet<Charmander> charmanders = new CopyOnWriteArraySet<>();
    public final CopyOnWriteArraySet<Missile> missiles = new CopyOnWriteArraySet<>();
    public final CopyOnWriteArraySet<MyThread> threads = new CopyOnWriteArraySet<>();

    public Room(Game game, CopyOnWriteArrayList<Player> ps){
        this.game = game;

        int customsPass = game.getCustomsPass();

        if (customsPass%3 != 0) {
            h = Math.min(customsPass * 10 + 1 , 81);
            w = h;

            body = new int[h][w];

            addWallAll();
        }else {//迷宫图
            //生成迷宫
            Maze maze = new Maze(Math.min(customsPass * 5, 40), Math.min(customsPass * 5, 40));

            body = maze.inIt();

            h = maze.getR();
            w = maze.getC();
        }

        bobs = new Bom[h][w];

        wSize = w * CELL_WIDTH;
        hSize = h * CELL_HEIGHT;

        if(ps != null) {
            this.ps = ps;
        }else{
            this.ps = new CopyOnWriteArrayList<>();
        }

        //统计空格数
        int blankNumber = 0;
        for (int[] ints : body) {
            for (int anInt : ints) {
                if(anInt == 0){
                    blankNumber++;
                }
            }
        }

        blank = blankNumber;//总共的空格数

        //随机添加可破坏砖块
        int wallNumber = blank/8;//需要随机的墙数 概率
        int number = RoomUtil.randomNumberToBody(this, 3, wallNumber);
        blank -= number;

        //随机添加门
        RoomUtil.randomAddDoor(this);

        //创建elite body对象
        futureBody = new int[h][w];
        for (int i = 0; i < body.length; i++) {
            System.arraycopy(body[i], 0, futureBody[i], 0, body[i].length);
        }

        addCritterAll(customsPass);//添加小怪

        //一个十字框
        /*
        body[1][7] = -1;
        body[3][6] = -1;
        body[3][8] = -1;
        body[5][5] = -1;
        body[5][8] = -1;
        body[6][3] = -1;
        body[6][9] = -1;
        body[6][11] = -1;
        body[7][1] = -1;
        body[7][13] = -1;
        body[8][3] = -1;
        body[8][5] = -1;
        body[8][11] = -1;
        body[9][6] = -1;
        body[9][9] = -1;
        body[11][6] = -1;
        body[11][8] = -1;
        body[13][7] = -1;
        */


        new BomThread(this).start();
        new MissileMoveThread(this).start();

    }

    private void addWallAll(){
        for (int i = 0; i < h; i++) {
            this.body[i][0] = -1;
            this.body[i][w - 1] = -1;
        }

        for (int i = 0; i < w; i++) {
            this.body[0][i] = -1;
            this.body[h-1][i] = -1;
        }

        this.body[0][0] = -1;
        this.body[h - 1][w - 1] = -1;

        for (int i = 2; i < h - 2; i+=2) {//偶数行列添加无法破坏的墙
            for(int j=2; j < w - 2; j+=2){
                this.body[i][j] = -1;
            }
        }
    }

    /**
     * 添加小怪
     */
    private void addCritterAll(int customsPass){
        if(customsPass % 5 != 0) {//骑士关卡
            int eliteCritterNumber;
            if (customsPass > 1) {
                eliteCritterNumber = Math.min(100, customsPass - 3);
                RoomUtil.randomCritterToBody(this, 1, eliteCritterNumber);
            } else {
                eliteCritterNumber = 0;
            }

            int basicsCritterNumber = Math.min(100, Math.max(0, customsPass * customsPass - eliteCritterNumber));
            RoomUtil.randomCritterToBody(this, 0, basicsCritterNumber);

            if (new Random().nextInt( Math.max(1,20/customsPass) ) == 0) {//有概率出现两条龙
                RoomUtil.randomCritterToBody(this, 3, Math.min(2 * (customsPass/5), 10));
            }
        }else{
            int knightCritterNumber = Math.min(5, customsPass/5);
            int dragonCritterNumber = Math.min(10, customsPass/5 * 2);
            RoomUtil.randomCritterToBody(this, 2, knightCritterNumber);
            RoomUtil.randomCritterToBody(this, 3, dragonCritterNumber);
        }
    }

    //实际像素坐标
    @Deprecated
    public int getBodyCell(int x,int y){
        int i = (x - Common.interfaceStartX - CELL_WIDTH) / CELL_WIDTH;
        int j = (y - Common.interfaceStartY - CELL_HEIGHT) / CELL_HEIGHT;

        if(i<0 || j<0 || i >= body[0].length || j >= body.length){
            return -1;
        }else{
            return body[j][i];
        }
    }

    /**
     * 当前位置有玩家，则玩家死亡
     */
    public boolean playerDie(int y, int x){
        AtomicBoolean die = new AtomicBoolean(false);
        ps.forEach(player -> {
            if ( (!player.isDie()) && (!player.isFireImmune())) {
                if (player.getTy() == y || player.getBy() == y) {
                    if (player.getLx() == x || player.getRx() == x) {
                        player.die();
                        die.set(true);
                    }
                }
            }
        });
        return die.get();
    }


    /**
     * 以实际像素作为判断条件，判断两个目标是否重叠
     */
    public boolean dieActualXY(Collection<? extends Biota> biotas, Biota thisBiota){
        int actualX = thisBiota.getActualX();
        int actualY = thisBiota.getActualY();
        AtomicBoolean hasMissile = new AtomicBoolean(false);
        biotas.forEach(biota -> {
            if (thisBiota != biota && RoomUtil.toDetermineDeath(biota, actualX, actualY)) {
                hasMissile.set(true);
            }
        });
        return hasMissile.get();
    }

    /**
     * 清除墙
     */
    public boolean destroyTheWall(int y, int x){
        if(body[y][x] == 3){
            new DestroyWallThread(this, y, x).start();
            return true;
        }else{
            return body[y][x] == Common.PROP_DOOR;
        }
    }

    public boolean addBom(int x, int y, Bom bom){
        if(bom == null){
            removeBom(x, y);
        }else {
            if(bobs[x][y] != null){
                return false;
            }else {
                bobs[x][y] = bom;
                refreshFutureBody();
            }
        }
        return true;
    }

    public void removeBom(int x, int y){
        if(bobs[x][y] != null){
            bobs[x][y] = null;
            refreshFutureBody();
        }
    }

    public void addMissile(Missile missile){
        missiles.add(missile);
    }

    /**
     * 添加小怪
     */
    public void addCritter(int x, int y, int type){
        Critter critter;

        if(type == 0 || type == 1 || type == 3) {
            CritterThread critterThread = new CritterThread(this, x, y, type);
            critterThread.start();

            critter = critterThread.critter;
//        }else if (type == 2){
        }else{
            critter = new KnightCritter(this, x, y);
            Knight1Thread knight1Thread = new Knight1Thread(this, (KnightCritter) critter);
            knight1Thread.start();
        }
        this.critters.add(critter);//记录bom
    }

    /**
     * 添加小恐龙
     */
    public void addCharmander(int x, int y){
        Charmander charmander = new Charmander(this, x, y);
        CharmanderThread charmanderThread = new CharmanderThread(this, charmander);
        charmanderThread.start();
        this.charmanders.add(charmander);
    }



    /**
     * 刷新未来body
     */
    public void refreshFutureBody(){
        int[][] copyFutureBody = new int[h][w];

        Set<Bom> bobs = new HashSet<>();
        for (int i = 0; i < body.length; i++) {
            for (int j = 0; j < body[i].length; j++) {
                int bodyNumber = body[i][j];
                Bom bom = this.bobs[j][i];

                if(futureFireStop(bodyNumber)){
                    copyFutureBody[i][j] = bodyNumber;
                }else if(bom != null){
                    bobs.add(bom);
                }
            }
        }

        bobs.forEach(bom -> futureBodyBom(bom, copyFutureBody));

        futureBody = copyFutureBody;
    }

    private void futureBodyBom(Bom bom, int[][] copyFutureBody){
        copyFutureBody[bom.y][bom.x] = Common.FUTURE_BODY_FIRE_NUMBER_CENTRE;//将当前坐标数据变成火

        int x = bom.x;
        int y = bom.y;
        int size = bom.getSize();

        boolean top = true;
        boolean bottom = true;
        boolean left = true;
        boolean right = true;

        int i = 1;
        while (i < size) {
            if (top) {
                int reallyY = y - i;
                int reallyX = x;

                int bodyNumber = copyFutureBody[reallyY][reallyX];

                if (futureFireStop(copyFutureBody, reallyY, reallyX) || bodyNumber == Common.FUTURE_BODY_FIRE_NUMBER_CENTRE) {
                    top = false;
                } else {
                    copyFutureBody[reallyY][reallyX] = Common.FUTURE_BODY_FIRE_NUMBER_TOP;
                }
            }

            if (bottom) {
                int reallyY = y + i;
                int reallyX = x;

                int bodyNumber = copyFutureBody[reallyY][reallyX];

                if (futureFireStop(copyFutureBody, reallyY, reallyX) || bodyNumber == Common.FUTURE_BODY_FIRE_NUMBER_CENTRE) {
                    bottom = false;
                } else {
                    copyFutureBody[reallyY][reallyX] = Common.FUTURE_BODY_FIRE_NUMBER_BOTTOM;
                }
            }

            if (left) {
                int reallyY = y;
                int reallyX = x - i;

                int bodyNumber = copyFutureBody[reallyY][reallyX];

                if (futureFireStop(copyFutureBody, reallyY, reallyX) || bodyNumber == Common.FUTURE_BODY_FIRE_NUMBER_CENTRE) {
                    left = false;
                } else {
                    copyFutureBody[reallyY][reallyX] = Common.FUTURE_BODY_FIRE_NUMBER_LEFT;
                }
            }

            if (right) {
                int reallyY = y;
                int reallyX = x + i;

                int bodyNumber = copyFutureBody[reallyY][reallyX];

                if (futureFireStop(copyFutureBody, reallyY, reallyX) || bodyNumber == Common.FUTURE_BODY_FIRE_NUMBER_CENTRE) {
                    right = false;
                } else {
                    copyFutureBody[reallyY][reallyX] = Common.FUTURE_BODY_FIRE_NUMBER_RIGHT;
                }
            }

            i++;
        }
    }

    /**
     * 判断当前位置爆炸是否停止
     */
    private boolean futureFireStop(int[][] copyFutureBody, int reallyY, int reallyX){
        int bodyNumber = copyFutureBody[reallyY][reallyX];

        return futureFireStop(bodyNumber);
    }

    /**
     * 仅判断当前位置是否会爆炸通过
     */
    private boolean futureFireStop(int bodyNumber){
        return IsUtil.isWall(bodyNumber) || bodyNumber == Common.PROP_DOOR;
    }

    /**
     * 暂停游戏
     */
    public void suspendRoom(){
        if (!suspend.compareAndSet(false, true)) {
            startRoom();
        }else{
            critterSuspend.set(true);
        }
    }

    public void startRoom(){
        if (!suspend.compareAndSet(true, false)) {
            suspendRoom();
        }else{
            threads.forEach(myThread -> {
                synchronized (myThread) {
                    myThread.notify();
                }
            });
            critterSuspend.set(false);
        }
    }

    /**
     * 添加门
     */
    public boolean addDoor(int y, int x){
        if(canDoor.compareAndSet(true, false)){
            setBodyCellValue(y, x, Common.PROP_DOOR);
            return true;
        }
        return false;
    }

    public boolean isCanDoor(){
        return canDoor.get();
    }

    public Bom getBomCellValue(int x, int y){
        return bobs[x][y];
    }

    public void setBodyCellValue(int y, int x, int value){
        body[y][x] = value;

        if(value == 1001){
            refreshFutureBody();
        }
    }

    public int getBodyCellValue(int y, int x){
        if (y < 0 || y >= body.length){
            return -1;
        }
        if (x < 0 || x >= body[y].length){
            return -1;
        }
        return body[y][x];
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getBlank() {
        return blank;
    }

    public void reloadRoom(){
        closeRoom();
        game.reloadRoom();
    }

    public void reloadGame(){
        closeRoom();
        game.gameOver();
    }

    public void closeRoom(){
        if (close.compareAndSet(false, true)) {
            suspend.set(false);//暂停改为false

            critters.forEach(Biota::die);
            charmanders.forEach(Biota::die);

            threads.forEach(myThread -> {//将所有的自定义线程重新启动
                synchronized (myThread) {
                    if (myThread.isInterrupted()) {
                        myThread.interrupt();
                    } else {
                        myThread.notifyAll();
                    }
                }
            });
        }
    }

    public void addScore(int scoreNumber){
        if(!close.get()) {
            game.addScore(scoreNumber);
        }
    }

    public int getCustomsPass(){
        return game.getCustomsPass();
    }

    public void addPlayer(String name, boolean member){
        Player player;
        if(name != null && !name.equals("")) {
            player = new Player(this, 1, 1, name, member);
        }else{
            player = new Player(this, 1, 1, "P" + (ps.size() + 1), member);
        }
        ps.add(player);
    }

    public int[][] getBody() {
        return body;
    }

    public Bom[][] getBobs() {
        return bobs;
    }
}
