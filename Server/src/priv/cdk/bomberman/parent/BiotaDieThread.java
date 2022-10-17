package priv.cdk.bomberman.parent;

import priv.cdk.bomberman.charmander.Charmander;
import priv.cdk.bomberman.critter.Critter;
import priv.cdk.bomberman.data.InputData;

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
            mySleep(biota.getDieTime());
            mySleep(biota.getDieTime() * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        biota.setState(2);
        try {
            mySleep(biota.getDieTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        biota.setState(1);
        try {
            mySleep(biota.getDieTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(biota instanceof Critter) {
            biota.room.critters.remove(biota);
        }else if (biota instanceof Charmander){
            biota.room.charmanders.remove(biota);
        }
        biota.setState(0);
    }
}
