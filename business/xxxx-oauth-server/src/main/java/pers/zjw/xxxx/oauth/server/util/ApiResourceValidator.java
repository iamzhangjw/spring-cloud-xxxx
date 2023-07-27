package pers.zjw.xxxx.oauth.server.util;

import cn.hutool.core.lang.Assert;
import pers.zjw.xxxx.oauth.server.pojo.vo.ApiResource;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * api resource validator
 *
 * @author zhangjw
 * @date 2023/01/05 10:36
 */
public class ApiResourceValidator {
    private Map<String, List<String>> includes;
    private Map<String, List<String>> excludes;

    private ApiResourceValidator() {

    }

    public static ApiResourceValidator create(Collection<ApiResource> resources) {
        return create().includeResources(resources);
    }

    public static ApiResourceValidator create() {
        ApiResourceValidator validator = new ApiResourceValidator();
        validator.includes = new HashMap<>();
        return validator;
    }

    protected ApiResourceValidator includeResources(Collection<ApiResource> resources) {
        Assert.notEmpty(resources, "resources must not be empty");
        Map<String, List<String>> includes = this.includes;
        for (ApiResource resource : resources) {
            String key = resource.getService().toLowerCase() + resource.getUri().toLowerCase();
            List<String> methods = Arrays.asList(resource.getMethod().split(","));
            List<String> previousValue = includes.get(key);
            if (null == previousValue) {
                includes.put(key, new LinkedList<>(methods));
            } else {
                previousValue.addAll(methods);
            }
        }
        return this;
    }

    public ApiResourceValidator excludeResources(Collection<ApiResource> resources) {
        Assert.notEmpty(resources, "resources must not be empty");
        Map<String, List<String>> excludes = new HashMap<>();
        for (ApiResource resource : resources) {
            String key = resource.getService().toLowerCase() + resource.getUri().toLowerCase();
            List<String> methods = Arrays.asList(resource.getMethod().toUpperCase().split(","));
            List<String> previousValue = excludes.get(key);
            if (null == previousValue) {
                excludes.put(key, new LinkedList<>(methods));
            } else {
                previousValue.addAll(methods);
            }
        }
        this.excludes = excludes;
        return this;
    }

    public boolean validate(ApiResource resource) {
        return validate(resource.getService(), resource.getUri(), resource.getMethod());
    }

    public boolean validate(String service, String uri, String method) {
        String path = service.toLowerCase() + uri.toLowerCase();
        List<String> excludeMethods = CollectionUtils.isEmpty(excludes) ? null : this.excludes.get(path);
        if (null != excludeMethods && excludeMethods.contains(method.toUpperCase())) {
            return true;
        }
        List<String> includeMethods = this.includes.get(path);
        if (null != includeMethods && includeMethods.contains(method.toUpperCase())) {
            return true;
        }
        return false;
    }
}
