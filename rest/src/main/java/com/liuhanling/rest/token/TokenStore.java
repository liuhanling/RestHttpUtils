package com.liuhanling.rest.token;

import java.util.HashMap;
import java.util.Map;

public class TokenStore {

    private static final String DEFAULT_TOKEN_KEY = "default_token_key";
    private static final HashMap<String, String> TOKENS = new HashMap<>();
    
    private TokenStore() {
    }

    public static String getToken() {
        return TOKENS.get(DEFAULT_TOKEN_KEY);
    }

    public static String getToken(String key) {
        return TOKENS.get(key);
    }

    public static Map<String, String> getAllToken() {
        return new HashMap<>(TOKENS);
    }

    public static void setToken(String token) {
        TOKENS.put(DEFAULT_TOKEN_KEY, token);
    }

    public static void setToken(String key, String token) {
        TOKENS.put(key, token);
    }

    public static void removeToken(String key) {
        TOKENS.remove(key);
    }

    public static void removeAllToken() {
        TOKENS.clear();
    }
}
