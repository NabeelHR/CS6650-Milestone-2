package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class LiftRide {

  @Getter
  private final int liftId;

  @Getter
  private final int resortId;

  @Getter
  private final int seasonId;

  @Getter
  private  final int dayId;

  @Getter
  private final int time;

  @Getter
  private final int skierId;

  public int getSkierId() {
    return skierId;
  }

  public int getDayId() {
    return dayId;
  }

  public int getLiftId() {
    return liftId;
  }

  public int getResortId() {
    return resortId;
  }

  public int getSeasonId() {
    return seasonId;
  }

  public int getTime() {
    return time;
  }
}

