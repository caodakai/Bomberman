package priv.cdk.bomberman.parent;

public class BiotaUtil {

    public static boolean haveBiota(Biota biota, int x, int y){
        if (!biota.isDie()) {
            if (biota.getLx() == x) {
                return biota.getTy() == y || biota.getBy() == y;
            } else if (biota.getRx() == x) {
                return biota.getTy() == y || biota.getBy() == y;
            }
        }
        return false;
    }
}
