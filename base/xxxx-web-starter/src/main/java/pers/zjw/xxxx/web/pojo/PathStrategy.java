package pers.zjw.xxxx.web.pojo;

import pers.zjw.xxxx.web.constant.AccessScope;
import lombok.Getter;
import lombok.Setter;

/**
 * PathStrategy
 *
 * @author zhangjw
 * @date 2023/02/09 23:26
 */
public class PathStrategy {
    @Getter
    @Setter
    private String serviceName;
    @Getter
    @Setter
    private String paths;
    @Setter
    private String scope;

    public AccessScope getScope() {
        return AccessScope.parse(scope);
    }
}
