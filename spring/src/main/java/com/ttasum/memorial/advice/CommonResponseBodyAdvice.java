package com.ttasum.memorial.advice;

import com.ttasum.memorial.annotation.ConvertGender;
import com.ttasum.memorial.annotation.FormatDate;
import com.ttasum.memorial.annotation.MaskNameIfAnonymous;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CommonResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final Map<Class<?>, List<Field>> maskCache   = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<Field>> dateCache   = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<Field>> genderCache = new ConcurrentHashMap<>();

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        // 전체 컨트롤러 응답에 공통 적용
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (body == null) {
            return null;
        }

        // ResponseEntity<T> 래핑 해제
        if (body instanceof ResponseEntity<?> resp) {
            applyToObject(resp.getBody());
            return resp;
        }

        applyToObject(body);
        return body;
    }

    private void applyToObject(Object obj) {
        if (obj instanceof Iterable<?> iterable) {
            iterable.forEach(this::processAll);
        } else if (obj instanceof Map<?, ?> map && map.containsKey("content")) {
            ((List<?>) map.get("content")).forEach(this::processAll);
        } else {
            processAll(obj);
        }
    }

    private void processAll(Object target) {
        if (target == null) return;
        Class<?> cls = target.getClass();

        // DTO에 어노테이션이 하나도 없으면 건너뛰기
        if (!Arrays.stream(cls.getDeclaredFields())
                .anyMatch(f -> f.isAnnotationPresent(MaskNameIfAnonymous.class)
                        || f.isAnnotationPresent(FormatDate.class)
                        || f.isAnnotationPresent(ConvertGender.class))) {
            return;
        }

        applyMasking(target, cls);
        applyDateFormatting(target, cls);
        applyGenderConversion(target, cls);
    }

    private void applyMasking(Object target, Class<?> cls) {
        List<Field> fields = maskCache.computeIfAbsent(cls, c ->
                Arrays.stream(c.getDeclaredFields())
                        .filter(f -> f.isAnnotationPresent(MaskNameIfAnonymous.class))
                        .collect(Collectors.toList())
        );
        if (fields.isEmpty()) return;

        String anonymity = getFieldValue(target, "anonymityFlag", String.class);
        if (!"Y".equalsIgnoreCase(anonymity)) return;

        for (Field f : fields) {
            f.setAccessible(true);
            String name = getFieldValue(target, f.getName(), String.class);
            if (name != null) {
                setFieldValue(target, f, maskName(name));
            }
        }
    }

    private void applyDateFormatting(Object target, Class<?> cls) {
        List<Field> fields = dateCache.computeIfAbsent(cls, c ->
                Arrays.stream(c.getDeclaredFields())
                        .filter(f -> f.isAnnotationPresent(FormatDate.class))
                        .collect(Collectors.toList())
        );
        for (Field f : fields) {
            FormatDate ann = f.getAnnotation(FormatDate.class);
            String raw = getFieldValue(target, f.getName(), String.class);
            if (raw == null || raw.length() < ann.pattern().length()) continue;

            LocalDate ld = LocalDate.parse(raw, DateTimeFormatter.ofPattern(ann.pattern()));
            String formatted = ld.format(DateTimeFormatter.ofPattern(ann.output()));
            setFieldValue(target, f, formatted);
        }
    }

    private void applyGenderConversion(Object target, Class<?> cls) {
        List<Field> fields = genderCache.computeIfAbsent(cls, c ->
                Arrays.stream(c.getDeclaredFields())
                        .filter(f -> f.isAnnotationPresent(ConvertGender.class))
                        .collect(Collectors.toList())
        );
        for (Field f : fields) {
            ConvertGender ann = f.getAnnotation(ConvertGender.class);
            String code = getFieldValue(target, f.getName(), String.class);
            String conv = "M".equalsIgnoreCase(code)       ? ann.male()
                    : "F".equalsIgnoreCase(code)
                    || "W".equalsIgnoreCase(code)     ? ann.female()
                    : ann.defaultValue();
            setFieldValue(target, f, conv);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getFieldValue(Object target, String name, Class<T> type) {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return (T) f.get(target);
        } catch (Exception e) {
            return null;
        }
    }

    private void setFieldValue(Object target, Field f, Object value) {
        try {
            f.setAccessible(true);
            f.set(target, value);
        } catch (IllegalAccessException ignored) {}
    }

    private String maskName(String name) {
        if (name.length() == 2) return name.charAt(0) + "*";
        if (name.length() == 4) return name.charAt(0) + name.charAt(1) + "*" +  name.substring(3);
        return name.charAt(0) + "*" + name.charAt(name.length() - 1);
    }
}
