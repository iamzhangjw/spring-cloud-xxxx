package pers.zjw.xxxx.oauth.server.constant;

import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * http method enum
 *
 * @author zhangjw
 * @date 2022/12/26 0026 14:53
 */
public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    ;

    public final static Map<String, HttpMethod> all = Stream.of(values()).collect(
            Collectors.toMap(e -> e.name().toLowerCase(), e -> e));

    public static Optional<HttpMethod> parse(String key) {
        if (StringUtils.hasText(key)) return Optional.ofNullable(all.get(key.toLowerCase()));
        return Optional.empty();
    }
}
