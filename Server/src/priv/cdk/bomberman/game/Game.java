package priv.cdk.bomberman.game;

import priv.cdk.bomberman.player.Player;
import priv.cdk.bomberman.room.Room;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {
    public final AtomicBoolean stop = new AtomicBoolean(false);//游戏关闭  最终指令

    private final AtomicBoolean gameOver = new AtomicBoolean(false);//游戏结束
    private final GameOverThread gameOverThread = new GameOverThread(this);//游戏结束将调用的线程
    private final AtomicInteger score = new AtomicInteger(0);//分数

    private int customsPass = 1;//关卡

    public Room room;

    public Game(String...players){
       this.room = new Room(this, null);

        for (String name : players) {
            this.room.addPlayer(name);
        }

       this.gameOverThread.start();
    }

    //过关
    public void reloadRoom(){
        customsPass ++ ;
        this.room = new Room(this, room.ps);
        room.ps.forEach(player->{
            if(!player.isDie()){
                player.reload(this.room);
                player.questionMarkThread.openQuestionMark(5);
            }
        });
    }

    /**
     * 重新开始游戏
     */
    void reloadGame(){
        if(!stop.get()) {
            this.customsPass = 1;

            CopyOnWriteArrayList<Player> ps = this.room.ps;

            this.room = new Room(this, null);

            ps.forEach(player -> {
                this.room.addPlayer(player.name);
            });
            this.score.set(0);
            this.gameOver.set(false);
        }
    }

    /**
     * 游戏结束，游戏将在13秒后重新启动
     */
    public void gameOver(){
        if (gameOver.compareAndSet(false, true)) {
            synchronized (gameOverThread) {
                gameOverThread.notify();
            }
        }
    }

    /**
     * 彻底关闭游戏
     */
    public void gameStop(){
       if(stop.compareAndSet(false, true)) {
           room.closeRoom();//清除小怪

           room.ps.forEach(player -> {
               player.stop();

               synchronized (player.moveThread) {
                   player.moveThread.notify();
               }
               synchronized (player.questionMarkThread) {
                   player.questionMarkThread.notify();
               }
           });

           synchronized (gameOverThread) {
               gameOverThread.notify();
           }
       }
    }

    public boolean isGameOver() {
        return gameOver.get();
    }

    public int getScore() {
        return score.get();
    }

    public void addScore(int scoreNumber){
        if(!gameOver.get()){
            score.addAndGet(scoreNumber);
        }
    }

    public void startGame(){
        if(gameOver.get() && gameOverThread.canManualAwaken.compareAndSet(true, false)){
            System.out.println("重启");
            gameOverThread.interrupt();
        }
    }

    public Thread getGameOverThread() {
        return gameOverThread;
    }

    public int getCustomsPass() {
        return customsPass;
    }
}
