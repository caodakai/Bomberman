package priv.cdk.bomberman.critter;

public class CritterThreadUtil {
    /**
     * 随机移动，如果随机的路线不可走，那么依次判断路口
     */
    public static int randomMove(Critter critter, int startNumber, int number){
        int tb, lr;
        switch (number) {
            case 0:
                critter.setState(7);
                tb = - critter.moveSize;
                lr = 0;
                break;
            case 1:
                critter.setState(4);
                tb = critter.moveSize;
                lr = 0;
                break;
            case 2:
                critter.setState(5);
                tb = 0;
                lr = - critter.moveSize;
                break;
            case 3:
                critter.setState(6);
                tb = 0;
                lr = critter.moveSize;
                break;
            default:
                tb = 0;
                lr = 0;
                break;
        }

        if(!critter.isDie()){
            boolean move = critter.move(tb, lr);
            if(!move){
                if(startNumber == (number == 3 ? 0 : (number + 1))){
                    return 5;//全没找到
                }else{
                    return randomMove(critter, startNumber, (number == 3 ? 0 : (number + 1)));
                }
            }else{
                return number;
            }
        }else {
            return 4;//已经死亡
        }
    }

    /**
     * 通过移动方向确认实际移动像素
     * @param number 移动方向
     * @param moveSize 移动大小
     * @return tb : 0 , lr :1
     */
    public static int[] movePx(int number, int moveSize){
        switch (number) {
            case 0:
                return new int[]{-moveSize, 0};
            case 1:
                return new int[]{moveSize, 0};
            case 2:
                return new int[]{0, - moveSize};
            case 3:
                return new int[]{0, moveSize};
            default:
                return new int[]{0, 0};
        }
    }
}
