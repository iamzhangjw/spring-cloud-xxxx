package pers.zjw.xxxx.demo.pojo.resp;

import pers.zjw.xxxx.foundation.annotation.SensitiveField;
import pers.zjw.xxxx.foundation.constant.FieldType;
import pers.zjw.xxxx.mysql.CryptoAlgorithm;
import pers.zjw.xxxx.mysql.annotation.Crypto;
import pers.zjw.xxxx.mysql.annotation.Decryption;
import lombok.Data;

import java.io.Serializable;

/**
 * UserForm
 *
 * @author zhangjw
 * @date 2022/10/22 0022 15:34
 */
@Crypto
@Data
public class UserResp implements Serializable {
    private static final long serialVersionUID = -1274406743800830335L;
    @Decryption
    @SensitiveField(FieldType.REAL_NAME)
    private String name;

    @Decryption(CryptoAlgorithm.AES)
    @SensitiveField(FieldType.MOBILE)
    private String mobile;

    @Decryption(CryptoAlgorithm.AES)
    @SensitiveField(FieldType.ID_NUM)
    private String certificationNo;

    @Decryption(CryptoAlgorithm.AES)
    @SensitiveField(FieldType.EMAIL)
    private String email;
}
