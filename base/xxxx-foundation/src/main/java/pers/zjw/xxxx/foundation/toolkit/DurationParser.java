package pers.zjw.xxxx.foundation.toolkit;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * write some class description
 *
 * @author zhangjw
 * @date 2022/07/22 0022 19:03
 *
 * reference https://stackoverflow.com/questions/6403851/parsing-time-strings-like-1h-30min
 */
public final class DurationParser {
    private static final Pattern periodPattern = Pattern.compile("([0-9]+)([dhms])");

    public static Duration parse(String durationString){
        Duration duration = Duration.ZERO;
        Matcher matcher = periodPattern.matcher(durationString);
        while(matcher.find()){
            int num = Integer.parseInt(matcher.group(1));
            String typ = matcher.group(2);
            switch (typ) {
                case "d":
                    duration = duration.plus(Duration.ofDays(num));
                    break;
                case "h":
                    duration = duration.plus(Duration.ofHours(num));
                    break;
                case "m":
                    duration = duration.plus(Duration.ofMinutes(num));
                    break;
                case "s":
                    duration = duration.plus(Duration.ofSeconds(num));
                    break;
                default:
                    // ignore
            }
        }
        return duration;
    }
}
