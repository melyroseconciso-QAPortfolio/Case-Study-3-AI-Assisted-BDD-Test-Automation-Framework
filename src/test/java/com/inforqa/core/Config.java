package com.inforqa.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Reads settings from config.properties. Command-line values (-Dname=value) win. */
public final class Config {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read config.properties", e);
        }
    }

    private Config() {
    }

    public static String get(String key) {
        String fromCommandLine = System.getProperty(key);
        return fromCommandLine != null ? fromCommandLine : PROPS.getProperty(key);
    }

    public static String baseUrl() {
        return get("baseUrl");
    }

    public static String browser() {
        return get("browser").toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static boolean record() {
        return Boolean.parseBoolean(get("record"));
    }

    public static boolean stepScreenshots() {
        return Boolean.parseBoolean(get("stepScreenshots"));
    }

    public static String apiBaseUrl() {
        return get("apiBaseUrl");
    }

    public static String apiUsername() {
        return get("apiUsername");
    }

    public static String apiPassword() {
        return get("apiPassword");
    }

    public static int timeoutSeconds() {
        return Integer.parseInt(get("timeoutSeconds"));
    }
}
