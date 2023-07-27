package pers.zjw.xxxx.mysql;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 密码算法
 *
 * @author zhangjw
 * @date 2023/01/15 0015 9:35
 */
public enum CryptoAlgorithm {
    /**
     * 可查询加密
     * BASE62 + caesar
     */
    QUERY{
        @Override
        public String decrypt(String cipherText, String secretKey) {
            if (StringUtils.isEmpty(cipherText) || StringUtils.isEmpty(secretKey)) return cipherText;
            StringBuilder decode = new StringBuilder();
            int offset = secretKey.hashCode() % 128;
            int offset4Num = offset%10, offset4Letter = offset%26;
            cipherText.chars().map(e -> {
                /**
                 * ASCII
                 * https://www.cs.cmu.edu/~pattis/15-1XX/common/handouts/ascii.html
                 */
                if (e >= '0' && e <= '9') {
                    return (e - '0' - offset4Num + 10) % 10 + '0';
                }
                if (e >= 'A' && e <= 'Z') {
                    return (e - 'A' - offset4Letter + 26) % 26 + 'A';
                }
                if (e >= 'a' && e <= 'z') {
                    return (e - 'a' - offset4Letter + 26) % 26 + 'a';
                }
                return (char)e;
            }).forEach(e -> decode.append((char) e));
            return HexUtil.decodeHexStr(decode.toString(), CharsetUtil.CHARSET_UTF_8);
        }

        @Override
        public String encrypt(String plainText, String secretKey) {
            if (StringUtils.isEmpty(plainText) || StringUtils.isEmpty(secretKey)) return plainText;
            String base62 = HexUtil.encodeHexStr(plainText, CharsetUtil.CHARSET_UTF_8);
            int offset = secretKey.hashCode() % 128;
            StringBuilder encode = new StringBuilder();
            base62.chars().map(e -> {
                if (e >= '0' && e <= '9') {
                    return (e - '0' + offset) % 10 + '0';
                }
                if (e >= 'A' && e <= 'Z') {
                    return (e - 'A' + offset) % 26 + 'A';
                }
                if (e >= 'a' && e <= 'z') {
                    return (e - 'a' + offset) % 26 + 'a';
                }
                return (char)e;
            }).forEach(e -> encode.append((char) e));
            return encode.toString();
        }
    },
    AES{
        @Override
        public String decrypt(String cipherText, String secretKey) {
            SymmetricCrypto aes = getCrypto(SymmetricAlgorithm.AES.getValue(), secretKey);
            return aes.decryptStr(cipherText, CharsetUtil.CHARSET_UTF_8);
        }

        @Override
        public String encrypt(String plainText, String secretKey) {
            SymmetricCrypto aes = getCrypto(SymmetricAlgorithm.AES.getValue(), secretKey);
            return aes.encryptHex(plainText);
        }
    },
    DES{
        @Override
        public String decrypt(String cipherText, String secretKey) {
            SymmetricCrypto aes = getCrypto(SymmetricAlgorithm.DES.getValue(), secretKey);
            return aes.decryptStr(cipherText, CharsetUtil.CHARSET_UTF_8);
        }

        @Override
        public String encrypt(String plainText, String secretKey) {
            SymmetricCrypto aes = getCrypto(SymmetricAlgorithm.DES.getValue(), secretKey);
            return aes.encryptHex(plainText);
        }
    },
    SM4{
        @Override
        public String decrypt(String cipherText, String secretKey) {
            SymmetricCrypto sm4 = getCrypto("SM4", secretKey);
            return sm4.decryptStr(cipherText, CharsetUtil.CHARSET_UTF_8);
        }

        @Override
        public String encrypt(String plainText, String secretKey) {
            SymmetricCrypto sm4 = getCrypto("SM4", secretKey);
            return sm4.encryptHex(plainText, CharsetUtil.CHARSET_UTF_8);
        }
    }
    ;

    public abstract String decrypt(String cipherText, String secretKey);
    public abstract String encrypt(String plainText, String secretKey);

    private static final Map<String, SymmetricCrypto> AES_MAP = new ConcurrentHashMap<>();
    private static final Map<String, SymmetricCrypto> DES_MAP = new ConcurrentHashMap<>();
    private static final Map<String, SymmetricCrypto> SM4_MAP = new ConcurrentHashMap<>();

    private static SymmetricCrypto getCrypto(String algorithm, String key) {
        if (SymmetricAlgorithm.AES.getValue().equals(algorithm)) {
            SymmetricCrypto crypto = AES_MAP.get(key);
            if (null == crypto) {
                SecureRandom random = new SecureRandom(key.getBytes(StandardCharsets.UTF_8));
                byte[] bytes = new byte[16];
                random.nextBytes(bytes);
                crypto = SecureUtil.aes(bytes);
                AES_MAP.putIfAbsent(key, crypto);
            }
            return crypto;
        } else if (SymmetricAlgorithm.DES.getValue().equals(algorithm)) {
            SymmetricCrypto crypto = DES_MAP.get(key);
            if (null == crypto) {
                SecureRandom random = new SecureRandom(key.getBytes(StandardCharsets.UTF_8));
                byte[] bytes = new byte[8];
                random.nextBytes(bytes);
                crypto = SecureUtil.des(bytes);
                DES_MAP.putIfAbsent(key, crypto);
            }
            return crypto;
        }
        else if ("SM4".equals(algorithm)) {
            SymmetricCrypto crypto = SM4_MAP.get(key);
            if (null == crypto) {
                SecureRandom random = new SecureRandom(key.getBytes(StandardCharsets.UTF_8));
                byte[] bytes = new byte[16];
                random.nextBytes(bytes);
                crypto = SecureUtil.aes(bytes);
                SM4_MAP.putIfAbsent(key, crypto);
            }
            return crypto;
        }
        throw new IllegalArgumentException("unsupported algorithm:" + algorithm);
    }
}
