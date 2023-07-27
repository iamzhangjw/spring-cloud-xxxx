package pers.zjw.xxxx.oauth.server.constant;

import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Web Resource Type enum
 *
 * @author zhangjw
 * @date 2023/01/06 15:05
 */
public enum ResourceType {
    MODULE,
    MENU,
    BUTTON,
    ELEMENT,
    ;

    public final static Map<String, ResourceType> all = Stream.of(values()).collect(
            Collectors.toMap(e -> e.name().toLowerCase(), e -> e));

    public static Optional<ResourceType> parse(String key) {
        if (StringUtils.hasText(key)) return Optional.ofNullable(all.get(key.toLowerCase()));
        return Optional.empty();
    }
}
