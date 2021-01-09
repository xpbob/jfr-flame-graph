package com.xp.jfr.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackFrame {

    String name;
    int value = 0;
    List<StackFrame> children = null;
    transient Map<String, StackFrame> childrenMap = new HashMap<>();

    public StackFrame(String name) {
        this.name = name;
    }

    public StackFrame addFrame(String frameName, int increase) {
        if (children == null) {
            children = new ArrayList<>();
        }
        StackFrame frame = childrenMap.get(frameName);
        if (frame == null) {
            frame = new StackFrame(frameName);
            childrenMap.put(frameName, frame);
            children.add(frame);
        }
        frame.value += increase;
        return frame;
    }
}
