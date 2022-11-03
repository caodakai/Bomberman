package priv.cdk.bomberman.server;

import priv.cdk.bomberman.UserInterface;
import priv.cdk.bomberman.game.Game;

import java.net.InetAddress;
import java.util.Timer;

public class PlayerData {
    private int number; //第几个玩家

    private String playerName;//玩家名称

    private boolean member;//是否为管理员

    private Game game;//玩家所处游戏对象

    private UserInterface userInterface;//玩家操作对象

    private InetAddress address;//连接集合

    private String ip;//玩家的ip地址

    private int port;//玩家交互的端口号

    private int dataPort;//玩家接收数据的端口号

    private Thread thread;//玩家交互的线程

    private Timer timer;//发送到玩家接收数据的线程

    public PlayerData(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getDataPort() {
        return dataPort;
    }

    public void setDataPort(int dataPort) {
        this.dataPort = dataPort;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public UserInterface getUserInterface() {
        return userInterface;
    }

    public void setUserInterface(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public boolean isMember() {
        return member;
    }

    public void setMember(boolean member) {
        this.member = member;
    }
}
