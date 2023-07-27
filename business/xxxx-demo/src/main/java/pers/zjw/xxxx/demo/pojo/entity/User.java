package pers.zjw.xxxx.demo.pojo.entity;

import pers.zjw.xxxx.mysql.CryptoAlgorithm;
import pers.zjw.xxxx.mysql.annotation.Crypto;
import pers.zjw.xxxx.mysql.annotation.Encryption;
import pers.zjw.xxxx.mysql.pojo.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author zhangjw
 * @since 2023-06-22
 */
@Crypto
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends GenericEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @Encryption
    private String name;

    /**
     * 电话
     */
    @Encryption(CryptoAlgorithm.AES)
    private String mobile;

    /**
     * 证件号码
     */
    @Encryption(CryptoAlgorithm.AES)
    private String certificationNo;

    /**
     * 邮箱地址
     */
    @Encryption(CryptoAlgorithm.AES)
    private String email;
}
