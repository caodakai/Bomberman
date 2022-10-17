package priv.cdk.bomberman.wayfinding;

public class Point<T> {
    public int x;
    public int y;
    public int step;
    public Point<T> last;
    public T biota;

    public Point(int y, int x, int step, Point<T> last) {
        this.x = x;
        this.y = y;
        this.step = step;
        this.last = last;
    }
}
