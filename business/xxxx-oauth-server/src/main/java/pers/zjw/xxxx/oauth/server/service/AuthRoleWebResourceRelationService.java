package pers.zjw.xxxx.oauth.server.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.zjw.xxxx.oauth.server.mapper.AuthRoleWebResourceRelationMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthRoleWebResourceRelation;
import pers.zjw.xxxx.oauth.server.pojo.vo.WebResource;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色和web资源关系表 服务实现类
 * </p>
 *
 * @author zhangjw
 * @since 2023-01-04
 */
@Service
public class AuthRoleWebResourceRelationService extends ServiceImpl<AuthRoleWebResourceRelationMapper, AuthRoleWebResourceRelation> {
    @Autowired
    private AuthRoleWebResourceRelationMapper mapper;
    @Autowired
    private AuthRoleApiResourceRelationService roleApiResourceRelationService;
    @Autowired
    private AuthWebResourceService webResourceService;

    public void bind(Long roleId, Collection<Long> resourceIds, AuthenticatedUser user) {
        if (CollectionUtils.isEmpty(resourceIds)) return;
        long now = System.currentTimeMillis();
        Collection<AuthRoleWebResourceRelation> relations = resourceIds.stream().map(e -> {
            AuthRoleWebResourceRelation relation = new AuthRoleWebResourceRelation();
            relation.setRoleId(roleId);
            relation.setResourceId(e);
            relation.setCreateAt(now);
            relation.setCreateBy(user.getId());
            relation.setVersion(now);
            return relation;
        }).collect(Collectors.toSet());
        mapper.delete(Wrappers.lambdaQuery(AuthRoleWebResourceRelation.class)
                .eq(AuthRoleWebResourceRelation::getRoleId, roleId)
                .eq(AuthRoleWebResourceRelation::getDeleted, false)
                .lt(AuthRoleWebResourceRelation::getVersion, now));
        saveBatch(relations);
        roleApiResourceRelationService.bind(roleId, resourceIds, user);
    }

    public Collection<Long> listResourceIdsByRole(Long roleId) {
        return mapper.listResourceIdsByRole(roleId);
    }

    public Collection<Long> listResourceIdsByUser(Long userId) {
        return mapper.listResourceIdsByUser(userId);
    }

    public Set<WebResource> cascadeByRole(Long roleId) {
        return cascadeByRole(roleId, webResourceService.types());
    }

    public Collection<WebResource> cascadeByUser(Long userId) {
        return cascadeByUser(userId, webResourceService.types());
    }

    private Set<WebResource> cascadeByRole(Long roleId, Collection<String> types) {
        Collection<WebResource> all = listResourcesByRole(roleId, types);
        return cascade(all);
    }

    private Collection<WebResource> listResourcesByRole(Long roleId, Collection<String> types) {
        return mapper.listResourcesByRole(roleId, types);
    }

    private Set<WebResource> cascadeByUser(Long userId, Collection<String> types) {
        Collection<WebResource> all = listResourcesByUser(userId, types);
        return cascade(all);
    }

    private Collection<WebResource> listResourcesByUser(Long userId, Collection<String> types) {
        return mapper.listResourcesByUser(userId, types);
    }

    private Set<WebResource> cascade(Collection<WebResource> resources) {
        Set<WebResource> result = new TreeSet<>(Comparator.comparing(WebResource::getOrderNum));
        Map<Long, WebResource> map = resources.stream().collect(Collectors.toMap(WebResource::getId, r -> r));
        map.forEach((k,v) -> {
            WebResource parent = map.get(v.getParentId());
            if (null == parent) {
                result.add(v);
            } else {
                Set<WebResource> leaves = parent.getLeaves();
                if (null == leaves) {
                    leaves = new TreeSet<>(Comparator.comparing(WebResource::getOrderNum));
                    parent.setLeaves(leaves);
                }
                leaves.add(v);
            }
        });
        return result;
    }
}
