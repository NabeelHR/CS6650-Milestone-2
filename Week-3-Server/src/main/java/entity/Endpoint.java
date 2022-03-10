package entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Endpoint {
    private final String URL;
    private final String operation;
    private final int mean;
    private final int max;
}
