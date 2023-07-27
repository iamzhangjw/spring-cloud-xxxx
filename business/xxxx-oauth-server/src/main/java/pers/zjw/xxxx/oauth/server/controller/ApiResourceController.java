package pers.zjw.xxxx.oauth.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import pers.zjw.xxxx.foundation.pojo.PageResult;
import pers.zjw.xxxx.oauth.server.pojo.request.AuthApiResourceQuery;
import pers.zjw.xxxx.oauth.server.pojo.response.AuthApiResourceResp;
import pers.zjw.xxxx.oauth.server.service.AuthApiResourceService;
import pers.zjw.xxxx.web.pojo.UnifiedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ApiResourceController
 *
 * @author zhangjw
 * @date 2023/01/04 22:54
 */
@Slf4j
@RequestMapping("/resource/api")
@RestController("authApiResourceController")
public class ApiResourceController {
    @Autowired
    private AuthApiResourceService authApiResourceService;

    @GetMapping
    public PageResult<AuthApiResourceResp> page(UnifiedQuery query, AuthApiResourceQuery condition) {
        Page<AuthApiResourceResp> page = Page.of(query.getPageNum(), query.getPageSize());
        return authApiResourceService.page(page, condition);
    }
}
