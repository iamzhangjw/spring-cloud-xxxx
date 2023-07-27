package pers.zjw.xxxx.demo.pojo.req;

import pers.zjw.xxxx.mysql.CryptoAlgorithm;
import pers.zjw.xxxx.mysql.annotation.Crypto;
import pers.zjw.xxxx.mysql.annotation.Encryption;
import lombok.Data;

/**
 * UserQuery
 *
 * @author zhangjw
 * @date 2022/10/22 0022 15:37
 */
@Crypto
@Data
public class UserQuery {
    @Encryption
    private String name;
    @Encryption(CryptoAlgorithm.AES)
    private String mobile;
}
