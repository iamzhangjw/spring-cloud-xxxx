package pers.zjw.xxxx.oauth.server.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.zjw.xxxx.oauth.server.mapper.AuthUserMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author zhangjw
 * @since 2022-12-27
 */
@Service
public class AuthUserService extends ServiceImpl<AuthUserMapper, AuthUser> {
    @Autowired
    private AuthUserMapper mapper;

    @Cacheable(cacheNames = "loadByUsername", key = "#username")
    public AuthUser loadByUsername(String username) {
        return mapper.selectOne(Wrappers.lambdaQuery(AuthUser.class)
                .eq(AuthUser::getName, username)
                .eq(AuthUser::getEnabled, true)
                .eq(AuthUser::getLocked, false)
                .eq(AuthUser::getDeleted, false));
    }

    public void loginSuccess(Long id, long time, Long userId) {
        AuthUser entity = new AuthUser();
        entity.setId(id);
        entity.setLastLoginTime(time);
        entity.setModifyAt(time);
        entity.setModifyBy(userId);
        entity.setVersion(time);
        mapper.updateById(entity);
    }
}
