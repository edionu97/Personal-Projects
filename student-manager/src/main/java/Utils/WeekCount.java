package Utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class WeekCount {

    public static long getWeekCount(){

        LocalDateTime d1 = LocalDateTime.of(2017,10,1,0,0);

        LocalDateTime d2 = LocalDateTime.now();

        return Math.min(Duration.between(d1,d2).toHours()/(24 * 7) - 1,14);
    }
}
