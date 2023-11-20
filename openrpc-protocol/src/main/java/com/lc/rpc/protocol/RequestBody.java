package com.lc.rpc.protocol;

import java.io.Serializable;

/**
 * @author gujixian
 * @since 2023/11/19
 */
public class RequestBody implements Serializable {
    private String className;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] paramValues;

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

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParamValues() {
        return paramValues;
    }

    public void setParamValues(Object[] paramValues) {
        this.paramValues = paramValues;
    }
}
