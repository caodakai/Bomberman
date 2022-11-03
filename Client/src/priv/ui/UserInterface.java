package priv.ui;

import priv.Start;
import priv.cdk.bomberman.data.InputData;
import priv.cdk.bomberman.wayfinding.Point;
import priv.cdk.bomberman.wayfinding.PointHelper;
import priv.cdk.bomberman.wayfinding.PointInterface;
import priv.common.Common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 界面程序
 */
public class UserInterface extends JPanel implements KeyListener, FocusListener {
    private static final int showSize = 800;//查看的区域大小
    private static final boolean drawPath = true;//绘制路线，即自动寻路

    private int showStartX = 0;
    private int showStartY = 0;

    private final List<Socket> sockets = new ArrayList<>();

    public InputData inputData;

    private static final int fontSize = 12;
    private static final Font font = new Font(null, Font.BOLD, fontSize);

    public int[][] movementRoutes;

    private final PointHelper<InputData.Critter> pointHelper = new PointHelper<>(new PointInterface<InputData.Critter>() {
        @Override
        public InputData.Critter hasT(int y, int x) {
            boolean hasDragon = false;
            label:
            for (InputData.Critter critter : inputData.getCritters()) {
                if (critter != null && !critter.isDie()) {
                    switch (critter.getName()) {
                        case "Dragon_blue":
                        case "Dragon_golden":
                            hasDragon = true;
                            break label;
                        default:
                            break;
                    }
                }
            }

            boolean hasCritter = false;
            for (InputData.Critter critter : inputData.getCritters()) {
                if (critter != null && !critter.isDie()) {
                    hasCritter = true;
                    if (critter.getLx() == x && critter.getTy() == y) {
                        switch (critter.getName()) {//如果找到骑士，但是存在龙，那么不返回骑士，找到龙再返回龙
                            case "Knight_1":
                            case "Knight_2":
                                if (hasDragon) {
                                    break;
                                } else {
                                    return critter;
                                }
                            default:
                                return critter;
                        }
                    }
                }
            }

            //如果没有找到任何怪，那么虚构一个怪在门的地方，并返回
            if (!hasCritter) {
                if (inputData.getBody(y, x) == Common.PROP_DOOR) {
                    return new InputData.Critter();
                }
            }

            return null;
        }

        @Override
        public boolean canMove(int number) {
            return number != -1;
        }
    });

    public static boolean member = false;

    public UserInterface(Socket socket) {
        this.sockets.add(socket);
    }

    public UserInterface() {
    }

    public void addSocket(Socket socket) {
        this.sockets.add(socket);
    }

    //计算可视区域
    public void calculateShow() {
        InputData.Player thisPlayer = showPlayer(inputData.getPlayers(), inputData.getpNumber(), 0);

        showStartX = Math.max(0, thisPlayer.getActualX() - showSize / 2 - Common.interfaceStartX);
        showStartY = Math.max(0, thisPlayer.getActualY() - showSize / 2 - Common.interfaceStartY);

        showStartX = Math.min(showStartX, Math.max(0, inputData.wSize - showSize + Common.interfaceStartX));
        showStartY = Math.min(showStartY, Math.max(0, inputData.hSize - showSize + Common.interfaceStartY));
    }

    /**
     * 获取玩家视野区域 如果玩家死亡，那么获取下一个玩家的视野，如果玩家均死亡，那么获取当前视野
     *
     * @param players 玩家集合
     * @param pNumber 玩家下标
     * @param number  计数
     */
    public InputData.Player showPlayer(InputData.Player[] players, int pNumber, int number) {
        if (pNumber >= players.length) {
            pNumber = 0;
        }
        InputData.Player thisPlayer = players[pNumber];
        if (thisPlayer.isDie() && number < players.length) {
            return showPlayer(players, pNumber + 1, number + 1);
        } else {
            return thisPlayer;
        }
    }


