package priv.cdk.bomberman.player;

import priv.cdk.bomberman.bom.Bom;
import priv.cdk.bomberman.bom.Missile;
import priv.cdk.bomberman.bom.MissileMoveThread;
import priv.cdk.bomberman.common.Common;
import priv.cdk.bomberman.common.MotorDirection;
import priv.cdk.bomberman.parent.Biota;
import priv.cdk.bomberman.parent.MyThread;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Player extends Biota {
    private static final String CLASSNAME = Player.class.getName();

    private final static boolean TEST = false;//是否为测试，如果是测试，那么创建的玩家都是无敌的

    private final AtomicLong ID = new AtomicLong(1L);//当前对象是为了用于顽疾死亡校验，如果获取是否死亡状态不等于当前ID，那么会返回是死亡状态

    private final boolean member;//会员

    public final String name;

    private int maxBomNumber;
    private final AtomicInteger bomNumber;
    public int bomSize;
    private int pressProcessed;//移动速度
    private int moveInterval;//移动间隔时间
    public final MoveThread moveThread;
    public final AtomicBoolean canMoveThread;

    private boolean bomControl;//炸弹爆炸控制
    private boolean bomThrough;//炸弹穿越
    private boolean wallThrough;//墙壁穿越
    public final AtomicBoolean questionMark;//短暂无敌
    private boolean fireImmune;//火焰免疫
    private boolean tank;//处于坦克状态

    public final QuestionMarkThread questionMarkThread;


//    public int speed = Room.CELL_WIDTH/4;
    public int speed = Room.CELL_WIDTH/4 * 3;

    public Player(Room room, int lx, int ty, String name, boolean member){
        super(room,lx,ty);

        this.name = name;

        if (TEST){
            this.member = true;
        }else this.member = member;

        this.bomNumber = new AtomicInteger(this.member ? this.room.getBlank() : 1);
        this.maxBomNumber = this.bomNumber.get();

        this.bomSize = this.member ? 5 : 2;
        this.pressProcessed = 50;
        this.moveInterval = this.member ? 0 : 180;

        this.moveThread = new MoveThread(this);
        this.canMoveThread = new AtomicBoolean(true);

        this.moveThread.start();

        this.bomControl = this.member;
        this.bomThrough = this.member;
        this.wallThrough = this.member;
        this.fireImmune = this.member;
        this.questionMark = new AtomicBoolean(true);
        this.tank = this.member;

        this.questionMarkThread = new QuestionMarkThread(room, this);
        this.questionMarkThread.start();
    }

    @Override
    public boolean revive(){
        if (super.revive()) {
            this.ID.addAndGet(1); // 修改玩家ID
            this.bomNumber.set(this.member ? this.room.getBlank() : 1); // 炸弹数量重置
            this.maxBomNumber = this.bomNumber.get(); // 最大炸弹数量重置
            this.canMoveThread.set(true); // 允许移动
            this.questionMark.set(false); // 设置无敌为否

            this.questionMarkThread.openQuestionMark(5); // 启动5秒的无敌时间

            return true;
        }

        return false;
    }

    public void reload(Room room){
        this.ID.addAndGet(1);

        setPosition(1,1);
        this.room = room;

        if(this.member) {
            this.bomNumber.set(this.room.getBlank());
            this.maxBomNumber = this.bomNumber.get();
        }else{//过关后，火焰免疫取消
            this.bomNumber.set(this.maxBomNumber);
            this.fireImmune = false;
        }
    }

    //移动
    @Override
    public boolean move(int xPx,int yPx){
//        System.out.println(name + "移动0");
        if(canMoveThread.get() && !isDie()) {

            boolean b = ! playerMoveOnce(xPx/3, yPx/3, false);

            if(b) {
                canMoveThread.set(false);
                synchronized (moveThread) {
                    moveThread.init(xPx, yPx);
                    moveThread.notify();
                }
            }

            return b;
        }else{
            return false;
        }
    }

    /**
     * 玩家移动一次
     * @return true : 可以继续移动，不允许继续移动
     */
    public boolean playerMoveOnce(int xPx, int yPx, boolean sleep) {
        if(sleep) {
            try {
                MyThread.mainSleep(pressProcessed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if ((!canMoveThread.get()) || !sleep) {
            boolean b = playerMove(xPx / 3, yPx / 3);

            return !b;
        }else{
            return true;
        }
    }

    /**
     * 玩家移动 ，将一次移动拆分为3次
     */
    public boolean playerMove(int xPx,int yPx){
        boolean move = super.move(xPx, yPx);

        if(move){
            int moveX = getMoveX();
            int moveY = getMoveY();
            int bodyNumber = room.getBodyCellValue(moveY, moveX);
            if(IsUtil.isProp(bodyNumber)){
                switch (bodyNumber){
                    case Common.PROP_SCOPE_ADD:
                        bomSize ++;
                        break;
                    case Common.PROP_BOM_ADD:
                        bomNumberAdd(this.ID.get());
                        this.maxBomNumber++;
                        break;
                    case Common.PROP_BOM_CONTROL:
                        bomControl = true;
                        break;
                    case Common.PROP_SPEED_ADD:
//                        int i = Room.CELL_WIDTH / speed;//计算运动多少次可以走完一格
//                        speed = i <= 1 ? Room.CELL_WIDTH : ( Room.CELL_WIDTH / (i - 1) );
                        pressProcessed *= 0.8;
                        moveInterval *= 0.8;
                        speed =  Room.CELL_WIDTH - (int) (Room.CELL_WIDTH / ((Room.CELL_WIDTH / (Room.CELL_WIDTH - (double) speed)) * 2));//y(x) = 40 - 40/x ;
                        break;
                    case Common.PROP_BOM_THROUGH:
                        bomThrough = true;
                        break;
                    case Common.PROP_WALL_THROUGH:
                        wallThrough = true;
                        break;
                    case Common.PROP_QUESTION_MARK:
                        questionMarkThread.openQuestionMark(10);
                        break;
                    case Common.PROP_FIRE_IMMUNE:
                        fireImmune = true;
                        break;
                    case Common.PROP_TANK:
                        questionMarkThread.openQuestionMark(20);//开启无敌时间20秒
                        tank = true;
                        break;
                    case Common.PROP_DOOR:
                        if(room.critters.size() == 0) {
                            room.reloadRoom();
                            canMoveThread.set(false);
                        }
                        break;
                }

                if(bodyNumber != Common.PROP_DOOR) {
                    room.setBodyCellValue(moveY, moveX, 0);
                }
            }
        }

        return move;
    }

    /**
     * 当前位置能否添加炸弹
     */
    public boolean canAddBom(int x, int y){
        if(bomNumber.get() > 0){
            if(room.getBomCellValue(x, y) == null){
                int bodyNumber = room.getBodyCellValue(y, x);
                return !IsUtil.isBom(bodyNumber) && !IsUtil.isWall(bodyNumber) && !IsUtil.isFire(bodyNumber) && !IsUtil.isProp(bodyNumber);
            }
        }
        return false;
    }

    /**
     * 添加炸弹
     */
    public Bom addBom(int y, int x){
        if(canAddBom(x, y)) {
            Bom bom = new Bom(x, y, this);
            if (room.addBom(x, y, bom)) {
                bomNumber.addAndGet(-1);
                return bom;
            }
        }
        return null;
    }

    /**
     * 添加导弹
     */
    public void addMissile(int y, int x, MotorDirection motorDirection){
        if (bomNumber.get() > 0) {
            Missile missile = new Missile(room, x, y, this, motorDirection);
            if (y == getMoveY() && x == getMoveX()){
                switch (motorDirection) {
                    case TOP:
                        missile.setActualPosition(getActualX(), getActualY() - Room.CELL_HEIGHT + missile.getMoveSize());
                        break;
                    case BOTTOM:
                        missile.setActualPosition(getActualX(), getActualY() + Room.CELL_HEIGHT - missile.getMoveSize());
                        break;
                    case LEFT:
                        missile.setActualPosition(getActualX() - Room.CELL_WIDTH + missile.getMoveSize(), getActualY());
                        break;
                    case RIGHT:
                        missile.setActualPosition(getActualX() + Room.CELL_WIDTH - missile.getMoveSize(), getActualY());
                        break;
                }
            }
            room.addMissile(missile);
            bomNumber.addAndGet(-1);
        }
    }

    public void randomAddBobs(){
        int size = Math.min(room.getBlank()/4, 100);
        Random random = new Random();
        for (int i=1; i < room.getH() - 1; i++){
            for (int j=1; j< room.getW() - 1; j++){
                if(canAddBom(j, i)){
                    int randomNumber = random.nextInt(room.getBlank());
                    if(randomNumber < size){
                        if (isTank()){
                            addMissile(j, i, getMotorDirection());
                        }else {
                            addBom(j, i);
                        }
                    }
                }
            }
        }
    }

    /**
     * 唤醒所有炸弹
     */
    public void wakeUpTheBombAll(){
        for (int i = 0; i < room.getH(); i++) {
            for (int j = 0; j < room.getW(); j++) {
                Bom bom = room.getBomCellValue(i, j);
                if(bom != null){
                    Bom.wakeUpTheBomb(room, j, i, this);
                }
            }
        }
    }


    @Override
    public boolean canMove(int y, int x) {
        if(isMember()){
            return y > 0 && y < room.getH() - 1 && x > 0 && x < room.getW() - 1;
        }

        int bodyNumber = room.getBodyCellValue(y, x);
        if(IsUtil.isOrdinaryWall(bodyNumber)){
            return wallThrough;
        }

        if(IsUtil.isBom(bodyNumber)){
            if(IsUtil.isBom(room.getBodyCellValue(getTy() == y ? getTy() : getBy(), getLx() == x ? getLx() : getRx()))) {//本身已经在炸弹上，那么可以移动
                return true;
            }else{
                return bomThrough;
            }
        }

        return !IsUtil.isWall(bodyNumber);
    }

    public boolean isDie(long id){
        if (id == ID.get()){
            return isDie();
        }else {
            return true;
        }
    }

    @Override
    public boolean die(){
        if(!this.isQuestionMark()) {
            boolean die = super.die();

            AtomicBoolean canReloadGame = new AtomicBoolean(true);
            room.ps.forEach(player -> {
                if(!player.isDie()){
                    canReloadGame.set(false);
                }
            });

            if(canReloadGame.get()) {
                room.reloadGame();
            }

            return die;
        }
        return false;
    }

    @Override
    public void setState(int state){
        if (state == 3 || state == 2 || state == 1 || state == 0){
            if (!isDie()){//设置状态为死亡时，并未死亡，那么不进行设置
                return;
            }
        }
        super.setState(state);
    }

    public boolean gameStop(){
        return room.gameStop();
    }

    public void stop(){
        super.die();
    }

    private void println(String message, String method){
        System.out.println(CLASSNAME + method + ":   " + message);
    }

    public long getId(){
        return ID.get();
    }

    public int getBomNumber() {
        return bomNumber.get();
    }

    public void bomNumberAdd(long id){
        if (id == ID.get()) {
            bomNumber.addAndGet(1);
        }
    }

    public int getBomSize() {
        return bomSize;
    }

    public boolean isBomControl() {
        return bomControl;
    }

    public boolean isBomThrough() {
        return bomThrough;
    }

    public boolean isWallThrough() {
        return wallThrough;
    }

    public boolean isQuestionMark() {
        return questionMark.get();
    }

    public boolean isFireImmune() {
        return fireImmune;
    }

    public int getPressProcessed() {
        return pressProcessed;
    }

    public int getMoveInterval() {
        return moveInterval;
    }

    public boolean isMember() {
        return member;
    }

    public boolean isTank() {
        return tank;
    }

    public void setTank(boolean tank) {
        this.tank = tank;
    }
}
