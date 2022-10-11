package priv.cdk.bomberman.utils;

import priv.cdk.bomberman.common.Common;

public class IsUtil {

    /**
     * 坐标数是否为炸弹
     */
    public static boolean isBom(int number){
        switch (number){
            case 1001:
            case 1002:
            case 1003:
                return true;
            default:
                return false;
        }
    }

    /**
     * 坐标是否为火
     */
    public static boolean isFire(int number){
        return number > 1000 && number < 2000;
    }

    /**
     * 是否为中间火
     */
    public static boolean isMiddle(int number){
        switch (number){
            case 1004:
            case 1005:
            case 1006:
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否为上终点火
     */
    public static boolean isUpDestination(int number){
        switch (number){
            case 1007:
            case 1008:
            case 1009:
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否为上火
     */
    public static boolean isUp(int number){
        switch (number){
            case 1010:
            case 1011:
            case 1012:
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否为下终点火
     */
    public static boolean isDownDestination(int number){
        switch (number){
            case 1013:
            case 1014:
            case 1015:
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否为下火
     */
    public static boolean isDown(int number){
        switch (number){
            case 1016:
            case 1017:
            case 1018:
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否为左终点火
     */
    public static boolean isLeftDestination(int number){
        switch (number){
            case 1019:
            case 1020:
            case 1021:
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否为左火
     */
    public static boolean isLeft(int number){
        switch (number){
            case 1022:
            case 1023:
            case 1024:
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否为右终点火
     */
    public static boolean isRightDestination(int number){
        switch (number){
            case 1025:
            case 1026:
            case 1027:
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否为右火
     */
    public static boolean isRight(int number){
        switch (number){
            case 1028:
            case 1029:
            case 1030:
                return true;
            default:
                return false;
        }
    }

    /**
     * 坐标是否为墙
     */
    public static boolean isWall(int number){
        return isSpecialWall(number) || isOrdinaryWall(number);
    }

    /**
     * 坐标是否为普通的墙
     */
    public static boolean isOrdinaryWall(int number){
        return number == 1 || number == 2 || number == 3 || number == 4;
    }


    /**
     * 坐标是否为特殊的墙
     */
    public static boolean isSpecialWall(int number){
        return number < 0 ;
    }


    /**
     * 坐标是否为未来的火
     */
    public static boolean isFutureBodyFire(int number){
        switch (number) {
            case Common.FUTURE_BODY_FIRE_NUMBER_TOP:
            case Common.FUTURE_BODY_FIRE_NUMBER_BOTTOM:
            case Common.FUTURE_BODY_FIRE_NUMBER_LEFT:
            case Common.FUTURE_BODY_FIRE_NUMBER_RIGHT:
            case Common.FUTURE_BODY_FIRE_NUMBER_CENTRE:
                return true;
            default:
                return false;
        }
    }


    /**
     * 判断是否为道具
     */
    public static boolean isProp(int number){
        switch (number) {
            case Common.PROP_SCOPE_ADD:
            case Common.PROP_BOM_ADD:
            case Common.PROP_BOM_CONTROL:
            case Common.PROP_SPEED_ADD:
            case Common.PROP_BOM_THROUGH:
            case Common.PROP_WALL_THROUGH:
            case Common.PROP_QUESTION_MARK:
            case Common.PROP_FIRE_IMMUNE:
            case Common.PROP_DOOR:
                return true;
            default:
                return false;
        }
    }
}
