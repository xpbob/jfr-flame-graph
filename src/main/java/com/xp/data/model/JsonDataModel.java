package com.xp.data.model;

import com.google.gson.Gson;
import com.xp.jfr.parser.StackFrame;

import java.util.List;

public class JsonDataModel implements DataModel {
    StackFrame stackFrame = new StackFrame("root");

    @Override
    public void update(List<String> collect, long dataValue) {
        StackFrame stackFrameTmp = stackFrame;
        for (int i = collect.size() - 1; i >= 0; i--) {
            stackFrameTmp = stackFrameTmp.addFrame(collect.get(i), (int) dataValue);
        }
    }

    @Override
    public String toDataString() {
        Gson gson = new Gson();
        return gson.toJson(stackFrame);
    }
}
