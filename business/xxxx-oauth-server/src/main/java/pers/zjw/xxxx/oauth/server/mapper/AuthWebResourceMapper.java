package pers.zjw.xxxx.oauth.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthWebResource;
import pers.zjw.xxxx.oauth.server.pojo.vo.WebResource;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * <p>
 * web资源表 Mapper 接口
 * </p>
 *
 * @author zhangjw
 * @since 2023-01-04
 */
public interface AuthWebResourceMapper extends BaseMapper<AuthWebResource> {
    WebResource getById(@Param("id") Long id);
    WebResource getByCode(@Param("code") String code);

    Collection<WebResource> leaves(@Param("path") String path, @Param("types") Collection<String> types);
}
