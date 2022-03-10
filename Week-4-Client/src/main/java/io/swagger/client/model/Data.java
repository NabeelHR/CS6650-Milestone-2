package io.swagger.client.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Data implements Comparable<Data> {

    @Getter
    private final long startTime;

    @Getter
    private final long endTime;

    @Getter
    private final long latency;

    @Getter
    private final String requestType;

    @Getter
    private final int responseCode;

    @Override
    public int compareTo(Data data) {
        return Integer.compare((int) this.getLatency(), (int) data.getLatency());
    }

    @Override
    public String toString() {
        return String.format("\nStart Time = %d, End Time = %d, Latency = %d, Request Type = %s, Response Code = %d",
                startTime, endTime, latency, requestType, responseCode);
    }
}
