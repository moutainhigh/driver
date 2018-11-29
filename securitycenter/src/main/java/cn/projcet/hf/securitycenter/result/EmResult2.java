package cn.projcet.hf.securitycenter.result;

/**
 * Created by liuzihao on 2018/11/20.
 */

public class EmResult2<T> {
    private int code;

    private String msg;

    private T data;

    private int total;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }

    public T getData() {
        return data;
    }

    public int getTotal() {
        return total;
    }
}
