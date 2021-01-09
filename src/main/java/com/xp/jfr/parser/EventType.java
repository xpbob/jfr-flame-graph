package com.xp.jfr.parser;

import jdk.jfr.consumer.RecordedEvent;

import static com.xp.Bootstrap.*;

public enum EventType {


    METHOD_PROFILING_SAMPLE(CPU, ValueField.COUNT, "jdk.NativeMethodSample", "jdk.ExecutionSample"),
    ALLOCATION_IN_NEW_TLAB(TLAB, ValueField.TLAB_SIZE, "jdk.ObjectAllocationInNewTLAB"),
    ALLOCATION_OUTSIDE_TLAB(OUT_TLAB, ValueField.ALLOCATION_SIZE, "jdk.ObjectAllocationOutsideTLAB"),
    JAVA_EXCEPTION(EXCEPTION, ValueField.COUNT, "jdk.JavaExceptionThrow", "jdk.JavaErrorThrow"),
    JAVA_MONITOR_BLOCKED(MONITOR, ValueField.DURATION, "jdk.JavaMonitorWait"),
    IO(IO_SOCK, ValueField.DURATION, "jdk.FileRead", "jdk.FileWrite", "jdk.SocketRead", "jdk.SocketWrite");

    private String commandLineOption;
    private ValueField valueField;
    private String[] eventNames;

    EventType(String commandLineOption, ValueField valueField, String... eventNames) {
        this.commandLineOption = commandLineOption;
        this.valueField = valueField;
        this.eventNames = eventNames;
    }

    public String getCommandLineOption() {
        return commandLineOption;
    }

    public ValueField getValueField() {
        return valueField;
    }

    public String[] getEventNames() {
        return eventNames;
    }


}
