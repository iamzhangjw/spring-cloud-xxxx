package pers.zjw.xxxx.oauth.server.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.zjw.xxxx.oauth.server.mapper.AuthWebApiResourceRelationMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthWebApiResourceRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * <p>
 * web和api资源关系表 服务实现类
 * </p>
 *
 * @author zhangjw
 * @since 2023-01-04
 */
@Service
public class AuthWebApiResourceRelationService extends ServiceImpl<AuthWebApiResourceRelationMapper, AuthWebApiResourceRelation> {
    @Autowired
    private AuthWebApiResourceRelationMapper mapper;

    public Collection<Long> listApiResourceIdsByWebResources(Collection<Long> webResourceIds) {
        return mapper.listApiResourceIdsByWebResources(webResourceIds);
    }
}
