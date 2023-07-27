package pers.zjw.xxxx.demo.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import pers.zjw.xxxx.demo.pojo.req.UserForm;
import pers.zjw.xxxx.demo.pojo.req.UserQuery;
import pers.zjw.xxxx.demo.pojo.resp.UserResp;
import pers.zjw.xxxx.demo.service.UserService;
import pers.zjw.xxxx.foundation.annotation.Desensitization;
import pers.zjw.xxxx.foundation.pojo.PageResult;
import pers.zjw.xxxx.oauth.client.context.CurrentUser;
import pers.zjw.xxxx.web.controller.BaseController;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import pers.zjw.xxxx.web.pojo.UnifiedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author zhangjw
 * @since 2023-06-22
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @PutMapping
    public Long add(@RequestBody UserForm form, @CurrentUser AuthenticatedUser user) {
        return userService.add(form);
    }

    @Desensitization
    @GetMapping("/page")
    public PageResult<UserResp> page(UnifiedQuery query, UserQuery req, @CurrentUser AuthenticatedUser user) {
        log.debug("get authenticatedUser:{}", user);
        Page<UserResp> page = Page.of(query.getPageNum(), query.getPageSize());
        return userService.page(page, req);
    }

    @Desensitization
    @GetMapping("/pageByName")
    public PageResult<UserResp> pageByName(UnifiedQuery query, @RequestParam(value = "name", required = false) String name, @CurrentUser AuthenticatedUser user) {
        Page<UserResp> page = Page.of(query.getPageNum(), query.getPageSize());
        return userService.page(page, name);
    }

    @GetMapping("/getMobileByNameAndCertificationNo")
    public String getMobileByNameAndCertificationNo(@RequestParam String name, @RequestParam String certificationNo) {
        return userService.getMobileByNameAndCertificationNo(name, certificationNo);
    }

    @GetMapping("/getMobileByCertificationNo")
    public String getMobileByCertificationNo(@RequestParam String certificationNo) {
        //userService.testCollection();
        return userService.getMobileByCertificationNo(certificationNo);
    }
}

