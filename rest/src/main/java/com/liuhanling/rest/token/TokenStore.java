package com.liuhanling.rest.token;

import java.util.HashMap;

public class TokenStore {

    private static final String DEFAULT_TOKEN_KEY = "default_token_key";
    private static final HashMap<String, String> TOKENS = new HashMap<>();

    private static class TokenHolder {
        private static final TokenStore INSTANCE = new TokenStore();
    }

    public static TokenStore getInstance() {
        return TokenHolder.INSTANCE;
    }

    public String getToken() {
        return TOKENS.get(DEFAULT_TOKEN_KEY);
    }

    public String getToken(String key) {
        return TOKENS.get(key);
    }

    public void setToken(String token) {
        TOKENS.put(DEFAULT_TOKEN_KEY, token);
    }

    public void setToken(String key, String token) {
        TOKENS.put(key, token);
    }

    public void removeToken(String key) {
        TOKENS.remove(key);
    }

    public void removeAllToken() {
        TOKENS.clear();
    }
}
