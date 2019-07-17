package com.liuhanling.rest.interfaces;

import java.util.Map;

public interface HeadersListener {
    Map<String, String> buildHeaders();
}