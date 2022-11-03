package priv.cdk.bomberman.bom;

import priv.cdk.bomberman.parent.MyThread;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;

/**
 * 控制所有炸弹对象
 */
public class BomThread extends MyThread {
    public BomThread(Room room) {
        super(room);
    }

    @Override
    public void myRun() {
        while (!myRoom.close.get()){
            int[][] bomBody = new int[myRoom.getH()][myRoom.getW()];
            boolean hasBom = false;
            for (Bom[] bob : myRoom.getBobs()) {
                for (int i = 0; i < bob.length; i++) {
                    Bom bom = bob[i];
                    if (bom != null){
                        hasBom = true;
                        if (bom.isOver()){
                            bob[i] = null;
                        }else {
                            bom.update(bomBody);
                        }
                    }
                }
            }
            if (hasBom) {
                for (int i = 0; i < bomBody.length; i++) {
                    for (int j = 0; j < bomBody[i].length; j++) {
                        if (IsUtil.isFire(myRoom.getBodyCellValue(j, i))) {
                            myRoom.setBodyCellValue(j, i, bomBody[i][j]);
                        } else {
                            if (bomBody[i][j] != 0) {
                                myRoom.setBodyCellValue(j, i, bomBody[i][j]);
                            }
                        }
                    }
                }
            }

            try {
                mySleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
