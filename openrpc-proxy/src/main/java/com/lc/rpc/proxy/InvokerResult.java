package com.lc.rpc.proxy;

/**
 * @author gujixian
 * @since 2023/11/21
 */
public class InvokerResult {
    private int statusCode;
    private String statusDesc;
    private Object result;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
