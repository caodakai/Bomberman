package priv.cdk.bomberman.parent;

import priv.cdk.bomberman.critter.Critter;

public class BiotaDieThread extends MyThread {
    private final Biota biota;

    public BiotaDieThread(Biota biota) {
        super(biota.room);
        this.biota = biota;
    }

    @Override
    public void myRun(){
        biota.setState(3);
        try {
            mySleep(500);
            mySleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        biota.setState(2);
        try {
            mySleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        biota.setState(1);
        try {
            mySleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(biota instanceof Critter) {
            biota.room.critters.remove(biota);
        }
        biota.setState(0);
    }
}
