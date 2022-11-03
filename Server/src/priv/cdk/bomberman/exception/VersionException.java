package priv.cdk.bomberman.exception;

import java.util.HashMap;
import java.util.Map;

public class VersionException extends Exception {
    public static final Map<Integer, String> massages = new HashMap<>();

    private final String massage;

    private final int code;

    public VersionException(int code){
        this.code = code;
        this.massage = massages.get(this.code);
    }

    public String getMassage() {
        return massage;
    }

    public int getCode() {
        return code;
    }

    static{
        massages.put(1, "版本过低！");
    }
}
