package pers.zjw.xxxx.oauth.server.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.zjw.xxxx.oauth.server.mapper.AuthUserRoleRelationMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthUserRoleRelation;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户和角色关系表 服务实现类
 * </p>
 *
 * @author zhangjw
 * @since 2023-01-04
 */
@Service
public class AuthUserRoleRelationService extends ServiceImpl<AuthUserRoleRelationMapper, AuthUserRoleRelation> {
    @Autowired
    private AuthUserRoleRelationMapper mapper;

    public void bind(Long userId, Collection<Long> roleIds, AuthenticatedUser user) {
        if (CollectionUtils.isEmpty(roleIds)) return;
        long now = System.currentTimeMillis();
        Collection<AuthUserRoleRelation> relations = roleIds.stream().map(e -> {
            AuthUserRoleRelation relation = new AuthUserRoleRelation();
            relation.setRoleId(e);
            relation.setUserId(userId);
            relation.setCreateAt(now);
            relation.setCreateBy(user.getId());
            relation.setVersion(now);
            return relation;
        }).collect(Collectors.toSet());
        mapper.delete(Wrappers.lambdaQuery(AuthUserRoleRelation.class)
                .eq(AuthUserRoleRelation::getUserId, userId)
                .eq(AuthUserRoleRelation::getDeleted, false)
                .lt(AuthUserRoleRelation::getVersion, now));
        saveBatch(relations);
    }
}
