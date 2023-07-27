package pers.zjw.xxxx.oauth.server.service;


import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.zjw.xxxx.oauth.server.mapper.OauthAccessTokenMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.OauthAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * oauth客户access_token表 服务实现类
 * </p>
 *
 * @author zhangjw
 * @since 2022-12-26
 */
@Service
public class OauthAccessTokenService extends ServiceImpl<OauthAccessTokenMapper, OauthAccessToken> {
    @Autowired
    private OauthAccessTokenMapper mapper;

    public void save(String accessToken, String refreshToken, String clientId, String username, int expiresIn, Long userId) {
        // 可以通过控制 access token 的有效性实现单用户在线
        long now = System.currentTimeMillis();
        OauthAccessToken entity = new OauthAccessToken();
        entity.setToken(accessToken);
        entity.setTokenId(DigestUtil.md5Hex(accessToken));
        entity.setUserName(username);
        entity.setClientId(clientId);
        entity.setRefreshToken(refreshToken);
        entity.setExpiresIn(expiresIn);
        entity.setCreateAt(now);
        entity.setCreateBy(userId);
        entity.setVersion(now);
        mapper.insert(entity);
    }
}
