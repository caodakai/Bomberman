package priv.cdk.bomberman.data;

import java.io.Serializable;

/**
 * 用于拆分封装的传输资源
 */
public class UDPData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long id;//每次传输集合的id

    private final int index;//当前传输的数据所在的下标

    private final byte[] bytes;//当前传输的数据

    private final int finalIndex;//最大的下标

    private final int finalLength;//最终的长度

    private final State state;//当前传输的状态

    public UDPData(long id, int index, byte[] bytes, int finalIndex, int finalLength, State state) {
        this.id = id;
        this.index = index;
        this.bytes = bytes;
        this.finalIndex = finalIndex;
        this.finalLength = finalLength;
        this.state = state;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getFinalIndex() {
        return finalIndex;
    }

    public int getFinalLength() {
        return finalLength;
    }

    public State getState() {
        return state;
    }

    public enum State implements Serializable {
        START,SEND,END
    }
}
