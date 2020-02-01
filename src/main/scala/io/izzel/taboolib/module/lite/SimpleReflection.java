package io.izzel.taboolib.module.lite;

import com.google.common.collect.Maps;
import io.izzel.taboolib.TabooLibAPI;
import io.izzel.taboolib.module.locale.logger.TLogger;
import io.izzel.taboolib.util.Ref;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author 坏黑
 * @Since 2018-10-25 22:51
 */
public class SimpleReflection {

    private static Map<String, Map<String, Field>> fieldCached = Maps.newHashMap();

    public static boolean isExists(Class<?> nmsClass) {
        return fieldCached.containsKey(nmsClass.getName());
    }

    public static Map<String, Field> getFields(Class<?> nmsClass) {
        return fieldCached.getOrDefault(nmsClass.getName(), Maps.newHashMap());
    }

    public static Field getField(Class<?> nmsClass, String fieldName) {
        return fieldCached.getOrDefault(nmsClass.getName(), Maps.newHashMap()).get(fieldName);
    }

    public static void checkAndSave(Class<?>... nmsClass) {
        Arrays.stream(nmsClass).forEach(SimpleReflection::checkAndSave);
    }

    public static void checkAndSave(Class<?> nmsClass) {
        if (!isExists(nmsClass)) {
            saveField(nmsClass);
        }
    }

    public static void saveField(Class<?>... nmsClass) {
        Arrays.stream(nmsClass).forEach(SimpleReflection::saveField);
    }

    public static void saveField(Class<?> nmsClass) {
        try {
            Arrays.stream(nmsClass.getDeclaredFields()).forEach(declaredField -> saveField(nmsClass, declaredField.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveField(Class<?> nmsClass, String fieldName) {
        try {
            Field declaredField = nmsClass.getDeclaredField(fieldName);
            fieldCached.computeIfAbsent(nmsClass.getName(), name -> Maps.newHashMap()).put(fieldName, declaredField);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setFieldValue(Class<?> nmsClass, Object instance, String fieldName, Object value) {
        Map<String, Field> fields = fieldCached.get(nmsClass.getName());
        if (fields == null) {
            TLogger.getGlobalLogger().error("Field Not Found: " + nmsClass.getName());
        }
        Field field = fields.get(fieldName);
        if (value == null) {
            return;
        }
        try {
            Ref.putField(instance, field, value);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static Object getFieldValue(Class<?> nmsClass, Object instance, String fieldName) {
        Map<String, Field> fields = fieldCached.get(nmsClass.getName());
        if (fields == null) {
            TLogger.getGlobalLogger().error("Field Not Found: " + nmsClass.getName());
        }
        Field field = fields.get(fieldName);
        if (field == null) {
            return null;
        }
        try {
            return Ref.getField(instance, field);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Class<?> nmsClass, Object instance, String fieldName, T def) {
        Map<String, Field> fields = fieldCached.get(nmsClass.getName());
        if (fields == null) {
            TLogger.getGlobalLogger().error("Field Not Found: " + nmsClass.getName());
        }
        Field field = fields.get(fieldName);
        if (field == null) {
            return null;
        }
        try {
            return (T) Ref.getField(instance, field);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public static Class getListType(Field field) {
        Type genericType = field.getGenericType();
        try {
            if (ParameterizedType.class.isAssignableFrom(genericType.getClass())) {
                for (Type actualTypeArgument : ((ParameterizedType) genericType).getActualTypeArguments()) {
                    return TabooLibAPI.getPluginBridge().getClass(actualTypeArgument.getTypeName());
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static Class[] getMapType(Field field) {
        Class[] mapType = new Class[2];
        try {
            Type genericType = field.getGenericType();
            if (ParameterizedType.class.isAssignableFrom(genericType.getClass())) {
                for (Type actualTypeArgument : ((ParameterizedType) genericType).getActualTypeArguments()) {
                    mapType[mapType[0] == null ? 0 : 1] = TabooLibAPI.getPluginBridge().getClass(actualTypeArgument.getTypeName());
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return mapType[1] == null ? null : mapType;
    }
}
