package pers.zjw.xxxx.foundation.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * web 响应
 *
 * @date 2022/03/31 0031 14:04
 * @author zhangjw
 */
@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = -3340420546422679904L;
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private Collection<T> list;

    private PageResult(long total, int pageNum, int pageSize, Collection<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
    }

    private PageResult(long total, int pageSize, Collection<T> list) {
        this.total = total;
        this.pageSize = pageSize;
        this.list = list;
    }

    private PageResult(int pageSize, Collection<T> list) {
        this.pageSize = pageSize;
        this.list = list;
    }

    public static <T> PageResult<T> create(long total, int pageNum, int pageSize, Collection<T> list) {
        return new PageResult<>(total, pageNum, pageSize, list);
    }

    public static <T> PageResult<T> create(long total, int pageSize, Collection<T> list) {
        return new PageResult<>(total, pageSize, list);
    }

    public static <T> PageResult<T> create(int pageSize, Collection<T> list) {
        return new PageResult<>(pageSize, list);
    }
}
