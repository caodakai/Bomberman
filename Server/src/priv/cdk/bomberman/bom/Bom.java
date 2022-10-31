package priv.cdk.bomberman.bom;

import priv.cdk.bomberman.common.Common;
import priv.cdk.bomberman.critter.Critter;
import priv.cdk.bomberman.critter.knight.KnightCritter;
import priv.cdk.bomberman.parent.BiotaUtil;
import priv.cdk.bomberman.parent.MyThread;
import priv.cdk.bomberman.player.Player;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class Bom extends MyThread {
    public static final long BOM_TIME = 1000;//爆炸的速度
    public static final long FIRE_SPREAD = 200;//火焰展开的速度
    public static final long FIRE_SHRINK = 200;//火焰收缩的速度

    public final int x;
    public final int y;
    private int bomState = 1;
    private final int size;

    private final AtomicBoolean direction = new AtomicBoolean(false);//是否爆炸 true为已经爆炸了
    public int sourceDirection = 0;//爆炸来源方向，当其它炸弹爆炸时，会影响当前炸弹，0:自然爆炸，1:上,2:下,3:左,4:右

    private final Player player;
    private final long id;
    private final Room room;

    public Bom(int x, int y, Player player){
        super(player.room);

        this.x = x;
        this.y = y;
        this.player = player;
        this.id = player.getId();
        this.room = player.room;
        this.size = player.bomSize;
    }

    @Override
    public void myRun() {

        while (bomState <= 3){
            try {
                room.setBodyCellValue(y, x,1000 + bomState);//1001~1003
                bomState++;
                mySleep(BOM_TIME);
            } catch (InterruptedException e) {
//                e.printStackTrace();
                break;
            }
        }

        bomState = 4;
        direction.set(true);
        room.setBodyCellValue(y, x,1001);//1001~1003

        int tj = y;
        int bj = y;
        int li = x;
        int ri = x;
        int maxTj = y;
        int maxBj = y;
        int maxLi = x;
        int maxRi = x;

        int bomState2 = bomState;//记录回收状态

        int stateRefresh = 0;

        while (bomState <= 6) {
            int i = 0;

            int tj2 = tj;
            int bj2 = bj;
            int li2 = li;
            int ri2 = ri;
            boolean top = sourceDirection != 1;
            boolean bottom = sourceDirection != 2;
            boolean left = sourceDirection != 3;
            boolean right = sourceDirection != 4;

            while (i < size) {
                if(top){
                    int reallyY = y - i;
                    int reallyX = x;

                    boolean stop = fireStopSpread(reallyY, reallyX, i, 2);

                    if (stop){
                        top = false;
                        if (i == 0){
                            bottom = false;
                            left = false;
                            right = false;
                        }
                    }else{
                        fireFilter(reallyY, reallyX, i == 0, i == size - 1, 1, bomState);
                        tj = reallyY;
                    }

                    if(bomState != 4) {
                        if (tj2 == tj) {
                            top = false;
                        }
                    }
                }

                if(bottom){
                    int reallyY = y + i;
                    int reallyX = x;

                    boolean stop = fireStopSpread(reallyY, reallyX, i, 1);

                    if (stop){
                        bottom = false;
                    }else{
                        fireFilter(reallyY, reallyX, i == 0, i == size - 1, 2, bomState);
                        bj = reallyY;
                    }

                    if(bomState != 4) {
                        if (bj2 == bj) {
                            bottom = false;
                        }
                    }
                }

                if (left){
                    int reallyY = y;
                    int reallyX = x - i;

                    boolean stop = fireStopSpread(reallyY, reallyX, i, 4);

                    if (stop){
                        left = false;
                    }else{
                        fireFilter(reallyY, reallyX, i == 0, i == size - 1, 3, bomState);
                        li = reallyX;
                    }

                    if(bomState != 4) {
                        if (li2 == li) {
                            left = false;
                        }
                    }
                }

                if(right){
                    int reallyY = y;
                    int reallyX = x + i;

                    boolean stop = fireStopSpread(reallyY, reallyX, i, 3);

                    if (stop){
                        right = false;
                    }else{
                        fireFilter(reallyY, reallyX, i == 0, i == size - 1, 4, bomState);
                        ri = reallyX;
                    }

                    if(bomState != 4) {
                        if (ri2 == ri) {
                            right = false;
                        }
                    }
                }

                i++;
            }

            maxTj = Math.min(maxTj, tj);
            maxBj = Math.max(maxBj, bj);
            maxLi = Math.min(maxLi, li);
            maxRi = Math.max(maxRi, ri);

            if (bomState == 4) {
                new Thread(room::refreshFutureBody).start();
            }

            stateRefresh ++;
            if (stateRefresh == 3){
                stateRefresh = 0;
                bomState ++;
            }

            try {
                mySleep(FIRE_SPREAD/3);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }

        }

        int bomState2Index = 6;
        while(bomState2 <= bomState2Index) {
            int tj2 = tj, bj2 = bj, li2 = li, ri2= ri;

            while (tj2 <= y || bj2 >= y || li2 <= x || ri2 >= x) {
                if (tj2 <= y) {
                    fireFilter(tj2, x, tj2 == y, y - tj2 == size - 1, 1, bomState2Index);
                    tj2++;
                }
                if (bj2 >= y) {
                    fireFilter(bj2, x, bj2 == y, bj2 - y == size - 1, 2, bomState2Index);
                    bj2--;
                }
                if (li2 <= x) {
                    fireFilter(y , li2, li2 == x, x - li2 == size - 1, 3, bomState2Index);
                    li2++;
                }
                if (ri2 >= x) {
                    fireFilter(y , ri2, ri2 == x, ri2 - x == size - 1, 4, bomState2Index);
                    ri2--;
                }
            }

            stateRefresh ++;
            if (stateRefresh == 3){
                stateRefresh = 0;
                bomState2Index --;
            }

            try {
                mySleep(FIRE_SHRINK/3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        while (maxTj <= y || maxBj >= y || maxLi <= x || maxRi >= x) {
            if (maxTj <= y) {
                removeFire(maxTj, x);
                maxTj++;
            }
            if (maxBj >= y) {
                removeFire(maxBj, x);
                maxBj--;
            }
            if (maxLi <= x) {
                removeFire(y, maxLi);
                maxLi++;
            }
            if (maxRi >= x) {
                removeFire(y, maxRi);
                maxRi--;
            }
        }

        room.removeBom(x, y);//尝试清除炸弹对象
        player.bomNumberAdd(id);
    }

    public void setUpFireDestination(int bodyCellValue, int reallyY, int reallyX, int bomState){
        if (bodyCellValue == 0 || IsUtil.isProp(bodyCellValue) || IsUtil.isUpDestination(bodyCellValue)){//如果为道具或者为上终火
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1003 + bomState);//1007~1009
        }else if (IsUtil.isDown(bodyCellValue) || IsUtil.isDownDestination(bodyCellValue) || IsUtil.isUp(bodyCellValue)){//如果为向下的火，那么改成上火
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1006 + bomState);//1010~1012
        }else{
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1000 + bomState);//1004~1006
        }
    }

    public void setUpFire(int bodyCellValue, int reallyY, int reallyX, int bomState){
        if (bodyCellValue == 0 || IsUtil.isProp(bodyCellValue) || IsUtil.isUpDestination(bodyCellValue) || IsUtil.isUp(bodyCellValue) ||
            IsUtil.isDownDestination(bodyCellValue) || IsUtil.isDown(bodyCellValue)) {

            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1006 + bomState);//1010~1012
        }else{
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1000 + bomState);//1004~1006
        }
    }

    public void setDownFireDestination(int bodyCellValue, int reallyY, int reallyX, int bomState){
        if (bodyCellValue == 0 || IsUtil.isProp(bodyCellValue) || IsUtil.isDownDestination(bodyCellValue)){
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1009 + bomState);//1013~1015
        }else if (IsUtil.isUpDestination(bodyCellValue) || IsUtil.isUp(bodyCellValue)){
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1012 + bomState);//1016~1018
        }else if (!IsUtil.isDown(bodyCellValue)){
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1000 + bomState);//1004~1006
        }else{
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1012 + bomState);//1016~1018
        }
    }

    public void setDownFire(int bodyCellValue, int reallyY, int reallyX, int bomState){
        if (bodyCellValue == 0 || IsUtil.isProp(bodyCellValue) || IsUtil.isUpDestination(bodyCellValue) || IsUtil.isUp(bodyCellValue) ||
                IsUtil.isDownDestination(bodyCellValue) || IsUtil.isDown(bodyCellValue)) {

            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1012 + bomState);//1016~1018
        }else{
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1000 + bomState);//1004~1006
        }
    }

    public void setLeftFireDestination(int bodyCellValue, int reallyY, int reallyX, int bomState){
        if (bodyCellValue == 0 || IsUtil.isProp(bodyCellValue) || IsUtil.isLeftDestination(bodyCellValue)){
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1015 + bomState);//1019~1021
        }else if (IsUtil.isRightDestination(bodyCellValue) || IsUtil.isRight(bodyCellValue)){
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1018 + bomState);///1022~1024
        }else if (!IsUtil.isLeft(bodyCellValue)){
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1000 + bomState);//1004~1006
        }else{
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1018 + bomState);///1022~1024
        }
    }

    public void setLeftFire(int bodyCellValue, int reallyY, int reallyX, int bomState){
        if (bodyCellValue == 0 || IsUtil.isProp(bodyCellValue) || IsUtil.isLeftDestination(bodyCellValue) || IsUtil.isLeft(bodyCellValue) ||
                IsUtil.isRightDestination(bodyCellValue) || IsUtil.isRight(bodyCellValue)) {

            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1018 + bomState);///1022~1024
        }else{
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1000 + bomState);//1004~1006
        }
    }

    public void setRightFireDestination(int bodyCellValue, int reallyY, int reallyX, int bomState){
        if (bodyCellValue == 0 || IsUtil.isProp(bodyCellValue) || IsUtil.isRightDestination(bodyCellValue)){
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1021 + bomState);//1025~1027
        }else if (IsUtil.isLeftDestination(bodyCellValue) || IsUtil.isLeft(bodyCellValue)){
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1024 + bomState);//1028~1030
        }else if (!IsUtil.isRight(bodyCellValue)){
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1000 + bomState);//1004~1006
        }else{
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1024 + bomState);//1028~1030
        }
    }

    public void setRightFire(int bodyCellValue, int reallyY, int reallyX, int bomState){
        if (bodyCellValue == 0 || IsUtil.isProp(bodyCellValue) || IsUtil.isLeftDestination(bodyCellValue) || IsUtil.isLeft(bodyCellValue) ||
                IsUtil.isRightDestination(bodyCellValue) || IsUtil.isRight(bodyCellValue)) {

            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1024 + bomState);//1028~1030
        }else{
            setBodyFireValue(reallyY, reallyX, bodyCellValue, 1000 + bomState);//1004~1006
        }
    }

    public void setBodyFireValue(int reallyY, int reallyX, int bodyNumber, int value){
        if (bodyNumber == value){
            return;
        }

        if (bodyNumber < 1000 || bodyNumber > 1999){
            room.setBodyCellValue(reallyY, reallyX, value);
            return;
        }

        if (bomState <= 6){//火焰扩大阶段
            int i = bodyNumber - 1003;
            int i1 = i % 3;

            int min = (i == 1 || i == 2) ? Math.min(bodyNumber + 1, bodyNumber - i1 + 3) : value;

            room.setBodyCellValue(reallyY, reallyX, min);
        }else{
            if (IsUtil.isMiddle(value)){
                if (!IsUtil.isMiddle(bodyNumber)){

                    room.setBodyCellValue(reallyY, reallyX, value);
                }else{
                    room.setBodyCellValue(reallyY, reallyX, Math.min(bodyNumber, value));
                }
                return;
            }

            int i = bodyNumber - 1003;
            int i1 = i % 3;

            int max = (i1 != 1) ? Math.max(bodyNumber - 1, value) : value;

            room.setBodyCellValue(reallyY, reallyX, max);
        }
    }



    /**
     * 火焰停止蔓延
     * @param i 阶段
     * @param sourceDirection 方向
     */
    private boolean fireStopSpread(int reallyY, int reallyX, int i, int sourceDirection){
        int bodyNumber = room.getBodyCellValue(reallyY, reallyX);

        if (bodyNumber < 0){//不可破坏的墙，停止蔓延
            return true;
        }

        if (bodyNumber == Common.PROP_DOOR){//门处停止蔓延
            return true;
        }

        if (IsUtil.isOrdinaryWall(bodyNumber) | room.destroyTheWall(reallyY, reallyX)){//摧毁墙
            return true;
        }

        boolean b = false;

        for (Critter critter : room.critters) {
            if (critter instanceof KnightCritter) {
                if (BiotaUtil.haveBiota(critter, x, y)) {
                    b = ! critter.die();//骑士未死亡，那么不可穿过火
                }
            }
        }

        if (b){//如果执行过骑士死亡，并且执行失败，那么停止蔓延
            return true;
        }

        if (i != 0 && wakeUpTheBomb(room, reallyY, reallyX, sourceDirection)){//如果当前位置是炸弹，并且引爆了炸弹，那么停止蔓延
            return true;
        }

        return false;
    }

    /**
     * 清除火 消除火的时候，仅仅消除墙
     */
    private void removeFire(int y, int x){
        if (!room.destroyTheWall(y,x)) {
            int i = (room.getBodyCellValue(y, x) - 1003) % 3;
            if (i == 1) {
                room.setBodyCellValue(y, x, 0);
            }
        }
    }

    /**
     * 唤醒炸弹，如果坐标是其它炸弹，那么唤醒该炸弹
     */
    public static boolean wakeUpTheBomb(Room room, int y, int x, int sourceDirection){
        if(IsUtil.isBom(room.getBodyCellValue(y, x))){
            Bom bom = room.getBomCellValue(x, y);
            if (bom != null && bom.wakeUp(sourceDirection)){
                return true;
            }
        }
        return false;
    }

    public boolean wakeUp(int sourceDirection){
        if (this.direction.compareAndSet(false, true)) {
            this.sourceDirection = sourceDirection;
            this.interrupt();
            return true;
        }
        return false;
    }

    /**
     * 火焰监听 ， 清除小怪， 清除玩家
     */
    private void fireFilter(int reallyY, int reallyX, boolean midpoint, boolean destination, int fireType,int thisBomState){

        room.playerDie(reallyY, reallyX);

        room.critters.forEach(critter ->{
            if(critter.getLx() == reallyX){
                if(critter.getTy() == reallyY || critter.getBy() == reallyY){
                    critter.die();
                }
            }else if(critter.getRx() == reallyX){
                if(critter.getTy() == reallyY || critter.getBy() == reallyY){
                    critter.die();
                }
            }
        });

        if (midpoint) {
            room.setBodyCellValue(reallyY, reallyX, 1000 + thisBomState);//1004~1006
        } else {
            int bodyCellValue = room.getBodyCellValue(reallyY, reallyX);

            switch (fireType){
                case 1:
                    if (destination) {
                        setUpFireDestination(bodyCellValue, reallyY, reallyX, thisBomState);
                    }else{
                        setUpFire(bodyCellValue, reallyY, reallyX, thisBomState);
                    }
                    break;
                case 2:
                    if (destination){
                        setDownFireDestination(bodyCellValue, reallyY, reallyX, thisBomState);
                    } else {
                        setDownFire(bodyCellValue, reallyY, reallyX, thisBomState);
                    }
                    break;
                case 3:
                    if (destination){
                        setLeftFireDestination(bodyCellValue, reallyY, reallyX, thisBomState);
                    } else {
                        setLeftFire(bodyCellValue, reallyY, reallyX, thisBomState);
                    }
                    break;
                case 4:
                    if (destination){
                        setRightFireDestination(bodyCellValue, reallyY, reallyX, thisBomState);
                    } else {
                        setRightFire(bodyCellValue, reallyY, reallyX, thisBomState);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public Player getPlayer() {
        return player;
    }
}
