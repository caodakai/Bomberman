package priv.ui;

import priv.Start;
import priv.cdk.bomberman.data.InputData;
import priv.common.Common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 界面程序
 */
public class UserInterface extends JPanel implements KeyListener {
    private static final int showSize = 800;//查看的区域大小

    private int showStartX = 0;
    private int showStartY = 0;

    private final List<Socket> sockets = new ArrayList<>();

    public InputData inputData;

    public UserInterface(Socket socket){
        sockets.add(socket);
    }

    public UserInterface() {}

    public void addSocket(Socket socket){
        this.sockets.add(socket);
    }

    //计算可视区域
    public void calculateShow(){
        int pNumber = inputData.getpNumber();
        InputData.Player thisPlayer = inputData.getPlayers()[pNumber];
        showStartX = Math.max(0, thisPlayer.getActualX() - showSize/2 - Common.interfaceStartX);
        showStartY = Math.max(0, thisPlayer.getActualY() - showSize/2 - Common.interfaceStartY);

        showStartX = Math.min(showStartX, Math.max(0, inputData.wSize  - showSize + Common.interfaceStartX));
        showStartY = Math.min(showStartY, Math.max(0, inputData.hSize - showSize + Common.interfaceStartY));
    }


    public void paint(Graphics g) {
        //覆盖父类的方法
        super.paint(g);

        if(inputData == null){
            return;
        }

        calculateShow();

        for (int i = 0; i < inputData.H; i++) {
            for (int j = 0; j < inputData.W; j++) {
                int dx1 = j * inputData.CELL_WIDTH + Common.interfaceStartX - showStartX;
                int dy1 = i * inputData.CELL_HEIGHT + Common.interfaceStartY  - showStartY;
                if(cannotDraw(dx1, dy1)){
                    continue;
                }

                Image img;
                int dx2 = dx1 + inputData.CELL_WIDTH, dy2 = dy1 + inputData.CELL_HEIGHT;
                int sx1,sy1,sx2,sy2;
                switch (inputData.getBody(i,j)){
//                switch (room.futureBody[i][j]){
                    case Common.PROP_SCOPE_ADD:
                        img = Common.prop;
                        sx1 = 0; sy1 = 0;
                        break;
                    case Common.PROP_BOM_ADD:
                        img = Common.prop;
                        sx1 = 0; sy1 = 40;
                        break;
                    case Common.PROP_BOM_CONTROL:
                        img = Common.prop;
                        sx1 = 0; sy1 = 80;
                        break;
                    case Common.PROP_SPEED_ADD:
                        img = Common.prop;
                        sx1 = 0; sy1 = 120;
                        break;
                    case Common.PROP_BOM_THROUGH:
                        img = Common.prop;
                        sx1 = 0; sy1 = 160;
                        break;
                    case Common.PROP_WALL_THROUGH:
                        img = Common.prop;
                        sx1 = 0; sy1 = 200;
                        break;
                    case Common.PROP_QUESTION_MARK:
                        img = Common.prop;
                        sx1 = 0; sy1 = 240;
                        break;
                    case Common.PROP_FIRE_IMMUNE:
                        img = Common.prop;
                        sx1 = 0; sy1 = 280;
                        break;
                    case Common.PROP_DOOR:
                        img = Common.door;
                        sx1 = 0; sy1 = 0;
                        break;
                    case 1030:
                        img = Common.fire3;
                        sx1 = 120; sy1 = 80;
                        break;
                    case 1029:
                        img = Common.fire2;
                        sx1 = 120; sy1 = 80;
                        break;
                    case 1028:
                        img = Common.fire1;
                        sx1 = 120; sy1 = 80;
                        break;
                    case 1027:
                        img = Common.fire3;
                        sx1 = 160; sy1 = 80;
                        break;
                    case 1026:
                        img = Common.fire2;
                        sx1 = 160; sy1 = 80;
                        break;
                    case Common.FUTURE_BODY_FIRE_NUMBER_RIGHT:
                    case 1025:
                        img = Common.fire1;
                        sx1 = 160; sy1 = 80;
                        break;
                    case 1024:
                        img = Common.fire3;
                        sx1 = 40; sy1 = 80;
                        break;
                    case 1023:
                        img = Common.fire2;
                        sx1 = 40; sy1 = 80;
                        break;
                    case 1022:
                        img = Common.fire1;
                        sx1 = 40; sy1 = 80;
                        break;
                    case 1021:
                        img = Common.fire3;
                        sx1 = 0; sy1 = 80;
                        break;
                    case 1020:
                        img = Common.fire2;
                        sx1 = 0; sy1 = 80;
                        break;
                    case Common.FUTURE_BODY_FIRE_NUMBER_LEFT:
                    case 1019:
                        img = Common.fire1;
                        sx1 = 0; sy1 = 80;
                        break;
                    case 1018:
                        img = Common.fire3;
                        sx1 = 80; sy1 = 120;
                        break;
                    case 1017:
                        img = Common.fire2;
                        sx1 = 80; sy1 = 120;
                        break;
                    case 1016:
                        img = Common.fire1;
                        sx1 = 80; sy1 = 120;
                        break;
                    case 1015:
                        img = Common.fire3;
                        sx1 = 80; sy1 = 160;
                        break;
                    case 1014:
                        img = Common.fire2;
                        sx1 = 80; sy1 = 160;
                        break;
                    case Common.FUTURE_BODY_FIRE_NUMBER_BOTTOM:
                    case 1013:
                        img = Common.fire1;
                        sx1 = 80; sy1 = 160;
                        break;
                    case 1012:
                        img = Common.fire3;
                        sx1 = 80; sy1 = 40;
                        break;
                    case 1011:
                        img = Common.fire2;
                        sx1 = 80; sy1 = 40;
                        break;
                    case 1010:
                        img = Common.fire1;
                        sx1 = 80; sy1 = 40;
                        break;
                    case 1009:
                        img = Common.fire3;
                        sx1 = 80; sy1 = 0;
                        break;
                    case 1008:
                        img = Common.fire2;
                        sx1 = 80; sy1 = 0;
                        break;
                    case Common.FUTURE_BODY_FIRE_NUMBER_TOP:
                    case 1007:
                        img = Common.fire1;
                        sx1 = 80; sy1 = 0;
                        break;
                    case 1006:
                        img = Common.fire3;
                        sx1 = 80; sy1 = 80;
                        break;
                    case 1005:
                        img = Common.fire2;
                        sx1 = 80; sy1 = 80;
                        break;
                    case Common.FUTURE_BODY_FIRE_NUMBER_CENTRE:
                    case 1004:
                        img = Common.fire1;
                        sx1 = 80; sy1 = 80;
                        break;
                    case 1003:
                        img = Common.third_stage_bom;
                        sx1 = 0; sy1 = 0;
                        break;
                    case 1002:
                        img = Common.second_stage_bom;
                        sx1 = 0; sy1 = 0;
                        break;
                    case 1001:
                        img = Common.first_stage_bom;
                        sx1 = 0; sy1 = 0;
                        break;
                    case 3:
                        img = Common.first_stage_wall;
                        sx1 = 0; sy1 = 0;
                        break;
                    case 2:
                        img = Common.second_stage_wall;
                        sx1 = 0; sy1 = 0;
                        break;
                    case 1:
                        img =Common.third_stage_wall;
                        sx1 = 0; sy1 = 0;
                        break;
                    case -1:
                        img = Common.not_destroy_wall;
                        sx1 = 0; sy1 = 0;
                        break;
                    default:
                        img = null;
                        sx1 = 0; sy1 = 0;
                }
                if(img != null) {
                    sx2 = sx1 + 40; sy2 = sy1 + 40;
                    g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,null);
//                    g.setColor(Color.black);
//                    g.drawString(room.body[i][j] + "", dx1 + 10, dy1 + 10);
                }
            }
        }

