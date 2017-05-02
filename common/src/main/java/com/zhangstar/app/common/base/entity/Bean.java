package com.zhangstar.app.common.base.entity;


/**
 * 返回封装类
 * Created by zhang.wx on 16/7/3.
 */
public class Bean<T> {

    private String statusCode;
    private String sessionId;
    private String errorMsg;
    private String totalElements;//数据源返回的数据有多少条  ---总数
    private String[] errors;

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }

    public String getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(String totalElements) {
        this.totalElements = totalElements;
    }


    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }


}
