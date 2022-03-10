package entity;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Statistic {
    private final List<Endpoint> endpointStats;
}
