package pers.zjw.xxxx.oauth.server.constant;

import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * scope enum
 *
 * @author zhangjw
 * @date 2022/12/26 0026 14:53
 */
public enum Scope {
    /**
     * oauth scope
     */
    READ("read"){
        @Override
        public boolean allow(String httpMethod) {
            return StringUtils.hasText(httpMethod) && ("GET".equalsIgnoreCase(httpMethod)
                    || "HEAD".equalsIgnoreCase(httpMethod)
                    || "OPTIONS".equalsIgnoreCase(httpMethod)
                    || "CONNECT ".equalsIgnoreCase(httpMethod));
        }
    },
    WRITE("write"){
        @Override
        public boolean allow(String httpMethod) {
            return StringUtils.hasText(httpMethod) && ("POST".equalsIgnoreCase(httpMethod)
                    || "PUT".equalsIgnoreCase(httpMethod)
                    || "PATCH".equalsIgnoreCase(httpMethod));
        }
    },
    DELETE("delete"){
        @Override
        public boolean allow(String httpMethod) {
            return StringUtils.hasText(httpMethod) && ("DELETE".equalsIgnoreCase(httpMethod));
        }
    },
    ALL("all"){
        @Override
        public boolean allow(String httpMethod) {
            return true;
        }
    },
    ;

    private String value;

    Scope(String value) {
        this.value = value;
    }

    public final static Map<String, Scope> all = Stream.of(values()).collect(
            Collectors.toMap(e -> e.name().toLowerCase(), e -> e));

    public String value() {
        return value;
    }

    public static Optional<Scope> parse(String key) {
        if (StringUtils.hasText(key)) return Optional.ofNullable(all.get(key.toLowerCase()));
        return Optional.empty();
    }

    public abstract boolean allow(String httpMethod);
}
