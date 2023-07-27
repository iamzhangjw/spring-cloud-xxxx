package pers.zjw.xxxx.oauth.server.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.zjw.xxxx.oauth.server.mapper.AuthRoleApiResourceRelationMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthRole;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthRoleApiResourceRelation;
import pers.zjw.xxxx.oauth.server.pojo.vo.ApiResource;
import pers.zjw.xxxx.redis.cache.CacheHelper;
import pers.zjw.xxxx.web.constant.ErrorCode;
import pers.zjw.xxxx.web.exception.BizException;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色和api资源关系表 服务实现类
 * </p>
 *
 * @author zhangjw
 * @since 2023-01-04
 */
@Service
public class AuthRoleApiResourceRelationService extends ServiceImpl<AuthRoleApiResourceRelationMapper, AuthRoleApiResourceRelation> {
    @Autowired
    private AuthRoleApiResourceRelationMapper mapper;
    @Autowired
    private AuthWebApiResourceRelationService webApiResourceRelationService;
    @Autowired
    private AuthRoleService roleService;
    @Autowired
    private CacheHelper cacheHelper;

    public void bind(Long roleId, Collection<Long> webResources, AuthenticatedUser user) {
        if (CollectionUtils.isEmpty(webResources)) return;
        AuthRole role = roleService.getById(roleId);
        if (null == role) throw new BizException(ErrorCode.DATA_NOT_FOUND, "找不到角色");
        Collection<Long> apiResourceIds = webApiResourceRelationService.listApiResourceIdsByWebResources(webResources);
        if (CollectionUtils.isEmpty(apiResourceIds)) return;
        long now = System.currentTimeMillis();
        Collection<AuthRoleApiResourceRelation> relations = apiResourceIds.stream().map(e -> {
            AuthRoleApiResourceRelation relation = new AuthRoleApiResourceRelation();
            relation.setRoleId(roleId);
            relation.setResourceId(e);
            relation.setCreateAt(now);
            relation.setCreateBy(user.getId());
            relation.setVersion(now);
            return relation;
        }).collect(Collectors.toSet());
        mapper.delete(Wrappers.lambdaQuery(AuthRoleApiResourceRelation.class)
                .eq(AuthRoleApiResourceRelation::getRoleId, roleId)
                .eq(AuthRoleApiResourceRelation::getDeleted, false)
                .lt(AuthRoleApiResourceRelation::getVersion, now));
        saveBatch(relations);
        cacheHelper.evict(AuthRoleApiResourceRelationService.class, "listByRoleCode", role.getCode());
        cacheHelper.evict(AuthRoleApiResourceRelationService.class,"listByUsername", user.getName());
    }

    public Collection<ApiResource> listByRole(Long roleId) {
        return mapper.listByRole(roleId);
    }

    @Cacheable(cacheNames = "listByRoleCode", key = "#roleCode")
    public Collection<ApiResource> listByRoleCode(String roleCode) {
        return mapper.listByRoleCode(roleCode);
    }

    public Collection<ApiResource> listByUser(Long userId) {
        return mapper.listByUser(userId);
    }

    @Cacheable(cacheNames = "listByUsername", key = "#username")
    public Collection<ApiResource> listByUsername(String username) {
        return mapper.listByUsername(username);
    }
}
