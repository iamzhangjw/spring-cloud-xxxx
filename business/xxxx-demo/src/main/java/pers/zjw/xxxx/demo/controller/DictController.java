package pers.zjw.xxxx.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import pers.zjw.xxxx.demo.pojo.req.DictReq;
import pers.zjw.xxxx.demo.pojo.resp.DictResp;
import pers.zjw.xxxx.demo.service.DictService;
import pers.zjw.xxxx.foundation.pojo.PageResult;
import pers.zjw.xxxx.oauth.client.context.CurrentUser;
import pers.zjw.xxxx.web.controller.BaseController;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import pers.zjw.xxxx.web.pojo.UnifiedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * dict controller
 *
 * @author zhangjw
 * @date 2023/05/22 0022 11:26
 */
@RequestMapping("/dict")
@RestController
public class DictController extends BaseController {
    @Autowired
    private DictService dictService;

    @GetMapping("/page")
    public PageResult<DictResp> page(UnifiedQuery query, DictReq req, @CurrentUser AuthenticatedUser user) {
        Page<DictResp> page = Page.of(query.getPageNum(), query.getPageSize());
        return dictService.page(page, req);
    }
}
