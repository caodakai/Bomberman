package priv.cdk.bomberman;

import priv.cdk.bomberman.parent.MyThread;
import priv.cdk.bomberman.player.Player;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class Bom extends MyThread {
    public static final long BOM_TIME = 1000;//爆炸的速度
    public static final long FIRE_SPREAD = 300;//火焰展开的速度
    public static final long FIRE_SHRINK = 300;//火焰收缩的速度

    public final int x;
    public final int y;
    private int bomState = 1;
    private final int size;

    private final AtomicBoolean direction = new AtomicBoolean(false);//是否爆炸 true为已经爆炸了
    public int sourceDirection = 0;//爆炸来源方向，当其它炸弹爆炸时，会影响当前炸弹，0:自然爆炸，1:上,2:下,3:左,4:右

    private final Player player;
    private final Room room;

    public Bom(int x, int y, Player player){
        super(player.room);

        this.x = x;
        this.y = y;
        this.player = player;
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
                    if(room.getBodyCellValue(reallyY, reallyX) < 0){
                        top = false;
                    }else {
                        if (room.destroyTheWallAndPlayer(reallyY, reallyX)) {
                            top = false;
                            if (i == 0) {
                                bottom = false;
                                left = false;
                                right = false;
                            }
                        }else {
                            if (i != 0 && wakeUpTheBomb(room, reallyY, reallyX, 2)) {
                                top = false;
                            } else {
                                fireFilter(reallyY, reallyX);
                                if (i == 0) {
                                    room.setBodyCellValue(reallyY, reallyX, 1000 + bomState);//1004~1006
                                } else if (i == size - 1) {
                                    room.setBodyCellValue(reallyY, reallyX, 1003 + bomState);//1007~1009
                                } else {
                                    room.setBodyCellValue(reallyY, reallyX, 1006 + bomState);//1010~1012
                                }
                                tj = reallyY;
                            }
                        }
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

                    if(room.getBodyCellValue(reallyY, reallyX) < 0){
                        bottom = false;
                    }else {
                        if (room.destroyTheWallAndPlayer(reallyY, reallyX)) {
                            bottom = false;
                        }else {
                            if (i != 0 && wakeUpTheBomb(room, reallyY, reallyX, 1)) {
                                bottom = false;
                            } else {
                                fireFilter(reallyY, reallyX);
                                if (i == 0) {
                                    room.setBodyCellValue(reallyY, reallyX, 1000 + bomState);//1004~1006
                                } else if (i == size - 1) {
                                    room.setBodyCellValue(reallyY, reallyX, 1009 + bomState);//1013~1015
                                } else {
                                    room.setBodyCellValue(reallyY, reallyX, 1012 + bomState);//1016~1018
                                }
                                bj = reallyY;
                            }
                        }
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

                    if(room.getBodyCellValue(reallyY, reallyX) < 0){
                        left = false;
                    }else {
                        if (room.destroyTheWallAndPlayer(reallyY, reallyX)) {
                            left = false;
                        }else {
                            if (i != 0 && wakeUpTheBomb(room, reallyY, reallyX, 4)) {
                                left = false;
                            } else {
                                fireFilter(reallyY, reallyX);
                                if (i == 0) {
                                    room.setBodyCellValue(reallyY, reallyX, 1000 + bomState);//1004~1006
                                } else if (i == size - 1) {
                                    room.setBodyCellValue(reallyY, reallyX, 1015 + bomState);//1019~1021
                                } else {
                                    room.setBodyCellValue(reallyY, reallyX, 1018 + bomState);///1022~1024
                                }
                                li = reallyX;
                            }
                        }
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

                    if(room.getBodyCellValue(reallyY, reallyX) < 0){
                        right = false;
                    }else {
                        if (room.destroyTheWallAndPlayer(reallyY, reallyX)) {
                            right = false;
                        }else {
                            if (i != 0 && wakeUpTheBomb(room, reallyY, reallyX, 3)) {
                                right = false;
                            } else {
                                fireFilter(reallyY, reallyX);
                                if (i == 0) {
                                    room.setBodyCellValue(reallyY, reallyX, 1000 + bomState);//1004~1006
                                } else if (i == size - 1) {
                                    room.setBodyCellValue(reallyY, reallyX, 1021 + bomState);//1025~1027
                                } else {
                                    room.setBodyCellValue(reallyY, reallyX, 1024 + bomState);//1028~1030
                                }
                                ri = reallyX;
                            }
                        }
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

            bomState ++;

            try {
                mySleep(FIRE_SPREAD);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
        }

        int bomState2Index = 6;
        while(bomState2 <= bomState2Index) {
            int tj2 = tj, bj2 = bj, li2 = li, ri2= ri;

            while (tj2 <= y || bj2 >= y || li2 <= x || ri2 >= x) {
                if (tj2 <= y) {
                    fireFilter(tj2, x);
                    if(tj2 == y){
                        room.setBodyCellValue(tj2, x, 1000 + bomState2Index);//1004~1006
                    } else if(y - tj2 == size - 1){
                        room.setBodyCellValue(tj2, x, 1003 + bomState2Index);//1007~1009
                    }else {
                        room.setBodyCellValue(tj2, x, 1006 + bomState2Index);//1010~1012
                    }
                    tj2++;
                }
                if (bj2 >= y) {
                    fireFilter(bj2, x);
                    if(bj2 == y){
                        room.setBodyCellValue(bj2, x, 1000 + bomState2Index);//1004~1006
                    } else if(bj2 - y == size - 1){
                        room.setBodyCellValue(bj2, x, 1009 + bomState2Index);//1013~1015
                    }else {
                        room.setBodyCellValue(bj2, x, 1012 + bomState2Index);;//1016~1018
                    }
                    bj2--;
                }
                if (li2 <= x) {
                    fireFilter(y , li2);
                    if(li2 == x){
                        room.setBodyCellValue(y, li2, 1000 + bomState2Index);//1004~1006
                    } else if(x - li2 == size - 1){
                        room.setBodyCellValue(y, li2, 1015 + bomState2Index);//1019~1021
                    }else {
                        room.setBodyCellValue(y, li2, 1018 + bomState2Index);//1022~1024
                    }
                    li2++;
                }
                if (ri2 >= x) {
                    fireFilter(y , ri2);
                    if(ri2 == x){
                        room.setBodyCellValue(y, ri2, 1000 + bomState2Index);//1004~1006
                    } else if(ri2 - x == size - 1){
                        room.setBodyCellValue(y, ri2, 1021 + bomState2Index);//1025~1027
                    }else {
                        room.setBodyCellValue(y, ri2, 1024 + bomState2Index);//1028~1030
                    }
                    ri2--;
                }
            }

            bomState2Index -- ;

            try {
                mySleep(FIRE_SHRINK);
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
        player.bomNumberAdd();
    }

    /**
     * 清除火 消除火的时候，仅仅消除墙
     */
    private void removeFire(int y, int x){
        if (!room.destroyTheWall(y,x)) {
            room.setBodyCellValue(y, x,0);
        }
    }

    /**
     * 唤醒炸弹，如果坐标是其它炸弹，那么唤醒该炸弹
     */
    public static boolean wakeUpTheBomb(Room room, int y, int x, int sourceDirection){
        if(IsUtil.isBom(room.getBodyCellValue(y, x))){
            Bom bom = room.getBomCellValue(x, y);
            if (bom != null && bom.direction.compareAndSet(false, true)) {
                bom.sourceDirection = sourceDirection;
                bom.interrupt();
                return true;
            }
        }
        return false;
    }

    /**
     * 火焰监听 ， 清除小怪
     */
    public void fireFilter(int y, int x){
        room.critters.forEach(critter ->{
            if(critter.getLx() == x){
                if(critter.getTy() == y || critter.getBy() == y){
                    critter.die();
                }
            }else if(critter.getRx() == x){
                if(critter.getTy() == y || critter.getBy() == y){
                    critter.die();
                }
            }
        });
    }

    public int getSize() {
        return size;
    }

    public Player getPlayer() {
        return player;
    }
}
