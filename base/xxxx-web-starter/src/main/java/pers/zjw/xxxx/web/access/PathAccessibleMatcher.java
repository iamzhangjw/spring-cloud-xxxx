package pers.zjw.xxxx.web.access;

import pers.zjw.xxxx.web.PathProperties;
import pers.zjw.xxxx.web.constant.AccessScope;
import pers.zjw.xxxx.web.pojo.PathStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 放开访问路径验证
 * TODO 改造为从配置中心读取解析
 * @author zhangjw
 * @date 2023/02/10 20:13
 */
@Component
public class PathAccessibleMatcher {
    @Value("${spring.application.name}")
    private String appName;
    private Map<AccessScope, Map<String, List<String>>> strategyMap;
    private List<String> ignorePaths;

    @Autowired
    void pathStrategies(PathProperties properties) {
        if (StringUtils.hasText(properties.getIgnorePaths())) {
            ignorePaths = Arrays.asList(properties.getIgnorePaths().split(","));
        } else {
            ignorePaths = Collections.emptyList();
        }
        strategyMap = new HashMap<>();
        if (CollectionUtils.isEmpty(properties.getStrategies())) return;
        for (PathStrategy strategy : properties.getStrategies()) {
            if (StringUtils.isEmpty(strategy.getServiceName()) || StringUtils.isEmpty(strategy.getPaths())) continue;
            Map<String, List<String>> serviceMap = strategyMap.computeIfAbsent(strategy.getScope(), v -> new HashMap<>());
            List<String> paths = serviceMap.computeIfAbsent(strategy.getServiceName(), v -> new LinkedList<>());
            paths.addAll(Arrays.stream(strategy.getPaths().split(",")).collect(Collectors.toList()));
        }
    }

    public AccessScope match(String serviceName, String uri) {
        if (AccessScope.PUBLIC.test(uri, ignorePaths)) return AccessScope.PUBLIC;
        for (Map.Entry<AccessScope, Map<String, List<String>>> entry : strategyMap.entrySet()) {
            AccessScope scope = entry.getKey();
            Map<String, List<String>> map = entry.getValue();
            if (CollectionUtils.isEmpty(map)) continue;
            Collection<String> paths = map.get(serviceName);
            if (CollectionUtils.isEmpty(paths)) continue;
            if (scope.test(uri, paths)) return scope;
        }
        return AccessScope.MORE_CHECK;
    }

    public AccessScope match(String uri) {
        return match(appName, uri);
    }

    public List<String> getPath(AccessScope scope, String service) {
        Map<String, List<String>> serviceMap = strategyMap.get(scope);
        if (CollectionUtils.isEmpty(serviceMap)) return Collections.emptyList();
        return serviceMap.getOrDefault(service, Collections.emptyList());
    }
}
