package pers.zjw.xxxx.oauth.server.service;


import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.zjw.xxxx.oauth.server.mapper.OauthRefreshTokenMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.OauthRefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * oauth客户refresh_token表 服务实现类
 * </p>
 *
 * @author zhangjw
 * @since 2022-12-26
 */
@Service
public class OauthRefreshTokenService extends ServiceImpl<OauthRefreshTokenMapper, OauthRefreshToken> {
    @Autowired
    private OauthRefreshTokenMapper mapper;

    public void save(String refreshToken, Integer expiresIn, Long userId) {
        long now = System.currentTimeMillis();
        OauthRefreshToken entity = new OauthRefreshToken();
        entity.setToken(refreshToken);
        entity.setTokenId(DigestUtil.md5Hex(refreshToken));
        entity.setExpiresIn(expiresIn);
        entity.setExpiresIn(expiresIn);
        entity.setCreateAt(now);
        entity.setCreateBy(userId);
        entity.setVersion(now);
        mapper.insert(entity);
    }

}