        InputData.Player[] ps = inputData.getPlayers();

        for (InputData.Player player : ps) {
            drawBiology(g, player, player.isQuestionMark() ? Common.player_question_mark : Common.player);
        }

        InputData.Critter[] critters = inputData.getCritters();

        for (InputData.Critter critter : critters) {
            if(critter != null){
                Image image;

                switch (critter.getName()){
                    case "Elite":
                        image = Common.eliteCritter;
                        break;
                    case "Knight_1":
                        image = Common.knightCritter1;
                        break;
                    case "Knight_2":
                        image = Common.knightCritter2;
                        break;
                    case "Dragon_blue":
                        image = Common.dragonCritter1;
                        break;
                    case "Dragon_golden":
                        image = Common.dragonCritter2;
                        break;
                    default:
                        image = Common.basicsCritter;
                }

                drawBiology(g, critter, image);
            }
        }

        InputData.Charmander[] charmanders = inputData.getCharmanders();

        for (InputData.Charmander charmander : charmanders) {
            if(charmander != null){
                drawBiology(g, charmander, Common.charmander);
            }
        }

        if(inputData.isGameOver()){
            g.drawImage(Common.gameOver, Common.interfaceStartX - 1, Common.interfaceStartY - 1,Common.interfaceStartX + showSize + 2,Common.interfaceStartY + showSize + 2,
                    0, 0, 802, 802, null);
        }

        drawProperty(g, ps);

