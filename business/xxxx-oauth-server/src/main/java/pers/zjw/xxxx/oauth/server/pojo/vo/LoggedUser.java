package pers.zjw.xxxx.oauth.server.pojo.vo;

import pers.zjw.xxxx.web.pojo.AccessToken;
import lombok.Data;

import java.util.Collection;

/**
 * 登录成功的用户
 *
 * @author zhangjw
 * @date 2023/01/06 23:23
 */
@Data
public class LoggedUser {
    private Long id;
    private String name;
    private String nickname;
    private AccessToken token;
    private Collection<WebResource> permissions;
}