    public void paint(Graphics g) {
        //覆盖父类的方法
        super.paint(g);

        if (inputData == null) {
            return;
        }

        calculateShow();

        g.setFont(font);//设置字体

        int[][] body = inputData.getBody();

        for (int i = 0; i < inputData.H; i++) {
            for (int j = 0; j < inputData.W; j++) {
                int dx1 = j * inputData.CELL_WIDTH + Common.interfaceStartX - showStartX;
                int dy1 = i * inputData.CELL_HEIGHT + Common.interfaceStartY - showStartY;
                if (cannotDraw(dx1, dy1)) {
                    continue;
                }

                Image img;
                int dx2 = dx1 + inputData.CELL_WIDTH, dy2 = dy1 + inputData.CELL_HEIGHT;
                int sx1, sy1, sx2, sy2;
                switch (body[i][j]) {
//                switch (room.futureBody[i][j]){
                    case Common.PROP_SCOPE_ADD:
                        img = Common.prop;
                        sx1 = 0;
                        sy1 = 0;
                        break;
                    case Common.PROP_BOM_ADD:
                        img = Common.prop;
                        sx1 = 0;
                        sy1 = 40;
                        break;
                    case Common.PROP_BOM_CONTROL:
                        img = Common.prop;
                        sx1 = 0;
                        sy1 = 80;
                        break;
                    case Common.PROP_SPEED_ADD:
                        img = Common.prop;
                        sx1 = 0;
                        sy1 = 120;
                        break;
                    case Common.PROP_BOM_THROUGH:
                        img = Common.prop;
                        sx1 = 0;
                        sy1 = 160;
                        break;
                    case Common.PROP_WALL_THROUGH:
                        img = Common.prop;
                        sx1 = 0;
                        sy1 = 200;
                        break;
                    case Common.PROP_QUESTION_MARK:
                        img = Common.prop;
                        sx1 = 0;
                        sy1 = 240;
                        break;
                    case Common.PROP_FIRE_IMMUNE:
                        img = Common.prop;
                        sx1 = 0;
                        sy1 = 280;
                        break;
                    case Common.PROP_TANK:
                        img = Common.prop;
                        sx1 = 0;
                        sy1 = 320;
                        break;
                    case Common.PROP_DOOR:
                        img = Common.door;
                        sx1 = 0;
                        sy1 = 0;
                        break;
                    case 1030:
                        img = Common.fire3;
                        sx1 = 120;
                        sy1 = 80;
                        break;
                    case 1029:
                        img = Common.fire2;
                        sx1 = 120;
                        sy1 = 80;
                        break;
                    case 1028:
                        img = Common.fire1;
                        sx1 = 120;
                        sy1 = 80;
                        break;
                    case 1027:
                        img = Common.fire3;
                        sx1 = 160;
                        sy1 = 80;
                        break;
                    case 1026:
                        img = Common.fire2;
                        sx1 = 160;
                        sy1 = 80;
                        break;
                    case Common.FUTURE_BODY_FIRE_NUMBER_RIGHT:
                    case 1025:
                        img = Common.fire1;
                        sx1 = 160;
                        sy1 = 80;
                        break;
                    case 1024:
                        img = Common.fire3;
                        sx1 = 40;
                        sy1 = 80;
                        break;
                    case 1023:
                        img = Common.fire2;
                        sx1 = 40;
                        sy1 = 80;
                        break;
                    case 1022:
                        img = Common.fire1;
                        sx1 = 40;
                        sy1 = 80;
                        break;
                    case 1021:
                        img = Common.fire3;
                        sx1 = 0;
                        sy1 = 80;
                        break;
                    case 1020:
                        img = Common.fire2;
                        sx1 = 0;
                        sy1 = 80;
                        break;
                    case Common.FUTURE_BODY_FIRE_NUMBER_LEFT:
                    case 1019:
                        img = Common.fire1;
                        sx1 = 0;
                        sy1 = 80;
                        break;
                    case 1018:
                        img = Common.fire3;
                        sx1 = 80;
                        sy1 = 120;
                        break;
                    case 1017:
                        img = Common.fire2;
                        sx1 = 80;
                        sy1 = 120;
                        break;
                    case 1016:
                        img = Common.fire1;
                        sx1 = 80;
                        sy1 = 120;
                        break;
                    case 1015:
                        img = Common.fire3;
                        sx1 = 80;
                        sy1 = 160;
                        break;
                    case 1014:
                        img = Common.fire2;
                        sx1 = 80;
                        sy1 = 160;
                        break;
                    case Common.FUTURE_BODY_FIRE_NUMBER_BOTTOM:
                    case 1013:
                        img = Common.fire1;
                        sx1 = 80;
                        sy1 = 160;
                        break;
                    case 1012:
                        img = Common.fire3;
                        sx1 = 80;
                        sy1 = 40;
                        break;
                    case 1011:
                        img = Common.fire2;
                        sx1 = 80;
                        sy1 = 40;
                        break;
                    case 1010:
                        img = Common.fire1;
                        sx1 = 80;
                        sy1 = 40;
                        break;
                    case 1009:
                        img = Common.fire3;
                        sx1 = 80;
                        sy1 = 0;
                        break;
                    case 1008:
                        img = Common.fire2;
                        sx1 = 80;
                        sy1 = 0;
                        break;
                    case Common.FUTURE_BODY_FIRE_NUMBER_TOP:
                    case 1007:
                        img = Common.fire1;
                        sx1 = 80;
                        sy1 = 0;
                        break;
                    case 1006:
                        img = Common.fire3;
                        sx1 = 80;
                        sy1 = 80;
                        break;
                    case 1005:
                        img = Common.fire2;
                        sx1 = 80;
                        sy1 = 80;
                        break;
                    case Common.FUTURE_BODY_FIRE_NUMBER_CENTRE:
                    case 1004:
                        img = Common.fire1;
                        sx1 = 80;
                        sy1 = 80;
                        break;
                    case 1003:
                        img = Common.third_stage_bom;
                        sx1 = 0;
                        sy1 = 0;
                        break;
                    case 1002:
                        img = Common.second_stage_bom;
                        sx1 = 0;
                        sy1 = 0;
                        break;
                    case 1001:
                        img = Common.first_stage_bom;
                        sx1 = 0;
                        sy1 = 0;
                        break;
                    case 3:
                        img = Common.first_stage_wall;
                        sx1 = 0;
                        sy1 = 0;
                        break;
                    case 2:
                        img = Common.second_stage_wall;
                        sx1 = 0;
                        sy1 = 0;
                        break;
                    case 1:
                        img = Common.third_stage_wall;
                        sx1 = 0;
                        sy1 = 0;
                        break;
                    case -1:
                        img = Common.not_destroy_wall;
                        sx1 = 0;
                        sy1 = 0;
                        break;
                    case 0:
                        img = null;
                        sx1 = 0;
                        sy1 = 0;
                        break;
                    default:
                        img = null;
                        sx1 = 0;
                        sy1 = 0;

                        g.setColor(Color.black);
                        g.fillRect(dx1, dy1, inputData.CELL_WIDTH, inputData.CELL_HEIGHT);
                        g.setColor(Color.white);
                        g.drawString(body[i][j] + "", dx1 + 10, dy1 + 10);
                }
                if (img != null) {
                    sx2 = sx1 + 40;
                    sy2 = sy1 + 40;
                    g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
//                    g.setColor(Color.black);
//                    g.drawString(room.body[i][j] + "", dx1 + 10, dy1 + 10);
                }
            }
        }

        InputData.Player[] ps = inputData.getPlayers();
        InputData.Critter[] critters = inputData.getCritters();

        if (drawPath) {
            drawAutomaticPathfinding(g);//绘制路线
        }

        for (InputData.Player player : ps) {
            if (player != null) {
                drawBiology(g, player, player.isQuestionMark() ? player.isTank() ? Common.player_tank : Common.player_question_mark : Common.player);
            }
        }

        for (InputData.Critter critter : critters) {
            if (critter != null) {
                Image image;

                switch (critter.getName()) {
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
            if (charmander != null) {
                drawBiology(g, charmander, Common.charmander);
            }
        }

        InputData.Missile[] missiles = inputData.getMissiles();

        for (InputData.Missile missile : missiles) {
            if (missile != null) {
                drawBiology(g, missile, Common.missile);
            }
        }

        if (inputData.isGameOver()) {
            g.drawImage(Common.gameOver, showStartX, showStartY, Math.min(showSize - inputData.CELL_WIDTH, showStartX + inputData.wSize), Math.min(showSize - inputData.CELL_HEIGHT, showStartY + inputData.hSize),
                    0, 0, 802, 802, null);
        }

        drawProperty(g, ps);

        drawThumbnail(g, critters, ps, charmanders);
    }

    /**
     * 绘制自动寻路
     */
    private long lastDrawPathMillis = System.currentTimeMillis();

    public void drawAutomaticPathfinding(Graphics g) {
        InputData.Player player = inputData.getPlayers()[inputData.getpNumber()];

        if (player.isDie()) {
            return;
        }

        if (System.currentTimeMillis() - lastDrawPathMillis > 100) {
            lastDrawPathMillis = System.currentTimeMillis();
            int[][] movementRoutes = new int[inputData.H][inputData.W];

            Point<InputData.Critter> shortestPaths = pointHelper.findShortestPaths(inputData.getBody(), movementRoutes, player.getLx(), player.getTy());

            if (shortestPaths != null) {
                this.movementRoutes = new int[inputData.H][inputData.W];

                int pathToMoveRoutes = createPathToMoveRoutes(0, shortestPaths, false);//从下一个点开始

                createPathToMoveRoutes(pathToMoveRoutes, shortestPaths.last, true);//需要前一个点做铺垫
            }
        }

        if (this.movementRoutes != null) {
            for (int i = 0; i < this.movementRoutes.length; i++) {
                int[] movementRoute = this.movementRoutes[i];
                for (int j = 0; j < movementRoute.length; j++) {
                    if (isPath(movementRoute[j])) {
                        int dx1 = i * inputData.CELL_WIDTH + Common.interfaceStartX - showStartX;
                        int dy1 = j * inputData.CELL_HEIGHT + Common.interfaceStartY - showStartY;
                        if (cannotDraw(dx1, dy1)) {
                            continue;
                        }

                        int sx1, sy1;
                        switch (movementRoute[j]) {
                            case 10:
                                sx1 = 120;
                                sy1 = 40;
                                break;
                            case 11:
                                sx1 = 120;
                                sy1 = 0;
                                break;
                            case 12:
                                sx1 = 120;
                                sy1 = 80;
                                break;
                            case 13:
                                sx1 = 200;
                                sy1 = 120;
                                break;
                            case 14:
                                sx1 = 240;
                                sy1 = 120;
                                break;
                            case 15:
                                sx1 = 160;
                                sy1 = 120;
                                break;
                            case 16:
                                sx1 = 0;
                                sy1 = 120;
                                break;
                            case 17:
                                sx1 = 40;
                                sy1 = 120;
                                break;
                            case 18:
                                sx1 = 80;
                                sy1 = 120;
                                break;
                            case 19:
                                sx1 = 120;
                                sy1 = 200;
                                break;
                            case 20:
                                sx1 = 120;
                                sy1 = 240;
                                break;
                            case 21:
                                sx1 = 120;
                                sy1 = 160;
                                break;
                            default:
                                sx1 = 0;
                                sy1 = 0;
                                break;
                        }

                        g.drawImage(Common.player_path, dx1, dy1, dx1 + inputData.CELL_WIDTH, dy1 + inputData.CELL_HEIGHT, sx1, sy1, sx1 + 40, sy1 + 40, null);
//                        g.fillOval(dx1 + inputData.CELL_WIDTH / 2 - 5, dy1 + inputData.CELL_HEIGHT / 2 - 5, 10, 10);
                    }
                }
            }
        }
    }

    /**
     * 判断是否为路径值
     */
    public boolean isPath(int number) {
        switch (number) {
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
                return true;
            default:
                return false;
        }
    }

    /**
     * 将路径赋值到数组
     */
    public int createPathToMoveRoutes(int lastNumber, Point<InputData.Critter> critterPoint, boolean sustain) {
        if (critterPoint != null) {
            Point<InputData.Critter> last = critterPoint.last;

            if (last != null) {
                int number;

                if (last.y < critterPoint.y) {//上
                    if (lastNumber == 13 || lastNumber == 14 || lastNumber == 15) {//右往上走
                        number = 10;
                    } else if (lastNumber == 16 || lastNumber == 17 || lastNumber == 18) {//左往上走
                        number = 11;
                    } else {
                        number = 12;
                    }
                } else if (last.y == critterPoint.y) {
                    if (last.x > critterPoint.x) {//右
                        if (lastNumber == 10 || lastNumber == 11 || lastNumber == 12) {//上往右走
                            number = 14;
                        } else if (lastNumber == 19 || lastNumber == 20 || lastNumber == 21) {//下往右走
                            number = 13;
                        } else {
                            number = 15;
                        }
                    } else {//左
                        if (lastNumber == 10 || lastNumber == 11 || lastNumber == 12) {//上往左走
                            number = 17;
                        } else if (lastNumber == 19 || lastNumber == 20 || lastNumber == 21) {//下往左走
                            number = 16;
                        } else {
                            number = 18;
                        }
                    }
                } else {
                    if (lastNumber == 13 || lastNumber == 14 || lastNumber == 15) {//右往下走
                        number = 20;
                    } else if (lastNumber == 16 || lastNumber == 17 || lastNumber == 18) {//左往下走
                        number = 19;
                    } else {
                        number = 21;
                    }
                }

                if (sustain) {
                    this.movementRoutes[critterPoint.x][critterPoint.y] = number;
                    return createPathToMoveRoutes(number, critterPoint.last, true);
                } else {
                    return number;
                }
            }
        }

        return 0;
    }

    /**
     * 绘制玩家
     */
    public void drawBiology(Graphics g, InputData.BasicAttribute basicAttribute, Image image) {
        int dx1 = basicAttribute.getActualX() - showStartX;
        int dy1 = basicAttribute.getActualY() - showStartY;

        if (cannotDraw(dx1, dy1)) {
            return;
        }

        int dx2 = dx1 + inputData.CELL_WIDTH, dy2 = dy1 + inputData.CELL_HEIGHT;
        int sx1, sy1, sx2, sy2;
        switch (basicAttribute.getState()) {
            case 1:
                sx1 = 80;
                sy1 = 200;
                break;
            case 2:
                sx1 = 40;
                sy1 = 200;
                break;
            case 3:
                sx1 = 0;
                sy1 = 200;
                break;
            case 4:
                sx1 = 80;
                sy1 = 0;
                break;
            case 5:
                sx1 = 80;
                sy1 = 40;
                break;
            case 6:
                sx1 = 80;
                sy1 = 120;
                break;
            case 7:
                sx1 = 80;
                sy1 = 160;
                break;
            case 8:
                sx1 = 0;
                sy1 = 80;
                break;
            case 9:
                sx1 = 40;
                sy1 = 80;
                break;
            case 10:
                sx1 = 120;
                sy1 = 80;
                break;
            case 11:
                sx1 = 160;
                sy1 = 80;
                break;
            default:
                sx1 = 0;
                sy1 = 0;
                image = null;//状态不存在那么不画图形了

        }

        if (image != null) {
            sx2 = sx1 + 40;
            sy2 = sy1 + 40;

            //添加玩家名字
            if (basicAttribute instanceof InputData.Player) {
                if (basicAttribute == inputData.getPlayers()[inputData.getpNumber()]) {
                    g.setColor(Color.magenta);
                } else {
                    g.setColor(Color.blue);
                }

                int offsetX;//计算玩家名字偏移量
                try {
                    int gbk = basicAttribute.getName().getBytes("gbk").length;

                    offsetX = -inputData.CELL_WIDTH / 2 + gbk * (fontSize / 4) + (gbk % 2 == 0 ? (fontSize / 8) : 0);
                } catch (UnsupportedEncodingException e) {
                    offsetX = 0;
                    e.printStackTrace();
                }

                g.drawString(basicAttribute.getName(), Math.max(0, dx1 - offsetX), dy1 - fontSize - 2);
            }

            g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
        }
    }


    /**
     * 绘制右侧属性面板
     */
    private void drawProperty(Graphics g, InputData.Player[] ps) {
        int x = Common.interfaceStartX + showSize + 2 + inputData.CELL_WIDTH * 2;

        g.setColor(Color.black);
        g.drawRect(x, Common.interfaceStartY, fontSize * 15, inputData.hSize);//框

        final int[] y = {Common.interfaceStartY};
        int spaceBetween = (int) (fontSize * 1.5);//间距
        g.setColor(Color.red);
        g.drawString("分数： " + inputData.getScore(), x + 5, y[0] += spaceBetween);
        g.drawString("剩余小怪： " + inputData.getSurplusCrittersSize(), x + 5, y[0] += spaceBetween);
        g.drawString("关卡： " + inputData.getCustomsPass(), x + 5, y[0] += spaceBetween);

        for (InputData.Player player : ps) {
            g.setColor(Color.black);
            g.drawLine(x, y[0] += spaceBetween, x + fontSize * 15, y[0]);

            g.setColor(player.isDie() ? Color.red : Color.blue);
            g.drawString("玩家名：" + player.getName() + (player.isDie() ? " - 死亡" : ""), x + 5, y[0] += spaceBetween);
            g.setColor(Color.blue);
            g.drawString("炸弹数量： " + player.getBomNumber(), x + 5, y[0] += spaceBetween);
            g.drawString("炸弹范围： " + player.getBomSize(), x + 5, y[0] += spaceBetween);
            g.drawString("火焰免疫： " + player.isFireImmune(), x + 5, y[0] += spaceBetween);
            g.drawString("无敌时间： " + player.getQuestionMarkTime(), x + 5, y[0] += spaceBetween);
        }
    }

    //判断绘制是否超出可视范围
    public boolean cannotDraw(int dx1, int dy1) {
        return dx1 < Common.interfaceStartX || dx1 > showSize - inputData.CELL_WIDTH || dy1 < Common.interfaceStartY || dy1 > showSize - inputData.CELL_HEIGHT;
    }

    /**
     * 绘制缩略图
     */
    public void drawThumbnail(Graphics g, InputData.Critter[] critters, InputData.Player[] ps, InputData.Charmander[] charmanders) {
        int startX = Common.interfaceStartX + showSize + 300;
        int startY = Common.interfaceStartY;
        int size = 6;

        g.setColor(Color.black);
        g.drawRect(startX, startY, inputData.W * size, inputData.H * size);//框

        g.setColor(Color.red);
        for (InputData.Critter critter : critters) {
            if (critter != null) {
                g.fillRect(startX + critter.getLx() * size, startY + critter.getTy() * size, size, size);
            }
        }

        g.setColor(Color.green);
        for (InputData.Charmander charmander : charmanders) {
            if (charmander != null) {
                g.fillRect(startX + charmander.getLx() * size, startY + charmander.getTy() * size, size, size);
            }
        }

        for (int i = 0; i < ps.length; i++) {
            InputData.Player player = ps[i];
            if (player != null) {
                if (inputData.getpNumber() == i) {//自己的颜色为紫色， 其它的为蓝色
                    g.setColor(Color.magenta);
                } else {
                    g.setColor(Color.blue);
                }
                g.fillRect(startX + player.getLx() * size, startY + player.getTy() * size, size, size);
            }
        }

        g.setColor(Color.green);
        for (int i = 0; i < inputData.H; i++) {
            for (int j = 0; j < inputData.W; j++) {
                if (inputData.getBody(i, j) == Common.PROP_DOOR) {
                    g.fillRect(startX + j * size, startY + i * size, size, size);
                }
            }
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {
//        System.out.println("keyTyped : " + e.getKeyCode());
    }

    //创建实时监听按钮事件
    private final Timer timer = new Timer();
    private final CopyOnWriteArraySet<String> keyDown = new CopyOnWriteArraySet<>();
    private final Timer moveTimer = new Timer();
    private final CopyOnWriteArraySet<String> moveKey = new CopyOnWriteArraySet<>();

    {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendKey();
            }
        }, 0, 80);
        moveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMove();
            }
        }, 0, 40);
    }

    @Override
    public void keyPressed(KeyEvent e) {
//        System.out.println("keyPressed : " + e.getKeyCode());

        switch (e.getKeyCode()) {
            case 38:
            case 87://W
            case 37:
            case 65://D
            case 40:
            case 83://S
            case 39:
            case 68://A
                moveKey.add(e.getKeyCode() + "");
                break;
            case 74://J
            case 75://K
            case 72://H
                keyDown.add(e.getKeyCode() + "");
                break;
            default:
                if (member) {
                    keyDown.add(e.getKeyCode() + "");
                }
                break;
        }
    }

    /**
     * //按键抬起
     */
    @Override
    public void keyReleased(KeyEvent e) {
        keyDown.remove(e.getKeyCode() + "");
        moveKey.remove(e.getKeyCode() + "");
    }

    /**
     * 发送点击事件到服务器
     */
    public void sendKey() {
        keyDown.forEach(this::send);
    }

    public void sendMove() {
        moveKey.forEach(this::send);
    }

    public synchronized void send(String key) {
        sockets.forEach(socket -> {
            Start.outString(socket, key);
        });

        sockets.forEach(socket -> {
            try {
                String s = Start.inString(socket);
            } catch (StringIndexOutOfBoundsException | UnsupportedEncodingException exception) {
                System.out.println("服务器关闭！");
                keyDown.remove(key);
                Start.stop = true;
            }
        });
    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    /**
     * 鼠标离开窗口时调用
     */
    @Override
    public void focusLost(FocusEvent e) {
        keyDown.clear();
        moveKey.clear();
    }
}
