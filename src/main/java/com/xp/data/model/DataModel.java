package com.xp.data.model;

import java.util.List;

public interface DataModel {
    void update(List<String> collect, long dataValue);

    String toDataString();
}
