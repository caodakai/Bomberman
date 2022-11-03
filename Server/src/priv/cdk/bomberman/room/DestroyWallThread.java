package priv.cdk.bomberman.room;

import priv.cdk.bomberman.common.Common;
import priv.cdk.bomberman.parent.MyThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class DestroyWallThread extends MyThread {
    public static final long DESTROY_THE_WALL_TIME = 500;//摧毁墙的速度

    private final int y;
    private final int x;

    public DestroyWallThread(Room room, int y, int x) {
        super(room);
        this.y = y;
        this.x = x;
    }

    @Override
    public void myRun() {
        myRoom.setBodyCellValue(y, x, 2);
        try {
            mySleep(DESTROY_THE_WALL_TIME);

            myRoom.setBodyCellValue(y, x, 1);

            mySleep(DESTROY_THE_WALL_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myRoom.setBodyCellValue(y, x, 0);

        AtomicBoolean addBomControl = new AtomicBoolean(false);
        AtomicBoolean addBomThrough = new AtomicBoolean(false);
        AtomicBoolean addWallThrough = new AtomicBoolean(false);
        AtomicBoolean addQuestionMark = new AtomicBoolean(false);
        AtomicBoolean addFireImmune = new AtomicBoolean(false);
        AtomicBoolean addTank = new AtomicBoolean(false);

        myRoom.ps.forEach(player -> {
            if(!player.isDie()) {
                if (!player.isBomControl()) {
                    addBomControl.set(true);
                }
                if (!player.isBomThrough()) {
                    addBomThrough.set(true);
                }
                if (!player.isWallThrough()) {
                    addWallThrough.set(true);
                }
                if (!player.isQuestionMark()) {
                    addQuestionMark.set(true);
                }
                if (!player.isFireImmune()) {
                    addFireImmune.set(true);
                }
                if (!player.isTank()){
                    addTank.set(true);
                }
            }
        });

        List<Byte> bytes = new ArrayList<>();//随机数组

        addWhile(bytes, Math.max(0, myRoom.getCustomsPass()) * 6, (byte) 1);//添加速度道具

        addWhile(bytes, Math.max(0, myRoom.getCustomsPass()) * 6, (byte) 2);//添加炸弹道具

        if(addBomControl.get()){
            addWhile(bytes, Math.max(0, myRoom.getCustomsPass()) * 2, (byte) 3);//添加控制炸弹的道具
        }

        addWhile(bytes, Math.max(0, myRoom.getCustomsPass()) * 6, (byte) 4);//添加速度道具

        if(addBomThrough.get()) {
            addWhile(bytes, Math.max(0, myRoom.getCustomsPass() - 1) * 2, (byte) 5);//添加炸弹穿越道具
        }

        if(addWallThrough.get()) {
            addWhile(bytes, Math.max(0, myRoom.getCustomsPass() - 2) * 2, (byte) 6);//添加墙壁穿越道具
        }

        if(addQuestionMark.get()) {
            addWhile(bytes, Math.max(0, myRoom.getCustomsPass()), (byte) 7);//添加无敌道具
        }

        if(addFireImmune.get()) {
            addWhile(bytes, Math.max(0, myRoom.getCustomsPass() - 2) * 2, (byte) 8);//添加火焰免疫道具
        }

        if (addTank.get()){
            addWhile(bytes, Math.max(0, myRoom.getCustomsPass()) * 2, (byte) 9);//添加火焰免疫道具
        }

        addWhile(bytes, (bytes.size() * 2) / myRoom.getCustomsPass(), (byte) 0);//添加空的数据

        int prop = bytes.get(new Random().nextInt(bytes.size()));
        switch (prop) {
            case 1:
                myRoom.setBodyCellValue(y, x, Common.PROP_SCOPE_ADD);
                break;
            case 2:
                myRoom.setBodyCellValue(y, x, Common.PROP_BOM_ADD);
                break;
            case 3:
                myRoom.setBodyCellValue(y, x, Common.PROP_BOM_CONTROL);
                break;
            case 4:
                myRoom.setBodyCellValue(y, x, Common.PROP_SPEED_ADD);
                break;
            case 5:
                myRoom.setBodyCellValue(y, x, Common.PROP_BOM_THROUGH);
                break;
            case 6:
                myRoom.setBodyCellValue(y, x, Common.PROP_WALL_THROUGH);
                break;
            case 7:
                myRoom.setBodyCellValue(y, x, Common.PROP_QUESTION_MARK);
                break;
            case 8:
                myRoom.setBodyCellValue(y, x, Common.PROP_FIRE_IMMUNE);
                break;
            case 9:
                myRoom.setBodyCellValue(y, x, Common.PROP_TANK);
                break;
        }
    }


    private void addWhile(List<Byte> bytes, int number, byte b){
        while (number-- > 0){
            bytes.add(b);
        }
    }
}
