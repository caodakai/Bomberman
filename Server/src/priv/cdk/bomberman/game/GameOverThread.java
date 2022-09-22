package priv.cdk.bomberman.game;

public class GameOverThread extends Thread {
    private final Game game;

    private boolean canManualAwaken = false;//能手动唤醒

    public GameOverThread(Game game){
        this.game = game;
    }

    @Override
    public void run() {
        synchronized (this) {
            while (true) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(game.stop.get()){
                    break;
                }

                try {
                    Thread.sleep(3000);//前三秒是不允许重新开始的，因为有对象并未执行完，可能导致第二次重新开始
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                canManualAwaken = true;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
//                  e.printStackTrace();
//                  System.out.println("手动开始游戏");
                }
                canManualAwaken = false;
                game.reloadGame();
            }
        }
    }

    public boolean isCanManualAwaken() {
        return canManualAwaken;
    }


}
