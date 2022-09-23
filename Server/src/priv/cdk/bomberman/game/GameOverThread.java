package priv.cdk.bomberman.game;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameOverThread extends Thread {
    private final Game game;

    public final AtomicBoolean canManualAwaken = new AtomicBoolean(false);//能手动唤醒

    public GameOverThread(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        try {
            while (!game.stop.get()) {
                synchronized (this) {
                    this.wait();
                }

                if (game.stop.get()) {
                    break;
                }

                Thread.sleep(3000);//前三秒是不允许重新开始的，因为有对象并未执行完，可能导致第二次重新开始

                canManualAwaken.set(true);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
//                  System.out.println("手动开始游戏");
                }
                canManualAwaken.set(false);

                if (game.isGameOver()) {//游戏是结束状态
                    game.reloadGame();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
