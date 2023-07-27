package pers.zjw.xxxx.oauth.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthWebApiResourceRelation;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * <p>
 * web和api资源关系表 Mapper 接口
 * </p>
 *
 * @author zhangjw
 * @since 2023-01-04
 */
public interface AuthWebApiResourceRelationMapper extends BaseMapper<AuthWebApiResourceRelation> {
    Collection<Long> listApiResourceIdsByWebResources(@Param("ids") Collection<Long> webResourceIds);
}
