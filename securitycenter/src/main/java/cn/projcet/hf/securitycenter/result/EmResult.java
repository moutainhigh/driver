package cn.projcet.hf.securitycenter.result;

/**
 * Created by Administrator on 2017/1/22.
 */

public class EmResult {
    private int code;

    private String msg;

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

    /**
     * 录音保护授权 0未授权
     */
    public int soundRecordCheck;
    /**
     * 紧急联系人授权 0未授权
     */
    public int emergeContackCheck;

}
