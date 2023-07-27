package pers.zjw.xxxx.oauth.server.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.zjw.xxxx.oauth.server.mapper.OauthClientDetailsMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.OauthClientDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * oauth客户端信息表 服务实现类
 * </p>
 *
 * @author zhangjw
 * @since 2022-12-26
 */
@Service
public class OauthClientDetailsService extends ServiceImpl<OauthClientDetailsMapper, OauthClientDetails> {
    @Autowired
    private OauthClientDetailsMapper mapper;

    public OauthClientDetails getByClientIdAndSecret(String clientId, String clientSecret) {
        return mapper.selectOne(Wrappers.lambdaQuery(OauthClientDetails.class)
                .eq(OauthClientDetails::getClientId, clientId)
                .eq(OauthClientDetails::getClientSecret, clientSecret)
                .eq(OauthClientDetails::getDeleted, false));
    }
}
