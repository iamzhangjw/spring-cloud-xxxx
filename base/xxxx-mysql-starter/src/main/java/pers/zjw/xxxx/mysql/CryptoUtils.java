package pers.zjw.xxxx.mysql;

import cn.hutool.core.util.HexUtil;
import pers.zjw.xxxx.mysql.annotation.Crypto;
import pers.zjw.xxxx.mysql.annotation.Decryption;
import pers.zjw.xxxx.mysql.annotation.Encryption;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CryptoUtils
 *
 * @author zhangjw
 * @date 2023/01/15 0015 11:21
 */
public final class CryptoUtils {

    public static void encrypt(Map<?, ?> map, String secretKey) throws IllegalAccessException {
        if (CollectionUtils.isEmpty(map)) return;
        for (Object value : map.values()) {
            if (value instanceof String) continue;
            encrypt(value, secretKey);
        }
    }

    public static Map<?, String> encrypt(Map<?, String> map, CryptoAlgorithm algorithm, String secretKey) {
        if (CollectionUtils.isEmpty(map) || null == algorithm) return map;
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> encrypt(e.getValue(), algorithm, secretKey)));
    }

    public static void encrypt(Object[] array, String secretKey) throws IllegalAccessException {
        if (Objects.isNull(array) || array.length == 0 || ClassUtils.isPrimitiveArray(array.getClass())) return;
        for (Object element : array) {
            if (element instanceof String) continue;
            encrypt(element, secretKey);
        }
    }

    public static String[] encrypt(String[] array, CryptoAlgorithm algorithm, String secretKey) {
        if (Objects.isNull(array) || array.length == 0 || null == algorithm) return array;
        for (int i=0; i<array.length; i++) {
            array[i] = encrypt(array[i], algorithm, secretKey);
        }
        return array;
    }

    public static void encrypt(Collection<?> collection, String secretKey) throws IllegalAccessException {
        if (CollectionUtils.isEmpty(collection)) return;
        for (Object element : collection) {
            if (element instanceof String) continue;
            encrypt(element, secretKey);
        }
    }

    public static Collection<String> encrypt(Collection<String> collection, CryptoAlgorithm algorithm, String secretKey) {
        if (CollectionUtils.isEmpty(collection) || null == algorithm) return collection;
        return collection.stream().map(e -> encrypt(e, algorithm, secretKey)).collect(Collectors.toList());
    }

    public static void encrypt(Object plain, String secretKey) throws IllegalAccessException {
        if (Objects.isNull(plain)) return;
        if (plain instanceof String || plain instanceof Date || plain instanceof LocalDate
                || plain instanceof LocalDateTime || ClassUtils.isPrimitiveOrWrapper(plain.getClass())
                || plain instanceof Collection || plain instanceof Map || plain.getClass().isArray()) return;
        Field[] fields = plain.getClass().getDeclaredFields();
        for (Field field : fields) {
            // ignore final field
            if (Modifier.isFinal(field.getModifiers())) continue;
            field.setAccessible(true);
            Object fieldValue = field.get(plain);
            if (Objects.isNull(fieldValue)) continue;
            CryptoAlgorithm algorithm = getEncryptionAlgorithm(field);

            if (field.getType() == String.class) {
                field.set(plain, encrypt((String)fieldValue, algorithm, secretKey));
            } else {
                if (fieldValue instanceof Map) {
                    ParameterizedType pt = (ParameterizedType)field.getGenericType();
                    // the type of map's value is string
                    if (pt.getActualTypeArguments()[1].getTypeName().equals(String.class.getName())) {
                        if (null == algorithm) continue;
                        field.set(plain, encrypt((Map<?, String>)fieldValue, algorithm, secretKey));
                    } else {
                        encrypt((Map<?, ?>) fieldValue, secretKey);
                    }
                } else if (fieldValue instanceof Collection) {
                    ParameterizedType pt = (ParameterizedType)field.getGenericType();
                    // the type of collection's element is string
                    if (pt.getActualTypeArguments()[0].getTypeName().equals(String.class.getName())) {
                        if (null == algorithm) continue;
                        field.set(plain, encrypt((Collection<String>)fieldValue, algorithm, secretKey));
                    } else {
                        encrypt((Collection<?>) fieldValue, secretKey);
                    }
                } else if (fieldValue.getClass().isArray()) {
                    // the type of array's element is string
                    if (fieldValue.getClass().getComponentType().equals(String.class)) {
                        if (null == algorithm) continue;
                        field.set(plain, encrypt((String[])fieldValue, algorithm, secretKey));
                    } else {
                        encrypt((Object[]) fieldValue, secretKey);
                    }
                } else {
                    encrypt(fieldValue, secretKey);
                }
            }
        }
    }

    private static CryptoAlgorithm getEncryptionAlgorithm(Field field) {
        Encryption encryption = field.getAnnotation(Encryption.class);
        if (null != encryption) {
            return encryption.value();
        }
        Crypto crypto = field.getAnnotation(Crypto.class);
        if (null != crypto) {
            return crypto.value();
        }
        return null;
    }

    public static String encrypt(String plainText, CryptoAlgorithm algorithm, String secretKey) {
        if (null == algorithm) return plainText;
        return algorithm.encrypt(plainText, secretKey);
    }



    public static void decrypt(Map<?, ?> map, String secretKey) throws IllegalAccessException {
        if (CollectionUtils.isEmpty(map)) return;
        for (Object value : map.values()) {
            if (value instanceof String) continue;
            decrypt(value, secretKey);
        }
    }

    public static Map<?, String> decrypt(Map<?, String> map, CryptoAlgorithm algorithm, String secretKey) {
        if (CollectionUtils.isEmpty(map) || null == algorithm) return map;
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> decrypt(e.getValue(), algorithm, secretKey)));
    }

    public static void decrypt(Object[] array, String secretKey) throws IllegalAccessException {
        if (Objects.isNull(array) || array.length == 0 || ClassUtils.isPrimitiveArray(array.getClass())) return;
        for (Object element : array) {
            if (element instanceof String) continue;
            decrypt(element, secretKey);
        }
    }

    public static String[] decrypt(String[] array, CryptoAlgorithm algorithm, String secretKey) {
        if (Objects.isNull(array) || array.length == 0 || null == algorithm) return array;
        for (int i=0; i<array.length; i++) {
            array[i] = decrypt(array[i], algorithm, secretKey);
        }
        return array;
    }

    public static void decrypt(Collection<?> collection, String secretKey) throws IllegalAccessException {
        if (CollectionUtils.isEmpty(collection)) return;
        for (Object element : collection) {
            if (element instanceof String) continue;
            decrypt(element, secretKey);
        }
    }

    public static Collection<String> decrypt(Collection<String> collection, CryptoAlgorithm algorithm, String secretKey) {
        if (CollectionUtils.isEmpty(collection) || null == algorithm) return collection;
        return collection.stream().map(e -> decrypt(e, algorithm, secretKey)).collect(Collectors.toList());
    }

    public static void decrypt(Object cipher, String secretKey) throws IllegalAccessException {
        if (Objects.isNull(cipher)) return;
        if (cipher instanceof String || cipher instanceof Date || cipher instanceof LocalDate
                || cipher instanceof LocalDateTime || ClassUtils.isPrimitiveOrWrapper(cipher.getClass())
                || cipher instanceof Number || cipher instanceof Collection || cipher instanceof Map
                || cipher.getClass().isArray()) return;
        Field[] fields = cipher.getClass().getDeclaredFields();
        for (Field field : fields) {
            // ignore final field
            if (Modifier.isFinal(field.getModifiers())) continue;
            field.setAccessible(true);
            Object fieldValue = field.get(cipher);
            if (Objects.isNull(fieldValue)) continue;
            CryptoAlgorithm algorithm = getDecryptionAlgorithm(field);
            if (null == algorithm) continue;
            if (field.getType() == String.class) {
                field.set(cipher, decrypt((String)fieldValue, algorithm, secretKey));
            } else {
                if (fieldValue instanceof Map) {
                    ParameterizedType pt = (ParameterizedType)field.getGenericType();
                    // the type of map's value is string
                    if (pt.getActualTypeArguments()[1].getTypeName().equals(String.class.getName())) {
                        field.set(cipher, decrypt((Map<?, String>)fieldValue, algorithm, secretKey));
                    } else {
                        encrypt((Map<?, ?>) fieldValue, secretKey);
                    }
                } else if (fieldValue instanceof Collection) {
                    ParameterizedType pt = (ParameterizedType)field.getGenericType();
                    // the type of collection's element is string
                    if (pt.getActualTypeArguments()[0].getTypeName().equals(String.class.getName())) {
                        field.set(cipher, decrypt((Collection<String>)fieldValue, algorithm, secretKey));
                    } else {
                        decrypt((Collection<?>) fieldValue, secretKey);
                    }
                } else if (fieldValue.getClass().isArray()) {
                    // the type of array's element is string
                    if (fieldValue.getClass().getComponentType().equals(String.class)) {
                        field.set(cipher, encrypt((String[])fieldValue, algorithm, secretKey));
                    } else {
                        decrypt((Object[]) fieldValue, secretKey);
                    }
                } else {
                    decrypt(fieldValue, secretKey);
                }
            }
        }
    }

    public static String decrypt(String cipherText, CryptoAlgorithm algorithm, String secretKey) {
        if (null == algorithm) return cipherText;
        return algorithm.decrypt(cipherText, secretKey);
    }

    private static CryptoAlgorithm getDecryptionAlgorithm(Field field) {
        Decryption decryption = field.getAnnotation(Decryption.class);
        if (null != decryption) {
            return decryption.value();
        }
        Crypto crypto = field.getAnnotation(Crypto.class);
        if (null != crypto) {
            return crypto.value();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(HexUtil.encodeHexStr("张"));
        System.out.println(HexUtil.encodeHexStr("张学友张学友"));

        long start = System.currentTimeMillis();
        String text = "张";
        String secretKey = "123456";
        String encode = CryptoAlgorithm.QUERY.encrypt(text, secretKey);
        System.out.println(encode);
        String decode = CryptoAlgorithm.QUERY.decrypt(encode, secretKey);
        System.out.println(decode);
        System.out.println((System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        encode = CryptoAlgorithm.AES.encrypt(text, secretKey);
        System.out.println(encode);
        decode = CryptoAlgorithm.AES.decrypt(encode, secretKey);
        System.out.println(decode);
        System.out.println((System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        encode = CryptoAlgorithm.DES.encrypt(text, secretKey);
        System.out.println(encode);
        decode = CryptoAlgorithm.DES.decrypt(encode, secretKey);
        System.out.println(decode);
        System.out.println((System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        encode = CryptoAlgorithm.SM4.encrypt(text, secretKey);
        System.out.println(encode);
        decode = CryptoAlgorithm.SM4.decrypt(encode, secretKey);
        System.out.println(decode);
        System.out.println((System.currentTimeMillis() - start));

        List<String> list = new ArrayList<>();
        System.out.println(list.getClass().getComponentType());
    }
}
