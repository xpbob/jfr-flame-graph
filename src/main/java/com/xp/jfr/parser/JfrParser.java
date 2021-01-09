package com.xp.jfr.parser;

import com.google.gson.Gson;
import com.xp.data.model.DataModel;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.consumer.RecordedStackTrace;
import jdk.jfr.consumer.RecordingFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JfrParser {

    DataModel dataModel;

    public JfrParser(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public Stream<RecordedEvent> getRecordStream(Path path) throws IOException {
        RecordingFile recordingFile = new RecordingFile(path);
        if (recordingFile.hasMoreEvents()) {
            RecordedEvent recordedEvent = recordingFile.readEvent();
            return Stream.iterate(recordedEvent, recordedEvent1 -> recordingFile.hasMoreEvents(), recordedEvent1 -> {
                try {
                    return recordingFile.readEvent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return recordedEvent1;
            });
        } else {
            return Stream.empty();
        }

    }


    public void getStack(Stream<RecordedEvent> recordedEventStream, EventType eventType) {

        recordedEventStream.filter(recordedEvent -> Arrays.stream(eventType.getEventNames()).
                anyMatch(eventTypeName -> eventTypeName.equals(recordedEvent.getEventType().getName()))).forEach(recordedEvent -> {
            long dataValue = eventType.getValueField().getValue(recordedEvent);
            RecordedStackTrace stackTrace = recordedEvent.getStackTrace();
            if(stackTrace!=null){
                List<String> collect = stackTrace.getFrames().stream().map(JfrParser::formatStack).collect(Collectors.toList());
                dataModel.update(collect, (int) dataValue);
            }

        });
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

    public String parser(Path path, EventType eventType) throws IOException {
        Stream<RecordedEvent> recordStream = getRecordStream(path);
        getStack(recordStream, eventType);
        return dataModel.toDataString();
    }
}
