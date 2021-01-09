# jfr support
jdk11+

# jdk support
jdk11+

# event support
- cpu
- allocation-tlab
- allocation-outside-tlab
- exceptions
- monitor-blocked
- io-socket


# Clone FlameGraph repository

Clone [Brendan]'s [FlameGraph] repository and set the environment variable `FLAMEGRAPH_DIR` to FlameGraph directory

[Brendan]: http://www.brendangregg.com/bio.html

```
git clone https://github.com/brendangregg/FlameGraph.git
export FLAMEGRAPH_DIR=/path/to/FlameGraph
```


# jfr-flame-graph usage
```
usage: jfr-flame-graph
 -d,--decompress                                              Decompress
                                                              the JFR file
 -e,--event <Type of event used to generate the flamegraph>   [cpu,
                                                              allocation-t
                                                              lab,
                                                              allocation-o
                                                              utside-tlab,
                                                              exceptions,
                                                              monitor-bloc
                                                              ked,
                                                              io-socket]
 -f,--jfrdump <jfr file path>                                 Java Flight
                                                              Recorder
                                                              Dump
 -h                                                           usage help

```
# How to generate a Flame Graph
```
java -jar jfr-flame-graph-1.0-SNAPSHOT-jar-with-dependencies.jar -f ~/flight_recording_16eaTest8490.jfr -e allocation-tlab |$FLAMEGRAPH_DIR/flamegraph.pl --title "Flame Graph" > flame.svg

```
