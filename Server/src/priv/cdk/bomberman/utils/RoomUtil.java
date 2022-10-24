package priv.cdk.bomberman.utils;

import priv.cdk.bomberman.critter.Critter;
import priv.cdk.bomberman.parent.Biota;
import priv.cdk.bomberman.parent.Movement;
import priv.cdk.bomberman.room.Room;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class RoomUtil {
    /**
     * 在body中随机的 空 位置生成指定数字
     * @param bodyNumber 需要添加的数字
     * @param size 可能需要添加多少个 ，这是一个随机数
     */
    public static int randomNumberToBody(Room room, int bodyNumber, int size){
        int number = 0;
        Random random = new Random();
        int blank = room.getBlank();
        for (int i=1; i < room.getH() - 1; i++){
            for (int j=1; j< room.getW() - 1; j++){
                if(i < 4 && j < 4){
                    continue;
                }

                if(room.getBodyCellValue(i,j) == 0){
                    int randomNumber = random.nextInt(blank);
                    if(randomNumber < size){
                        room.setBodyCellValue(i, j, bodyNumber);
                        number ++;
                    }
                }
            }
        }
        return number;
    }

    /**
     * 随机添加小怪
     */
    public static void randomCritterToBody(Room room, int type, int size){
        Random random = new Random();
        int blank = room.getBlank();
        int addSize = 0;
        int time = 2;
        while (addSize < size && time > 0) {
            for (int i = 5; i < room.getH() - 1; i++) {
                for (int j = 5; j < room.getW() - 1; j++) {
                    if (room.getBodyCellValue(i, j) == 0) {
                        int randomNumber = random.nextInt(blank);
                        if (randomNumber < size) {
                            room.addCritter(j, i, type);
                            addSize ++;

                            if (addSize >= size){
                                return;
                            }
                        }
                    }
                }
            }

            time -- ;
        }
    }

    /**
     * 随机添加门
     * @param room 房间
     */
    public static void randomAddDoor(Room room){
        Random random = new Random();
        int blank = room.getBlank();

        while (true) {
            for (int i = 5; i < room.getH() - 1; i++) {
                for (int j = 5; j < room.getW() - 1; j++) {
                    if (room.getBodyCellValue(i, j) == 0) {
                        int randomNumber = random.nextInt(blank);
                        if (randomNumber < 2) {
                            if (room.addDoor(j, i)) {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 判断当前位置的对象死亡
     * @param biota 需要死亡的对象
     * @param riskX 危险坐标x
     * @param riskY 危险坐标y
     */
    public static boolean toDetermineDeath(Biota biota, int riskX, int riskY){
        if (!biota.isDie()) {
            int x = biota.getActualX() - riskX;
            int y = biota.getActualY() - riskY;

            boolean b = (y >= 0 && y < Room.CELL_HEIGHT) || (y <= 0 && y > -Room.CELL_HEIGHT);
            if ((x >= 0 && x < Room.CELL_WIDTH && b) || (x <= 0 && x > -Room.CELL_WIDTH && b)) {
                biota.die();
                return true;
            }
        }
        return false;
    }
}
