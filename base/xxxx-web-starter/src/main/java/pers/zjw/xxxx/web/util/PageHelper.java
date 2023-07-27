package pers.zjw.xxxx.web.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.zjw.xxxx.foundation.pojo.PageResult;

/**
 * PageHelper
 *
 * @author zhangjw
 * @date 2023/01/15 0015 11:07
 */
public class PageHelper {
    public static  <T> PageResult<T> assembly(IPage<T> page) {
        return PageResult.create(page.getTotal(), (int)page.getCurrent(), (int)page.getSize(), page.getRecords());
    }
}
