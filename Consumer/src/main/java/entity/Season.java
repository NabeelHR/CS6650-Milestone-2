package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Season {

    @Getter
    private final int liftID;

    @Getter
    private final int time;

    @Getter
    private final int waitTime;

}
