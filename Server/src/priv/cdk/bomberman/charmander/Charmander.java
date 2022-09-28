package priv.cdk.bomberman.charmander;

import priv.cdk.bomberman.parent.Biota;
import priv.cdk.bomberman.room.Room;
import priv.cdk.bomberman.utils.IsUtil;

public class Charmander extends Biota {
     public int moveSize = Room.CELL_WIDTH/4;
     public int moveTime = 100;

    public Charmander(Room room, int lx, int ty) {
        super(room, lx, ty);
    }

    @Override
    public boolean move(int xPx, int yPx){
        boolean move = super.move(xPx, yPx);

        room.critters.forEach(critter -> {
            if(!critter.isDie()) {
                int x = critter.getActualX() - getActualX();
                int y = critter.getActualY() - getActualY();

                boolean b = (y >= 0 && y < Room.CELL_HEIGHT) || (y <= 0 && y > -Room.CELL_HEIGHT);
                if ((x >= 0 && x < Room.CELL_WIDTH && b) || (x <= 0 && x > -Room.CELL_WIDTH && b)) {
                    critter.die();
                }
            }
        });

        return move;
    }


    @Override
    public boolean canMove(int y, int x) {
        int bodyNumber = room.getBodyCellValue(y, x);
        return (!IsUtil.isWall(bodyNumber)) && (!IsUtil.isBom(bodyNumber));
    }


}
