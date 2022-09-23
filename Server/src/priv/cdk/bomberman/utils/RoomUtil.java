package priv.cdk.bomberman.utils;

import priv.cdk.bomberman.room.Room;

import java.util.Random;

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
                if(i < 3 && j < 3){
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
        for (int i=5; i < room.getH() - 1; i++){
            for (int j=5; j< room.getW() - 1; j++) {
                if(room.getBodyCellValue(i,j) == 0){
                    int randomNumber = random.nextInt(blank);
                    if(randomNumber < size){
                        room.addCritter(j,i,type);
                    }
                }
            }
        }
    }

    /**
     * 随机添加门
     * @param room 房间
     */
    public static void randomAddDoor(Room room){
        Random random = new Random();
        int blank = room.getBlank();
        for (int i=5; i < room.getH() - 1; i++){
            for (int j=5; j< room.getW() - 1; j++) {
                if (room.getBodyCellValue(i, j) == 0) {
                    int randomNumber = random.nextInt(blank);
                    if(randomNumber < 2){
                        if (room.addDoor(j, i)) {
                            return;
                        }
                    }
                }
            }
        }

        room.addDoor(room.getH() - 2, room.getH() - 2);
    }
}
