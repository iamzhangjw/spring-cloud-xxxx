package pers.zjw.xxxx.oauth.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.zjw.xxxx.foundation.pojo.PageResult;
import pers.zjw.xxxx.oauth.server.constant.HttpMethod;
import pers.zjw.xxxx.oauth.server.mapper.AuthApiResourceMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthApiResource;
import pers.zjw.xxxx.oauth.server.pojo.request.AuthApiResourceForm;
import pers.zjw.xxxx.oauth.server.pojo.request.AuthApiResourceQuery;
import pers.zjw.xxxx.oauth.server.pojo.vo.ApiResource;
import pers.zjw.xxxx.oauth.server.pojo.response.AuthApiResourceResp;
import pers.zjw.xxxx.web.constant.ErrorCode;
import pers.zjw.xxxx.web.exception.BizException;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import pers.zjw.xxxx.web.util.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <p>
 * api资源表 服务实现类
 * </p>
 *
 * @author zhangjw
 * @since 2023-01-04
 */
@Service
public class AuthApiResourceService extends ServiceImpl<AuthApiResourceMapper, AuthApiResource> {
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private AuthApiResourceMapper mapper;

    public Collection<String> listAllServices() {
        return discoveryClient.getServices();
    }

    public PageResult<AuthApiResourceResp> page(Page<AuthApiResourceResp> page, AuthApiResourceQuery condition) {
        return PageHelper.assembly(mapper.page(page, condition));
    }

    public Collection<ApiResource> allExcludes() {
        return mapper.allExcludes();
    }

    public void save(AuthApiResourceForm data, AuthenticatedUser user) {
        if (StringUtils.hasText(data.getMethod())) {
            String validMethod = Arrays.stream(data.getMethod().split(","))
                    .filter(e -> HttpMethod.parse(e.toLowerCase()).isPresent())
                    .collect(Collectors.joining(","));
            if (StringUtils.isEmpty(validMethod)) {
                throw new BizException(ErrorCode.PARAM_CHECK_FAILED, "method 非法");
            }
            data.setMethod(validMethod);
        }
        AuthApiResource entity = new AuthApiResource();
        entity.setName(data.getName());
        entity.setService(data.getService());
        entity.setUri(data.getUri());
        entity.setMethod(data.getMethod());
        entity.setExclude(null != data.getExclude() && data.getExclude());
        long now = System.currentTimeMillis();
        entity.setVersion(now);
        if (null == data.getId()) {
            entity.setCreateAt(now);
            entity.setCreateBy(user.getId());
            mapper.insert(entity);
        } else {
            entity.setModifyAt(now);
            entity.setModifyBy(user.getId());
            entity.setId(data.getId());
            mapper.updateById(entity);
        }
    }
}
