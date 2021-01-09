package com.xp.data.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FoldedDataModel implements DataModel {
    private final Map<String, Long> stackTraceMap = new LinkedHashMap<>();


    @Override
    public void update(List<String> collect, long dataValue) {
        StringBuilder stackTraceBuilder = new StringBuilder();
        boolean appendSemicolon = false;
        for (int i = collect.size() - 1; i >= 0; i--) {
            if (appendSemicolon) {
                stackTraceBuilder.append(";");
            } else {
                appendSemicolon = true;
            }
            stackTraceBuilder.append(collect.get(i));
        }

        String stackTrace = stackTraceBuilder.toString();
        Long count = stackTraceMap.get(stackTrace);
        if (count == null) {
            count = dataValue;
        } else {
            count += dataValue;
        }
        stackTraceMap.put(stackTrace, count);
    }

    @Override
    public String toDataString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Long> entry : stackTraceMap.entrySet()) {
            stringBuilder.append(String.format("%s %d%n", entry.getKey(), entry.getValue()));
        }
        return stringBuilder.toString();
    }
}
