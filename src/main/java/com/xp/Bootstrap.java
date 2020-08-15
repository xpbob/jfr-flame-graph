package com.xp;

import org.apache.commons.cli.*;


public class Bootstrap {


    public static void main(String[] args) {

        Options options = new Options();
        CommandLineParser commandLineParser = new DefaultParser();
        options.addOption("h", "usage help");
        options.addOption(Option.builder("d").longOpt("decompress").type(String.class).desc("Decompress the JFR file").build());
        options.addOption(Option.builder("f").longOpt("jfrdump").argName("jfr file path").hasArg(true).desc("Java Flight Recorder Dump").required(true).build());
        options.addOption(Option.builder("e").longOpt("event").argName("Type of event used to generate the flamegraph").hasArg(true).required(false).desc("[cpu, allocation-tlab, allocation-outside-tlab, exceptions, monitor-blocked, io]").build());
        CommandLine commandLine;
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("jfr-flame-graph", options);
            System.exit(0);
        }

    }


}