        drawThumbnail(g, critters, ps, charmanders);
    }

    /**
     * 绘制玩家
     */
    public void drawBiology(Graphics g, InputData.BasicAttribute basicAttribute, Image image){
        int dx1 = basicAttribute.getActualX() - showStartX;
        int dy1 = basicAttribute.getActualY() - showStartY;

        if(cannotDraw(dx1, dy1)){
            return;
        }

        int dx2 = dx1 + inputData.CELL_WIDTH, dy2 = dy1 + inputData.CELL_HEIGHT;
        int sx1,sy1,sx2,sy2;
        switch (basicAttribute.getState()){
            case 1:
                sx1 = 80;sy1 = 200;
                break;
            case 2:
                sx1 = 40;sy1 = 200;
                break;
            case 3:
                sx1 = 0;sy1 = 200;
                break;
            case 4:
                sx1 = 80;sy1 = 0;
                break;
            case 5:
                sx1 = 80;sy1 = 40;
                break;
            case 6:
                sx1 = 80;sy1 = 120;
                break;
            case 7:
                sx1 = 80;sy1 = 160;
                break;
            case 8:
                sx1 = 0;sy1 = 80;
                break;
            case 9:
                sx1 = 40;sy1 = 80;
                break;
            case 10:
                sx1 = 120;sy1 = 80;
                break;
            case 11:
                sx1 = 160;sy1 = 80;
                break;
            default:
                sx1 = 0;sy1 = 0;

        }

        if (image != null) {
            sx2 = sx1 + 40;
            sy2 = sy1 + 40;
            g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
        }
    }



    /**
     * 绘制右侧属性面板
     */
    private void drawProperty(Graphics g, InputData.Player[] ps){
        int x = Common.interfaceStartX + showSize + 2 + inputData.CELL_WIDTH * 2;

        g.setColor(Color.black);
        g.drawRect(x, Common.interfaceStartY, 100, inputData.hSize);//框

        final int[] y = {Common.interfaceStartY};
        int spaceBetween = 15;//间距
        g.setColor(Color.red);
        g.drawString("分数： " + inputData.getScore() , x + 5, y[0] += spaceBetween);
        g.drawString("剩余小怪： " + inputData.getSurplusCrittersSize() , x + 5, y[0] += spaceBetween);
        g.drawString("关卡： " + inputData.getCustomsPass() , x + 5, y[0] += spaceBetween);

        for (InputData.Player player : ps) {
            g.setColor(Color.black);
            g.drawLine(x, y[0] +=spaceBetween, x + 100, y[0]);

            g.setColor(player.isDie() ? Color.red : Color.blue);
            g.drawString("玩家名：" + player.getName() + (player.isDie() ? " - 死亡" : ""), x + 5, y[0] += spaceBetween);
            g.setColor(Color.blue);
            g.drawString("炸弹数量： " + player.getBomNumber() , x + 5, y[0] += spaceBetween);
            g.drawString("炸弹范围： " + player.getBomSize() , x + 5, y[0] += spaceBetween);
            g.drawString("无敌时间： " + player.getQuestionMarkTime() , x + 5, y[0] += spaceBetween);
        }
    }

    //判断绘制是否超出可视范围
    public boolean cannotDraw(int dx1, int dy1){
        return dx1 < Common.interfaceStartX || dx1 > showSize - inputData.CELL_WIDTH || dy1 < Common.interfaceStartY || dy1 > showSize - inputData.CELL_HEIGHT;
    }

    /**
     * 绘制缩略图
     */
    public void drawThumbnail(Graphics g, InputData.Critter[] critters, InputData.Player[] ps, InputData.Charmander[] charmanders){
        int startX = Common.interfaceStartX + showSize + 300;
        int startY = Common.interfaceStartY;
        int size = 4;

        g.setColor(Color.black);
        g.drawRect(startX, startY, inputData.W * size, inputData.H * size);//框

        g.setColor(Color.red);
        for (InputData.Critter critter : critters) {
            g.fillRect(startX + critter.getLx() * size, startY + critter.getTy() * size, size, size);
        }

        g.setColor(Color.blue);
        for (InputData.Player player : ps) {
            g.fillRect(startX + player.getLx() * size, startY + player.getTy() * size, size, size);
        }

        g.setColor(Color.green);
        for (InputData.Charmander charmander : charmanders) {
            g.fillRect(startX + charmander.getLx() * size, startY + charmander.getTy() * size, size, size);
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {
//        System.out.println("按下了能输入内容的按键 : " + e.getKeyCode());
    }

    @Override
    public void keyPressed(KeyEvent e) {
//        System.out.println("按下了能输入内容的按键 : " + e.getKeyCode());

        sockets.forEach(socket -> {
            Start.outString(socket, e.getKeyCode() + "");
        });

        sockets.forEach(socket -> {
            try {
                String s = Start.inString(socket);
            }catch (StringIndexOutOfBoundsException | UnsupportedEncodingException exception){
                System.out.println("服务器关闭！");
                Start.stop = true;
            }
        });

        /*switch (e.getKeyCode()){
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

                sockets.forEach(socket -> {
                    Start.outString(socket, e.getKeyCode() + "");
                });

                sockets.forEach(socket -> {
                    try {
                        String s = Start.inString(socket);
                    }catch (StringIndexOutOfBoundsException | UnsupportedEncodingException exception){
                        System.out.println("服务器关闭！");
                        Start.stop = true;
                    }
                });
            default:
                break;
        }*/
    }

    /**
     * //按键抬起
     */
    @Override
    public void keyReleased(KeyEvent e) {

    }


















}
