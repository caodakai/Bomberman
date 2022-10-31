package priv.cdk.bomberman.player;

import priv.cdk.bomberman.parent.MyThread;
import priv.cdk.bomberman.room.Room;

import java.util.concurrent.atomic.AtomicInteger;

public class QuestionMarkThread extends MyThread {
    private final Player player;
    private long id;
    private final AtomicInteger questionMarkTime = new AtomicInteger(5);//无敌时间
    
    public QuestionMarkThread(Room room, Player player) {
        super(room);
        this.player = player;
        this.id = player.getId();
    }
    
    @Override
    public void myRun() {
        while (!player.isDie(id)) {
            player.questionMark.set(true);

            while (questionMarkTime.get() > 0 && !player.isDie(id)) {
                try {
                    mySleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!player.isMember()) {
                    questionMarkTime.addAndGet(-1);
                }
            }

            player.questionMark.set(false);
            player.setTank(false);

            try {
                synchronized (this) {
                    if(!player.isDie(id)) {
                        this.wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开启无敌
     * @param time 增加无敌时间 单位秒
     */
    public void openQuestionMark(int time){
        questionMarkTime.addAndGet(time);
        if(!player.questionMark.get()) {
            synchronized (this) {
                this.notify();
            }
        }
    }

    public void updateId(){
        this.id = player.getId();
    }

    public int getQuestionMarkTime() {
        return questionMarkTime.get();
    }
}
