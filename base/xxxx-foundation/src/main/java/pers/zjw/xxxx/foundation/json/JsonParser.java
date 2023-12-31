package pers.zjw.xxxx.foundation.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import pers.zjw.xxxx.foundation.exception.JsonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * json parser
 *
 * @author zhangjw
 * @date 2022/11/27 0027 15:57
 */
@Slf4j
public final class JsonParser {
    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.CLOSE_CLOSEABLE)
            .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT,
                    DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            .setTimeZone(TimeZone.getTimeZone("GMT+8"))
            .setLocale(Locale.CHINESE)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public static ObjectMapper customize() {
        return mapper;
    }

    public static String defaultString() {
        return "{}";
    }

    public static String toString(Object object) {
        Assert.notNull(object, "object must not be null");
        if (object instanceof String) return (String) object;
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("parse object to json string failed");
            throw new JsonException("convert object to json string failed", e);
        }
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        Assert.hasText(json, "json string must not be null");
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("parse json string to class failed: {}", json);
            throw new JsonException("parse json string to object with class failed", e);
        }
    }

    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        Assert.hasText(json, "json string must not be null");
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("parse json string to typeReference failed: {}", json);
            throw new JsonException("parse json string to object with type reference failed", e);
        }
    }

    public static <T> T toObject(InputStream is, Class<T> clazz) {
        Assert.notNull(is, "json InputStream must not be null");
        try {
            return mapper.readValue(is, clazz);
        } catch (IOException e) {
            log.error("parse json InputStream to class failed");
            throw new JsonException("parse json stream to object with class failed", e);
        }
    }

    public static <T> T toObject(InputStream is, TypeReference<T> typeReference) {
        Assert.notNull(is, "json InputStream must not be null");
        try {
            return mapper.readValue(is, typeReference);
        } catch (IOException e) {
            log.error("parse json InputStream to typeReference failed");
            throw new JsonException("parse json stream to object with type reference failed", e);
        }
    }

    public static <T> T convert(Object object, TypeReference<T> typeReference) {
        Assert.notNull(object, "object must not be null");
        return mapper.convertValue(object, typeReference);
    }

    private static final TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};

    public static Map<String, Object> toMap(Object object) {
        Assert.notNull(object, "object must not be null");
        return mapper.convertValue(object, typeRef);
    }
}
