package priv.cdk.bomberman.data;

import java.io.Serializable;

public class InputData implements Serializable {
    private static final long serialVersionUID = 1L;

    public int CELL_WIDTH;
    public int CELL_HEIGHT;
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

    public static class BasicAttribute implements Serializable{
        private static final long serialVersionUID = 1L;

        private String name;
        private int actualX;//实际X坐标像素位置
        private int actualY;//实际Y坐标像素位置
        private int lx;//单元格位置
        private int ty;//单元格位置
        private int state;//状态

        private boolean die;//是否死亡

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
        private boolean fireImmune;//是否处于火焰免疫状态
        private int bomNumber;//炸弹数量
        private int bomSize;//炸弹范围
        private int questionMarkTime;//无敌时间

        public boolean isQuestionMark() {
            return questionMark;
        }

        public void setQuestionMark(boolean questionMark) {
            this.questionMark = questionMark;
        }

        public boolean isFireImmune() {
            return fireImmune;
        }

        public void setFireImmune(boolean fireImmune) {
            this.fireImmune = fireImmune;
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
    }

    public static class Charmander extends BasicAttribute implements Serializable {
        private static final long serialVersionUID = 1L;
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

    public int[][] getBody() {
        return body;
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
}