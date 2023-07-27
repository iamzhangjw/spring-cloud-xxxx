package pers.zjw.xxxx.oauth.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthApiResource;
import pers.zjw.xxxx.oauth.server.pojo.request.AuthApiResourceQuery;
import pers.zjw.xxxx.oauth.server.pojo.vo.ApiResource;
import pers.zjw.xxxx.oauth.server.pojo.response.AuthApiResourceResp;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * <p>
 * api资源表 Mapper 接口
 * </p>
 *
 * @author zhangjw
 * @since 2023-01-04
 */
public interface AuthApiResourceMapper extends BaseMapper<AuthApiResource> {
    IPage<AuthApiResourceResp> page(Page<?> page, @Param("condition") AuthApiResourceQuery condition);

    Collection<ApiResource> allExcludes();
}
