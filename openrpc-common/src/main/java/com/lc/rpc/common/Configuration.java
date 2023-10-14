package com.lc.rpc.common;

import java.util.Properties;

/**
 * @author gujixian
 * @since 2023/10/13
 */
public final class Configuration {
    private static Properties properties;

    private Configuration() {}

    public static void setProperties(Properties ps) {
        properties = ps;
    }

    public static <T> T getProperty(String key) {
        return (T) properties.get(key);
    }
}
