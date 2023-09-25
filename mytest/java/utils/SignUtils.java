package com.example.aidltest.utils;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public class SignUtils {

    public static String getSign(Object object, String secret) {
        StringBuilder builder = new StringBuilder();
        Class objectClass = object.getClass();
        Field[] declaredFields = objectClass.getDeclaredFields();
        Map<String, Object> fieldsMap = new TreeMap<>();
        String fieldName;
        for (Field declaredField : declaredFields) {
            fieldName = getFieldName(declaredField);
            if (!"sign".equals(fieldName)) {
                try {
                    fieldsMap.put(fieldName, declaredField.get(object));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Map.Entry<String, Object> entry : fieldsMap.entrySet()) {
            builder.append(entry.getKey()).append(entry.getValue());
            LogUtils.d(entry.getKey() + " : " + entry.getValue());
        }
        builder.append(secret);
        LogUtils.d(builder.toString());
        return EncryptUtils.encryptMD5ToString(builder.toString())
                .toLowerCase()
                .replaceAll("-", "");
    }

    private static String getFieldName(Field field) {
        field.setAccessible(true);
        Annotation annotation = field.getAnnotation(SerializedName.class);
        if (annotation != null) {
            return ((SerializedName) annotation).value();
        }
        return field.getName();
    }
}
