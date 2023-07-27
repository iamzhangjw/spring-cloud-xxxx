package pers.zjw.xxxx.oauth.server.authentication;

import pers.zjw.xxxx.redis.cache.LayeringCacheManager;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

/**
 * AuthenticatorManager
 *
 * @author zhangjw
 * @date 2023/01/06 19:02
 */
@Component
public class AuthenticatorManager implements Authenticator {
    @Autowired
    private LayeringCacheManager cacheManager;

    private AbstractAuthenticator first;

    @Autowired
    void authenticators(List<AbstractAuthenticator> authenticators) {
        TreeSet<AbstractAuthenticator> set = new TreeSet<>((o1, o2) -> {
            Order order1 = AnnotationUtils.findAnnotation(o1.getClass(), Order.class);
            Order order2 = AnnotationUtils.findAnnotation(o2.getClass(), Order.class);
            if (null == order2 && null == order1) return 0;
            if (null == order1) return 1;
            if (null == order2) return -1;
            if (order1.value() == order2.value()) return 0;
            return (order1.value() > order2.value()) ? -1 : 1;
        });
        set.addAll(authenticators);
        first = Objects.requireNonNull(set.pollFirst());
        AbstractAuthenticator last = first;
        for (AbstractAuthenticator authenticator : set) {
            last.setNext(authenticator);
            last = authenticator;
        }
    }

    @Override
    public AuthenticatedUser authenticate(Map<String, String> params) {
        return first.authenticate(params);
    }
}
