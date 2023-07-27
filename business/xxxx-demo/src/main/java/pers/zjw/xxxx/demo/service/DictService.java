package pers.zjw.xxxx.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.zjw.xxxx.demo.mapper.DictMapper;
import pers.zjw.xxxx.demo.pojo.entity.Dict;
import pers.zjw.xxxx.demo.pojo.req.DictReq;
import pers.zjw.xxxx.demo.pojo.resp.DictResp;
import pers.zjw.xxxx.foundation.pojo.PageResult;
import pers.zjw.xxxx.web.util.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @author zhangjw
 * @since 2023-05-22
 */
@Service
public class DictService extends ServiceImpl<DictMapper, Dict> {
    @Autowired
    private DictMapper mapper;

    public PageResult<DictResp> page(Page<DictResp> page, DictReq req) {
        // 不进行 count sql 优化，解决 mbp 无法自动优化 SQL 问题，这时候你需要自己查询 count 部分
        // page.setOptimizeCountSql(false);
        /**
         * 当 total 为小于 0 或者设置 setSearchCount(false) 分页插件不会进行 count 查询
         * 要点!! 分页返回的对象与传入的对象是同一个
         */
        return PageHelper.assembly(mapper.page(page, req));
    }
}
