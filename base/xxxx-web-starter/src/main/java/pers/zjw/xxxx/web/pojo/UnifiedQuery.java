package pers.zjw.xxxx.web.pojo;

import lombok.Data;

/**
 * 统一的查询参数
 *
 * @author zhangjw
 * @date 2022/12/22 0022 14:27
 */
@Data
public class UnifiedQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
