package priv.cdk.bomberman;

import priv.cdk.bomberman.charmander.Charmander;
import priv.cdk.bomberman.charmander.CharmanderThread;
import priv.cdk.bomberman.common.Common;
import priv.cdk.bomberman.game.Game;
import priv.cdk.bomberman.player.Player;
import priv.cdk.bomberman.room.Room;

/**
 * 界面程序
 */
public class UserInterface{
    public final Game game;
    private long lastPressProcessed = System.currentTimeMillis();//上一次点击的时间

    public UserInterface(Game game) {
        this.game = game;
    }

    public void keyPressed(int pNumber ,int keyCode) {
        Room room = game.room;
        Player player = room.ps.get(pNumber);

        if (!player.isMember()) {
            switch (keyCode) {
                case 38:
                case 87://W
                case 37:
                case 65://D
                case 40:
                case 83://S
                case 39:
                case 68://A
                case 74://J
                case 75://K
                case 72://H
                    break;
                default:
                    return;
            }
        }


        if(game.isGameOver()){//游戏结束，只能按H开始
            if(keyCode == 72){
                game.startGame();
            }
            return;
        }

        if(player.isDie()){//玩家死亡，不能操作
            return;
        }

        if(room.suspend.get() && !player.isMember()){//游戏暂停了
            if(keyCode != 72){//非按 H 开始 ，不继续运行
                return;
            }
        }

        if(System.currentTimeMillis() - lastPressProcessed >= player.getMoveInterval()) {//降低长按点击频率
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
                if (player.isTank()){
                    player.addMissile(player.getMoveY(), player.getMoveX(), player.getMotorDirection());
                }else {
                    player.addBom(player.getTy(), player.getLx());
                }
                break;
            case 75://K
                if(player.isBomControl()){
                    player.wakeUpTheBombAll();
                }
                break;
            case 76://L
                player.setTank(!player.isTank());
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
            case 112://F1
            case 113://F2
            case 114://F3
            case 115://F4
            case 116://F5
            case 117://F6
            case 118://F7
                room.addCritter(player.getLx(), player.getTy(), keyCode - 112);
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
            case 51://3
                room.setBodyCellValue(player.getTy(), player.getLx(), Common.PROP_QUESTION_MARK);
                break;
            case 52://4
                room.setBodyCellValue(player.getTy(), player.getLx(), Common.PROP_TANK);
                break;
            case 82://R
                room.addCharmander(player.getLx(), player.getTy());
        }
    }


















}
