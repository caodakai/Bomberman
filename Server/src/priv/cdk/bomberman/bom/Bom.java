package priv.cdk.bomberman.bom;

import priv.cdk.bomberman.common.Common;
import priv.cdk.bomberman.critter.Critter;
import priv.cdk.bomberman.critter.knight.KnightCritter;
import priv.cdk.bomberman.parent.BiotaUtil;
import priv.cdk.bomberman.player.Player;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;

public class Bom implements BomInterface{
    public final static int AWAIT_TIME = 1500;
    public final static int BLAST_TIME_1 = 150;
    public final static int BLAST_TIME_2 = 200;
    private boolean over = false;
    private final int[][] realy = {{-1,0},{1,0},{0,-1},{0, 1}};
    private final int middle = 1004;
    private final int[][] fires = {{1007,1010},{1013,1016},{1019,1022},{1025,1028}};

    private long lastUpdateTime = -1;

    public final int x;
    public final int y;
    private int bomState = 1;
    private final long id;//用来判断当前用户是否发生过改变
    private final int size;

    private final Player player;
    private final Room room;

    public Bom(int x, int y, Player player){
        this.x = x;
        this.y = y;
        this.player = player;
        this.id = player.getId();
        this.room = player.room;
        this.size = player.bomSize;
    }

    @Override
    public void update(int[][] bomBody) {
        switch (bomState){
            case 0:
            case 1:
            case 2:
            case 3:
                if (System.currentTimeMillis() - lastUpdateTime < AWAIT_TIME) {
                    bomState--;
                }else{
                    lastUpdateTime = System.currentTimeMillis();
                }
                break;
            case 4:
            case 5:
            case 6:
                if (System.currentTimeMillis() - lastUpdateTime < BLAST_TIME_1) {
                    bomState--;
                }else{
                    lastUpdateTime = System.currentTimeMillis();
                }
                break;
            case 7:
            case 8:
            case 9:
                if (System.currentTimeMillis() - lastUpdateTime < BLAST_TIME_2) {
                    bomState--;
                }else{
                    lastUpdateTime = System.currentTimeMillis();
                }
                break;
            default:
                break;
        }

        switch (bomState){
            case 0:
            case 1:
            case 2:
            case 3:
                bomBody[x][y] = 1000 + bomState;
                bomState++;
                break;
            case 4:
            case 5:
            case 6:
                diffusionState(bomBody, bomState - 4);
                bomState++;
                break;
            case 7:
            case 8:
            case 9:
                diffusionState(bomBody, 9 - bomState);
                bomState++;
                break;
            default:
                over = true;
                player.bomNumberAdd(id);
                break;
        }
    }

    /**
     * 火焰扩散状态
     */
    private void diffusionState(int[][] bomBody, int state){
        setBomBodyValue(y, x, bomBody, middle + state);
        int i = 0;
        boolean isStop = false;
        boolean[] stop = new boolean[4];
        while (i++ < size && !isStop){
            isStop = true;
            for (int j = 0; j < stop.length; j++) {
                if (!stop[j]){
                    isStop = false;

                    int reallyY = y + i * realy[j][0], reallyX = x + i * realy[j][1];
                    int number = i == size ? fires[j][0] : fires[j][1];

                    int bomBodyNumber = fireStopSpread(reallyX, reallyY, i, bomBody);

                    if (bomBodyNumber == -1){
                        stop[j] = true;
                    }else if (bomBodyNumber == 0){
                        setBomBodyValue(reallyY, reallyX, bomBody, number + state);
                    }else{
                        int readyNumber = fires[j][1] + state;
                        if (isCross(bomBodyNumber, readyNumber)){
                            readyNumber = middle + state;
                        }
                        int a = (bomBodyNumber - 1004) % 3;
                        setBomBodyValue(reallyY, reallyX, bomBody, a > state ? bomBodyNumber : readyNumber);
                    }
                }
            }
        }
    }

    /**
     * 判断两个火焰是否相交
     */
    public boolean isCross(int number1, int number2){
        if (number1 < 1007 || number2 < 1007){//有一个为中间火
            return true;
        }

        if (number1 < 1019 && number2 >= 1019){
            return true;
        }

        if (number1 >= 1019 && number2 < 1019){
            return true;
        }

        return false;
    }


    public int fireStopSpread(int bodyX, int bodyY,int i, int[][] bomBody){
        int bodyNumber = room.getBodyCellValue(bodyY, bodyX);

        if (bodyNumber < 0){//不可破坏的墙，停止蔓延
            return -1;
        }

        if (bodyNumber == Common.PROP_DOOR){//门处停止蔓延
            return -1;
        }

        if (IsUtil.isOrdinaryWall(bodyNumber) | room.destroyTheWall(bodyY, bodyX)){//摧毁墙
            return -1;
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
            return -1;
        }

        if (i != 0 && wakeUpTheBomb(room, bodyY, bodyX, null)){//如果当前位置是炸弹，并且引爆了炸弹，那么停止蔓延
            return -1;
        }

        return bomBody[bodyX][bodyY];
    }

    /**
     * 唤醒炸弹，如果坐标是其它炸弹，那么唤醒该炸弹
     */
    public static boolean wakeUpTheBomb(Room room, int y, int x, Player player){
        if(IsUtil.isBom(room.getBodyCellValue(y, x))){
            Bom bom = room.getBomCellValue(x, y);
            if (bom != null && (player == null || (bom.player == player)) && bom.wakeUp()){
                return true;
            }
        }
        return false;
    }

    public boolean wakeUp(){
        if (bomState < 4){
            lastUpdateTime = -1;
            bomState = 4;
            return true;
        }else {
            return false;
        }
    }

    public void setBomBodyValue(int reallyY, int reallyX, int[][] bomBody, int value){
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

        bomBody[reallyX][reallyY] = value;
    }

    public boolean isOver() {
        return over;
    }

    public int getSize() {
        return size;
    }
}
