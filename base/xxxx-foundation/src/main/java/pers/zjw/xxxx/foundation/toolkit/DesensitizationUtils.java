package pers.zjw.xxxx.foundation.toolkit;

import pers.zjw.xxxx.foundation.annotation.SensitiveField;
import pers.zjw.xxxx.foundation.constant.FieldType;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 脱敏工具类
 *
 * @author zhangjw
 * @date 2023/01/12 0012 16:15
 */
public final class DesensitizationUtils {

    public static void blur(Map<?, ?> map) throws IllegalAccessException {
        if (CollectionUtils.isEmpty(map)) return;
        for (Object value : map.values()) {
            if (value instanceof String) return;
            blur(value);
        }
    }

    public static Map<?, String> blur(Map<?, String> map, FieldType type) {
        if (CollectionUtils.isEmpty(map)) return map;
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> blur(e.getValue(), type)));
    }

    public static void blur(Object[] array) throws IllegalAccessException {
        if (Objects.isNull(array) || array.length == 0 || ClassUtils.isPrimitiveArray(array.getClass())) return;
        for (Object element : array) {
            if (element instanceof String) return;
            blur(element);
        }
    }

    public static String[] blur(String[] array, FieldType type) {
        if (Objects.isNull(array) || array.length == 0) return array;
        for (int i=0; i<array.length; i++) {
            array[i] = blur(array[i], type);
        }
        return array;
    }

    public static void blur(Collection<?> collection) throws IllegalAccessException {
        if (CollectionUtils.isEmpty(collection)) return;
        for (Object element : collection) {
            if (element instanceof String) return;
            blur(element);
        }
    }

    public static Collection<String> blur(Collection<String> collection, FieldType type) {
        if (CollectionUtils.isEmpty(collection)) return collection;
        return collection.stream().map(e -> blur(e, type)).collect(Collectors.toList());
    }

    public static void blur(Object plain) throws IllegalAccessException {
        if (Objects.isNull(plain)) return;
        if (plain instanceof String || plain instanceof Date || plain instanceof LocalDate
                || plain instanceof LocalDateTime || plain instanceof Number
                || ClassUtils.isPrimitiveOrWrapper(plain.getClass())) return;
        Field[] fields = plain.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers())) continue;
            field.setAccessible(true);
            Object fieldValue = field.get(plain);
            if (Objects.isNull(fieldValue)) continue;
            SensitiveField annotation = field.getAnnotation(SensitiveField.class);
            if (field.getType() == String.class) {
                field.set(plain, blur((String)fieldValue, annotation.value()));
            } else {
                if (fieldValue instanceof Map) {
                    if (!(field.getGenericType() instanceof ParameterizedType)) continue;
                    ParameterizedType pt = (ParameterizedType)field.getGenericType();
                    if (pt.getActualTypeArguments()[1].getTypeName().equals(String.class.getName())) {
                        if (null == annotation) continue;
                        field.set(plain, blur((Map<?, String>)fieldValue, annotation.value()));
                    } else {
                        blur((Map<?, ?>) fieldValue);
                    }
                } else if (fieldValue instanceof Collection) {
                    if (!(field.getGenericType() instanceof ParameterizedType)) continue;
                    ParameterizedType pt = (ParameterizedType)field.getGenericType();
                    if (pt.getActualTypeArguments()[0].getTypeName().equals(String.class.getName())) {
                        if (null == annotation) continue;
                        field.set(plain, blur((Collection<String>)fieldValue, annotation.value()));
                    } else {
                        blur((Collection<?>) fieldValue);
                    }
                } else if (fieldValue.getClass().isArray()) {
                    if (fieldValue.getClass().getComponentType().equals(String.class)) {
                        if (null == annotation) continue;
                        field.set(plain, blur((String[])fieldValue, annotation.value()));
                    } else {
                        blur((Object[]) fieldValue);
                    }
                } else {
                    blur(fieldValue);
                }
            }
        }
    }

    public static String blur(String plain, FieldType type) {
        if (Objects.isNull(plain) || Objects.isNull(type)) return plain;
        if (FieldType.REAL_NAME == type || FieldType.USER_NAME == type
                || FieldType.ID_NUM == type) {
            if (plain.length() <= 4) {
                return "****" + plain.substring(plain.length() - 1);
            } else {
                return plain.substring(0, 2) + "****" + plain.substring(plain.length() - 1);
            }
        } else if (FieldType.MOBILE == type){
            return plain.substring(0, 3) + "*****" + plain.substring(plain.length() - 3);
        } else if (FieldType.EMAIL == type){
            return plain.substring(0, 2) + "*****" + plain.substring(plain.indexOf("@"));
        } else {
            blur(plain, FieldType.USER_NAME);
        }
        return plain;
    }
}