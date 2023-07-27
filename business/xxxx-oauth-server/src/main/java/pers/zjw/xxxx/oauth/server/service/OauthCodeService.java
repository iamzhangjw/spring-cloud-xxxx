package pers.zjw.xxxx.oauth.server.service;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.zjw.xxxx.oauth.server.constant.OauthErrorCode;
import pers.zjw.xxxx.oauth.server.constant.Scope;
import pers.zjw.xxxx.oauth.server.exception.OauthException;
import pers.zjw.xxxx.oauth.server.mapper.OauthCodeMapper;
import pers.zjw.xxxx.oauth.server.pojo.entity.OauthCode;
import pers.zjw.xxxx.web.constant.ConstLiteral;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * oauth授权码表 服务实现类
 * </p>
 *
 * @author zhangjw
 * @since 2022-12-26
 */
@Service
public class OauthCodeService extends ServiceImpl<OauthCodeMapper, OauthCode> {
    @Autowired
    private OauthCodeMapper mapper;

    public OauthCode consumeCode(String clientId, String code) {
        OauthCode oauthCode = mapper.selectOne(Wrappers.lambdaQuery(OauthCode.class)
                .eq(OauthCode::getApplyClientId, clientId)
                .eq(OauthCode::getCode, code)
                .eq(OauthCode::getDeleted, false));
        if (null == oauthCode) throw new OauthException(OauthErrorCode.INVALID_REQUEST, "code invalid");;
        long now = System.currentTimeMillis();
        if ((oauthCode.getCreateAt() + oauthCode.getExpiresIn() * 1000L) < now) throw new OauthException(OauthErrorCode.INVALID_REQUEST, "code expired");
        LambdaUpdateWrapper<OauthCode> wrapper = Wrappers.lambdaUpdate(OauthCode.class)
                .eq(OauthCode::getId, oauthCode.getId())
                .eq(OauthCode::getVersion, oauthCode.getVersion())
                .set(OauthCode::getModifyAt, now)
                .set(OauthCode::getModifyBy, ConstLiteral.Authentication.NO_ONE)
                .set(OauthCode::getDeleted, true)
                .set(OauthCode::getVersion, now);
        if (mapper.update(null, wrapper) > 0) {
            return oauthCode;
        }
        throw new OauthException(OauthErrorCode.INVALID_REQUEST, "code invalid");
    }

    public OauthCode produceCode(String applyClientId, String redirectUri, String clientId, Long userId, String username, Set<Scope> scope) {
        long now = System.currentTimeMillis();
        OauthCode code = new OauthCode();
        code.setApplyClientId(applyClientId);
        code.setRedirectUri(redirectUri);
        code.setClientId(clientId);
        code.setUserId(userId);
        code.setUsername(username);
        code.setScope(scope.stream().map(e -> e.name().toLowerCase()).collect(Collectors.joining(",")));
        code.setExpiresIn(600);
        code.setCreateAt(now);
        code.setCreateBy(userId);
        code.setVersion(now);
        code.setCode(RandomStringUtils.randomAlphabetic(6));
        mapper.insert(code);
        return code;
    }
}
