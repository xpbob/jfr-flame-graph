package com.xp.jfr.parser;

import jdk.jfr.consumer.RecordedEvent;

public enum ValueField {
    COUNT {
        @Override
        public long getValue(RecordedEvent event) {
            return 1;
        }
    },
    DURATION {
        @Override
        public long getValue(RecordedEvent event) {
            return event.getDuration().toMillis();
        }
    },
    ALLOCATION_SIZE {
        @Override
        public long getValue(RecordedEvent event) {
            return (long) event.getValue("allocationSize") / 1000;
        }
    },
    TLAB_SIZE {
        @Override
        public long getValue(RecordedEvent event) {
            return (long) event.getValue("tlabSize") / 1000;
        }
    };

    public abstract long getValue(RecordedEvent event);
}