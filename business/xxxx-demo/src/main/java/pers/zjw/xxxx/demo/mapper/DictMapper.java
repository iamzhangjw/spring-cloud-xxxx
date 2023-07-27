package pers.zjw.xxxx.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import pers.zjw.xxxx.demo.pojo.entity.Dict;
import pers.zjw.xxxx.demo.pojo.req.DictReq;
import pers.zjw.xxxx.demo.pojo.resp.DictResp;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 字典表 Mapper 接口
 * </p>
 *
 * @author zhangjw
 * @since 2023-05-22
 */
public interface DictMapper extends BaseMapper<Dict> {
    /**
     * 分页查询
     * @param page 分页对象,xml中可以从里面进行取值,传递参数 Page 即自动分页,必须放在第一位(你可以继承Page实现自己的分页对象)
     * @param req 查询条件
     * @return 分页结果
     */
    IPage<DictResp> page(Page<?> page, @Param("req") DictReq req);
}
