package pers.zjw.xxxx.oauth.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthRole;
import pers.zjw.xxxx.oauth.server.pojo.request.AuthRoleQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author zhangjw
 * @since 2023-01-04
 */
public interface AuthRoleMapper extends BaseMapper<AuthRole> {
    IPage<AuthRole> page(Page<?> page, @Param("condition") AuthRoleQuery condition);

    Collection<AuthRole> getByUsername(@Param("username") String username);
}
