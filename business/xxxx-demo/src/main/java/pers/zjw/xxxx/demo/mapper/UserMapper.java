package pers.zjw.xxxx.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import pers.zjw.xxxx.demo.pojo.entity.User;
import pers.zjw.xxxx.demo.pojo.req.UserQuery;
import pers.zjw.xxxx.demo.pojo.resp.UserResp;
import pers.zjw.xxxx.mysql.CryptoAlgorithm;
import pers.zjw.xxxx.mysql.annotation.Decryption;
import pers.zjw.xxxx.mysql.annotation.Encryption;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author zhangjw
 * @since 2023-06-22
 */
public interface UserMapper extends BaseMapper<User> {
    IPage<UserResp> page(Page<?> page, @Param("req") UserQuery req);
    IPage<UserResp> pageByName(Page<?> page, @Encryption @Param("name") String name);
    @Decryption(CryptoAlgorithm.AES)
    String getMobileByNameAndCertificationNo(@Encryption @Param("name") String name, @Encryption(CryptoAlgorithm.AES) @Param("certificationNo") String certificationNo);
    @Decryption(CryptoAlgorithm.AES)
    String getMobileByCertificationNo(@Encryption(CryptoAlgorithm.AES) @Param("certificationNo") String certificationNo);
    List<UserResp> listByIds(@Param("ids") Collection<Long> ids);
    List<UserResp> listByCondition(@Encryption Map<String, Object> map);
}
