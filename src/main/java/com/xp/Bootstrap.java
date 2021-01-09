package com.xp;

import com.xp.data.model.FoldedDataModel;
import com.xp.jfr.parser.EventType;
import com.xp.jfr.parser.JfrParser;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;


public class Bootstrap {

    public static final String CPU = "cpu";
    public static final String TLAB = "allocation-tlab";
    public static final String OUT_TLAB = "allocation-outside-tlab";
    public static final String EXCEPTION = "exceptions";
    public static final String MONITOR = "monitor-blocked";
    public static final String IO_SOCK = "io-socket";


    public static Map<String, EventType> eventTypeMap = new HashMap<String, EventType>() {{
        put(CPU, EventType.METHOD_PROFILING_SAMPLE);
        put(TLAB, EventType.ALLOCATION_IN_NEW_TLAB);
        put(OUT_TLAB, EventType.ALLOCATION_OUTSIDE_TLAB);
        put(EXCEPTION, EventType.JAVA_EXCEPTION);
        put(MONITOR, EventType.JAVA_MONITOR_BLOCKED);
        put(IO_SOCK, EventType.IO);
    }};

    public static void main(String[] args) throws IOException {

        Options options = new Options();
        CommandLineParser commandLineParser = new DefaultParser();
        options.addOption("h", "usage help");
        options.addOption(Option.builder("d").longOpt("decompress").type(String.class).desc("Decompress the JFR file").build());
        options.addOption(Option.builder("f").longOpt("jfrdump").argName("jfr file path").hasArg(true).desc("Java Flight Recorder Dump").required(true).build());
        options.addOption(Option.builder("e").longOpt("event").argName("Type of event used to generate the flamegraph").hasArg(true).required(false).desc("[cpu, allocation-tlab, allocation-outside-tlab, exceptions, monitor-blocked, io-socket]").build());
        CommandLine commandLine;
        try {
            File file = null;
            EventType eventType = null;
            commandLine = commandLineParser.parse(options, args);
            if (commandLine.hasOption("f")) {
                file = new File(commandLine.getOptionValue("f"));
            }
            if (commandLine.hasOption("d")) {
                file = decompressFile(file);
            }
            if (commandLine.hasOption("e")) {
                String event = commandLine.getOptionValue("e");
                eventType = eventTypeMap.get(event);
                if (eventType == null) {
                    throw new ParseException("event is \n" +
                            "nonrecognition");
                }
            } else {
                eventType = EventType.JAVA_MONITOR_BLOCKED;
            }
            JfrParser jfrParser = new JfrParser(new FoldedDataModel());
            String parser = jfrParser.parser(file.toPath(), eventType);
            System.out.println(parser);

        } catch (ParseException e) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("jfr-flame-graph", options);
            System.exit(0);
        }

    }

    private static File decompressFile(final File compressedFile) throws IOException {
        byte[] buffer = new byte[8 * 1024];

        File decompressedFile;

        try (GZIPInputStream compressedStream = new GZIPInputStream(new FileInputStream(compressedFile));
             FileOutputStream uncompressedFileStream = new FileOutputStream(
                     decompressedFile = File.createTempFile("jfr_", null))) {

            decompressedFile.deleteOnExit();
            int numberOfBytes;

            while ((numberOfBytes = compressedStream.read(buffer)) > 0) {
                uncompressedFileStream.write(buffer, 0, numberOfBytes);
            }
        }

        return decompressedFile;
    }


}
