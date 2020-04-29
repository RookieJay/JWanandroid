package pers.jay.wanandroid.result;

import pers.jay.wanandroid.common.Const;

public class BaseWanBean {

    private int errorCode;
    private String errorMsg;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return errorCode == Const.HttpConst.HTTP_CODE_SUCCESS;
    }
}
