package pers.zjw.xxxx.web.constant;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * AccessScope
 *
 * @author zhangjw
 * @date 2023/02/10 23:30
 */
public enum AccessScope {
    PUBLIC,
    AUTHENTICATED,
    MORE_CHECK,
    ;

    private static final AntPathMatcher matcher = new AntPathMatcher();

    public final static Map<String, AccessScope> all = Stream.of(values()).collect(
            Collectors.toMap(e -> e.name().toLowerCase(), e -> e));

    public static AccessScope parse(String key) {
        return Objects.requireNonNull(all.get(Objects.requireNonNull(key).toLowerCase()));
    }

    public boolean test(String path, Collection<String> regularPaths) {
        if (StringUtils.isEmpty(path) || CollectionUtils.isEmpty(regularPaths)) return false;
        return regularPaths.stream().anyMatch(r -> matcher.match(r, path));
    }
}
