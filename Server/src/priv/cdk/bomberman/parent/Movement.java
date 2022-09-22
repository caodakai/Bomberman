package priv.cdk.bomberman.parent;

public interface Movement {
    boolean canMove(int y, int x);

    boolean die();
}
