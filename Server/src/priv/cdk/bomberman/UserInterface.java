package priv.cdk.bomberman;

import priv.cdk.bomberman.common.Common;
import priv.cdk.bomberman.game.Game;
import priv.cdk.bomberman.player.Player;
import priv.cdk.bomberman.room.Room;

/**
 * 界面程序
 */
public class UserInterface{

    public final Game game;
    private long lastPressProcessed = System.currentTimeMillis();

    public UserInterface(Game game) {
        this.game = game;
    }

    public void keyPressed(int pNumber ,int keyCode) {
        if(game.isGameOver()){
            if(keyCode == 72){
                game.startGame();
            }
            return;
        }

        Room room = game.room;
        Player player = room.ps.get(pNumber);

        if(player.isDie()){
            return;
        }

        if(room.suspend.get()){//游戏暂停了
            if(keyCode != 72){//非按 H 开始 ，不继续运行
                return;
            }
        }

        if(System.currentTimeMillis() - lastPressProcessed > player.getMoveInterval()) {
            //Do your work here...
            lastPressProcessed = System.currentTimeMillis();
        }else{
            return;
        }

        switch (keyCode){
            case 38:
            case 87://W
                //上
                player.move(0, - player.speed);
                break;
            case 37:
            case 65://D
                //右
                player.move( - player.speed, 0);
                break;
            case 40:
            case 83://S
                //下
                player.move(0, + player.speed);
                break;
            case 39:
            case 68://A
                //左
                player.move(+ player.speed, 0);
                break;
            case 74://J
                player.addBom(player.getTy(), player.getLx());
                break;
            case 75://K
                if(player.isBomControl()){
                    player.wakeUpTheBombAll();
                }
                break;
            case 77://M
                player.randomAddBobs();
                break;
            case 89://Y
                int i = room.getBodyCellValue(player.getTy(), player.getLx());
                if(i == 0){
                    room.setBodyCellValue(player.getTy(), player.getLx(), -1);
                }
                break;
            case 76://L
            case 80://P
                room.addCritter(player.getLx(), player.getTy(), keyCode == 76 ? 0 : 1);
                break;
            case 85://U
                room.setBodyCellValue(player.getTy(), player.getLx(), 3);
                break;
            case 73://I
                room.setBodyCellValue(player.getTy(), player.getLx(), 0);
                break;
            case 81://Q
            case 72://H
                room.suspendRoom();//房间暂停或开始
                break;
            case 71://G
                room.reloadRoom();//跳关
                break;
            case 49://1
                room.setBodyCellValue(player.getTy(), player.getLx(), Common.PROP_SPEED_ADD);
                break;
            case 50://2
                room.setBodyCellValue(player.getTy(), player.getLx(), Common.PROP_DOOR);
                break;
            case 51://2
                room.setBodyCellValue(player.getTy(), player.getLx(), Common.PROP_QUESTION_MARK);
                break;
        }
    }


















}
