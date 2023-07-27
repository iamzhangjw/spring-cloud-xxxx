package pers.zjw.xxxx.foundation.constant;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * account level
 *
 * @author zhangjw
 * @date 2023/01/16 0016 12:37
 */
public enum AccountLevel {
    /**
     * 账户级别
     */
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    ;

    private int value;

    AccountLevel(int value) {
        this.value = value;
    }

    public final static Map<Integer, AccountLevel> all = Stream.of(values()).collect(
            Collectors.toMap(e -> e.value, e -> e));

    public int value() {
        return value;
    }

    public static Optional<AccountLevel> parse(int value) {
        return Optional.ofNullable(all.get(value));
    }
}
