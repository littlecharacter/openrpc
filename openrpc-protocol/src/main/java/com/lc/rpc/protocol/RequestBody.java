package com.lc.rpc.protocol;

import java.io.Serializable;

/**
 * @author gujixian
 * @since 2023/11/19
 */
public class RequestBody implements Serializable {
    private String className;
    private String methodName;
    private Class<?>[] types;
    private Object[] params;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getTypes() {
        return types;
    }

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
