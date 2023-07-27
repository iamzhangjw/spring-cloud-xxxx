package pers.zjw.xxxx.demo.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.zjw.xxxx.demo.mapper.UserMapper;
import pers.zjw.xxxx.demo.pojo.entity.User;
import pers.zjw.xxxx.demo.pojo.req.UserForm;
import pers.zjw.xxxx.demo.pojo.req.UserQuery;
import pers.zjw.xxxx.demo.pojo.resp.UserResp;
import pers.zjw.xxxx.foundation.pojo.PageResult;
import pers.zjw.xxxx.web.util.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author zhangjw
 * @since 2023-06-22
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    @Autowired
    private UserMapper mapper;

    @CacheEvict(cacheNames = {"listUser"})
    @CachePut(cacheNames = {"getUser"}, key = "#form.certificationNo")
    public Long add(UserForm form) {
        User user = BeanUtil.toBean(form, User.class, CopyOptions.create());
        long now = System.currentTimeMillis();
        user.setCreateAt(now);
        user.setCreateBy(0L);
        user.setVersion(now);
        mapper.insert(user);
        return user.getId();
    }

    @Cacheable(value = "listUser", key = "#page.current+'_'+#page.size+'_'+#req?.name+'_'+#req?.mobile")
    public PageResult<UserResp> page(Page<UserResp> page, UserQuery req) {
        return PageHelper.assembly(mapper.page(page, req));
    }

    @Cacheable(value = "listUser", key = "#page.current+'_'+#page.size+'_'+#name")
    public PageResult<UserResp> page(Page<UserResp> page, String name) {
        return PageHelper.assembly(mapper.pageByName(page, name));
    }

    public String getMobileByNameAndCertificationNo(String name, String certificationNo) {
        return mapper.getMobileByNameAndCertificationNo(name, certificationNo);
    }

    @Cacheable(value = "getUser", key = "#certificationNo")
    public String getMobileByCertificationNo(String certificationNo) {
        return mapper.getMobileByCertificationNo(certificationNo);
    }

    public String testCollection() {
        mapper.listByIds(Collections.singletonList(6L));
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "刘德华");
        mapper.listByCondition(map);
        return "success";
    }
}
