package com.xp.jfr.parser;

import com.xp.data.model.DataModel;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.consumer.RecordedStackTrace;
import jdk.jfr.consumer.RecordingFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JfrParser {

    DataModel dataModel;

    public JfrParser(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public List<RecordedEvent> getRecordStream(RecordingFile recordingFile) throws IOException {
        List<RecordedEvent> recordedEventList = new ArrayList<>();
        while (recordingFile.hasMoreEvents()) {
            RecordedEvent recordedEvent = recordingFile.readEvent();
            recordedEventList.add(recordedEvent);
        }
        return recordedEventList;

    }


    public void getStackWithFiled(RecordedEvent recordedEvent, EventType eventType) {
        long dataValue = eventType.getValueField().getValue(recordedEvent);
        RecordedStackTrace stackTrace = recordedEvent.getStackTrace();
        if (stackTrace != null) {
            List<String> collect = stackTrace.getFrames().stream().map(JfrParser::formatStack).collect(Collectors.toList());
            dataModel.update(collect, (int) dataValue);
        }

    }


    public void getStackWithString(RecordedEvent recordedEvent) {
        String stacks = recordedEvent.toString();
        String[] splitData = stacks.split("\\s\\n");
        for (String splitDatum : splitData) {
            if (splitDatum.contains("java.lang.Thread.State:")) {
                String[] stackDatas = splitDatum.split("\n");
                List<String> collect = new ArrayList<>();
                for (String stackData : stackDatas) {
                    if (stackData.trim().contains("at ")) {
                        String stack = formatStack(stackData).trim();
                        collect.add(stack);
                    }
                }
                if (collect.size() > 0) {
                    dataModel.update(collect, 1);
                }

            }
        }
    }

    public void getStack(List<RecordedEvent> recordedEvents, EventType eventType) {

        recordedEvents.stream().filter(recordedEvent -> Arrays.stream(eventType.getEventNames()).
                anyMatch(eventTypeName -> eventTypeName.equals(recordedEvent.getEventType().getName()))).forEach(recordedEvent -> {
            if (eventType.getValueField() == null) {
                getStackWithString(recordedEvent);
            } else {
                getStackWithFiled(recordedEvent, eventType);
            }
        });

    }

    public static String formatStack(String stackLine) {
        String methodData = stackLine.substring(stackLine.indexOf("at") + 3, stackLine.indexOf("("));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(methodData);
        String lineNumber = stackLine.substring(stackLine.lastIndexOf(":") + 1, stackLine.lastIndexOf(")"));
        stringBuilder.append(":");
        try {
            Integer integer = Integer.valueOf(lineNumber);
            stringBuilder.append(integer);
        } catch (Exception e) {
            stringBuilder.append("-1");
        }

        return stringBuilder.toString();
    }

    public static String formatStack(RecordedFrame recordedFrame) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(recordedFrame.getMethod().getType().getName());
        stringBuilder.append(".");
        stringBuilder.append(recordedFrame.getMethod().getName());
        stringBuilder.append(":");
        stringBuilder.append(recordedFrame.getLineNumber());
        return stringBuilder.toString();
    }

    public String parser(Path path, EventType[] eventType) {
        try (RecordingFile recordingFile = new RecordingFile(path)) {
            List<RecordedEvent> recordStream = getRecordStream(recordingFile);
            for (EventType type : eventType) {
                getStack(recordStream, type);
            }
            return dataModel.toDataString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
