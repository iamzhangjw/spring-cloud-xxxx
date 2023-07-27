package pers.zjw.xxxx.oauth.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthRoleWebResourceRelation;
import pers.zjw.xxxx.oauth.server.pojo.vo.WebResource;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * <p>
 * 角色和web资源关系表 Mapper 接口
 * </p>
 *
 * @author zhangjw
 * @since 2023-01-04
 */
public interface AuthRoleWebResourceRelationMapper extends BaseMapper<AuthRoleWebResourceRelation> {
    Collection<Long> listResourceIdsByRole(@Param("roleId") Long roleId);
    Collection<Long> listResourceIdsByUser(@Param("userId") Long userId);

    Collection<WebResource> listResourcesByRole(@Param("roleId") Long roleId, @Param("types") Collection<String> types);
    Collection<WebResource> listResourcesByUser(@Param("userId") Long roleId, @Param("types") Collection<String> types);
}
