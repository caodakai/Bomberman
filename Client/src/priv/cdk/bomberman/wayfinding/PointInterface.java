package priv.cdk.bomberman.wayfinding;

public interface PointInterface<T> {

    T hasT(int y, int x);

    boolean canMove(int number);
}
