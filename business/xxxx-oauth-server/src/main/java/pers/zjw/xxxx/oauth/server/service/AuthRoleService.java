package pers.zjw.xxxx.oauth.server.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.zjw.xxxx.foundation.pojo.PageResult;
import pers.zjw.xxxx.oauth.server.mapper.AuthRoleMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthRole;
import pers.zjw.xxxx.oauth.server.pojo.request.AuthRoleForm;
import pers.zjw.xxxx.oauth.server.pojo.request.AuthRoleQuery;
import pers.zjw.xxxx.web.constant.ErrorCode;
import pers.zjw.xxxx.web.exception.BizException;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import pers.zjw.xxxx.web.util.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author zhangjw
 * @since 2023-01-04
 */
@Service
public class AuthRoleService extends ServiceImpl<AuthRoleMapper, AuthRole> {
    @Autowired
    private AuthRoleMapper mapper;

    public PageResult<AuthRole> page(Page<AuthRole> page, AuthRoleQuery condition) {
        return PageHelper.assembly(mapper.page(page, condition));
    }

    public Collection<AuthRole> getByUsername(String username) {
        return mapper.getByUsername(username);
    }

    public void save(AuthRoleForm data, AuthenticatedUser user) {
        AuthRole entity = new AuthRole();
        entity.setName(data.getName());
        entity.setEnabled(data.getEnabled());
        entity.setDescription(data.getDescription());
        long now = System.currentTimeMillis();
        entity.setVersion(now);
        if (null == data.getId()) {
            if (exists(data.getCode())) throw new BizException(ErrorCode.DATA_DUPLICATION, "code重复");
            entity.setCode(data.getCode());
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

    private boolean exists(String code) {
        return mapper.selectCount(Wrappers.lambdaQuery(AuthRole.class).eq(AuthRole::getCode, code)) > 0;
    }
}
