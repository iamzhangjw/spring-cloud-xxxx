package pers.zjw.xxxx.oauth.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthRoleApiResourceRelation;
import pers.zjw.xxxx.oauth.server.pojo.vo.ApiResource;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * <p>
 * 角色和api资源关系表 Mapper 接口
 * </p>
 *
 * @author zhangjw
 * @since 2023-01-04
 */
public interface AuthRoleApiResourceRelationMapper extends BaseMapper<AuthRoleApiResourceRelation> {
    Collection<ApiResource> listByRole(@Param("roleId") Long roleId);
    Collection<ApiResource> listByRoleCode(@Param("roleCode") String roleCode);
    Collection<ApiResource> listByUser(@Param("userId") Long userId);
    Collection<ApiResource> listByUsername(@Param("username") String username);
}
