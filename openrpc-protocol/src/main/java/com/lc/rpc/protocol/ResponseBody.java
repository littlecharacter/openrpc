package com.lc.rpc.protocol;

import java.io.Serializable;

/**
 * @author gujixian
 * @since 2023/11/19
 */
public class ResponseBody implements Serializable {
    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
