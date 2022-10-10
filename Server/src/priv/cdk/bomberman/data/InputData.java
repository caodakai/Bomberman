package priv.cdk.bomberman.data;

import priv.cdk.bomberman.critter.dragon.DragonCritter;
import priv.cdk.bomberman.critter.elite.EliteCritter;
import priv.cdk.bomberman.critter.knight.KnightCritter;
import priv.cdk.bomberman.game.Game;
import priv.cdk.bomberman.parent.Biota;
import priv.cdk.bomberman.room.Room;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

public class InputData implements Serializable {
    private static final long serialVersionUID = 1L;

    public int CELL_WIDTH = Room.CELL_WIDTH;
    public int CELL_HEIGHT = Room.CELL_HEIGHT;
    public int H;
    public int W;
    public int wSize;
    public int hSize;

    private Player[] players;
    private Critter[] critters;
    private Charmander[] charmanders;
    private int[][] body;
    private boolean gameOver;
    private int score;//分数
    private int surplusCrittersSize;//剩余小怪
    private int customsPass;//关卡

    private int pNumber;//第几个玩家

    public InputData(int pNumber, Game game){
        this.pNumber = pNumber;

        this.players = new Player[game.room.ps.size()];
        reload(game);
    }


    public void reload(Game game){
        Room room = game.room;

        this.H = room.getH();
        this.W = room.getW();
        this.wSize = room.wSize;
        this.hSize = room.hSize;

        CopyOnWriteArrayList<priv.cdk.bomberman.player.Player> ps = room.ps;
        for (int i = 0; i < ps.size(); i++) {
            if(players[i] == null){
                players[i] = new Player(ps.get(i));
            }else {
                players[i].reload(ps.get(i));
            }
        }

        CopyOnWriteArraySet<priv.cdk.bomberman.critter.Critter> critters = room.critters;

        this.critters = new Critter[critters.size()];

        AtomicInteger i = new AtomicInteger();
        critters.forEach(critter -> {
            this.critters[i.getAndIncrement()] = new Critter(critter);
        });

        CopyOnWriteArraySet<priv.cdk.bomberman.charmander.Charmander> charmanders = room.charmanders;

        this.charmanders = new Charmander[charmanders.size()];

        AtomicInteger j = new AtomicInteger();
        charmanders.forEach(charmander -> {
            this.charmanders[j.getAndIncrement()] = new Charmander( charmander );
        });


        this.body = room.getBody();
        this.gameOver = game.isGameOver();
        this.score = game.getScore();
        this.surplusCrittersSize = critters.size();
        this.customsPass = game.getCustomsPass();
    }

    public static class BasicAttribute implements Serializable{
        private static final long serialVersionUID = 1L;

        private String name;
        private int actualX;//实际X坐标像素位置
        private int actualY;//实际Y坐标像素位置
        private int lx;//单元格位置
        private int ty;//单元格位置
        private int state;//状态

        private boolean die;//是否死亡

        public BasicAttribute(Biota biota){
            reload(biota);
        }

        public void reload(Biota biota){
            this.actualX = biota.getActualX();
            this.actualY = biota.getActualY();
            this.lx = biota.getLx();
            this.ty = biota.getTy();
            this.state = biota.getState();
            this.die = biota.isDie();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getActualX() {
            return actualX;
        }

        public void setActualX(int actualX) {
            this.actualX = actualX;
        }

        public int getActualY() {
            return actualY;
        }

        public void setActualY(int actualY) {
            this.actualY = actualY;
        }

        public int getLx() {
            return lx;
        }

        public void setLx(int lx) {
            this.lx = lx;
        }

        public int getTy() {
            return ty;
        }

        public void setTy(int ty) {
            this.ty = ty;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public boolean isDie() {
            return die;
        }

        public void setDie(boolean die) {
            this.die = die;
        }
    }

    public static class Player extends BasicAttribute implements Serializable{
        private static final long serialVersionUID = 1L;

        private boolean questionMark;//是否处于无敌状态
        private int bomNumber;//炸弹数量
        private int bomSize;//炸弹范围
        private int questionMarkTime;//无敌时间

        public Player(priv.cdk.bomberman.player.Player player){
            super(player);
            reload(player);
        }

        public void reload(priv.cdk.bomberman.player.Player player){
            super.reload(player);

            this.setName(player.name);

            this.questionMark = player.isQuestionMark();
            this.bomNumber = player.getBomNumber();
            this.bomSize = player.getBomSize();
            this.questionMarkTime = player.questionMarkThread.getQuestionMarkTime();
        }

        public boolean isQuestionMark() {
            return questionMark;
        }

        public void setQuestionMark(boolean questionMark) {
            this.questionMark = questionMark;
        }

        public int getBomNumber() {
            return bomNumber;
        }

        public void setBomNumber(int bomNumber) {
            this.bomNumber = bomNumber;
        }

        public int getBomSize() {
            return bomSize;
        }

        public void setBomSize(int bomSize) {
            this.bomSize = bomSize;
        }

        public int getQuestionMarkTime() {
            return questionMarkTime;
        }

        public void setQuestionMarkTime(int questionMarkTime) {
            this.questionMarkTime = questionMarkTime;
        }
    }

    public static class Critter extends BasicAttribute implements Serializable{
        private static final long serialVersionUID = 1L;

        public Critter(priv.cdk.bomberman.critter.Critter critter){
            super(critter);

            String name;
            if(critter instanceof KnightCritter){
                KnightCritter knightCritter = (KnightCritter) critter;
                if (knightCritter.getHP() > 1) {
                    name = "Knight_1";
                }else{
                    name = "Knight_2";
                }
            }else if (critter instanceof DragonCritter){
                DragonCritter dragonCritter = (DragonCritter) critter;
                if (dragonCritter.isBLUE()){
                    name = "Dragon_blue";
                }else{
                    name = "Dragon_golden";
                }
            }else if(critter instanceof EliteCritter){
                name = "Elite";
            }else{
                name = "";
            }

            this.setName(name);
        }

    }

    public static class Charmander extends BasicAttribute implements Serializable {
        private static final long serialVersionUID = 1L;

        public Charmander(priv.cdk.bomberman.charmander.Charmander charmander) {
            super(charmander);
            this.setName("小火龙");
        }
    }


    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public Critter[] getCritters() {
        return critters;
    }

    public void setCritters(Critter[] critters) {
        this.critters = critters;
    }

    public int getBody(int x, int y) {
        return body[x][y];
    }

    public void setBody(int[][] body) {
        this.body = body;
    }

    public int getpNumber() {
        return pNumber;
    }

    public void setpNumber(int pNumber) {
        this.pNumber = pNumber;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSurplusCrittersSize() {
        return surplusCrittersSize;
    }

    public void setSurplusCrittersSize(int surplusCrittersSize) {
        this.surplusCrittersSize = surplusCrittersSize;
    }

    public int getCustomsPass() {
        return customsPass;
    }

    public void setCustomsPass(int customsPass) {
        this.customsPass = customsPass;
    }

    public Charmander[] getCharmanders() {
        return charmanders;
    }

    public void setCharmanders(Charmander[] charmanders) {
        this.charmanders = charmanders;
    }
}