package priv.cdk.bomberman.parent;

import priv.cdk.bomberman.room.Room;

public abstract class MyThread extends Thread implements MyRunnable {
    protected Room myRoom;

    public MyThread(Room room){
        this.myRoom = room;
    }

    @Override
    public void run(){
        myRoom.threads.add(this);
        myRun();
        myRoom.threads.remove(this);
    }

    public void mySleep(long millis) throws InterruptedException {
        if (!myRoom.close.get()) {
            if (myRoom.suspend.get()) {
                synchronized (this) {
                    wait();
                }
            }
        }

        mainSleep(millis);

        if (!myRoom.close.get()){
            if (myRoom.suspend.get()) {
                synchronized (this) {
                    wait();
                }
            }
        }
    }

    public static void mainSleep(long millis) throws InterruptedException {
        sleep(millis);
    }
}
