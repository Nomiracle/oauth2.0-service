package com.example.auth.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class PropertiesOps {
    //find recursively target properties specified by targetKeys in a entity and add those properties to result entity.
    public static void recursionFindProperty(Set<String> targetKeys, JsonObject entity, Map<String, String> result) {
        if (targetKeys.isEmpty()) return;
        entity.keySet().forEach(property -> {
            if (targetKeys.contains(property)) {
                result.put(property, entity.get(property).getAsString());
                targetKeys.remove(property);
            }
            if (entity.get(property).isJsonObject()) {
                recursionFindProperty(targetKeys, entity.get(property).getAsJsonObject(), result);
            }
        });
    }

    //static generic method
    //pick up all entity's empty (String type) properties
    public static <T> Set<String> pickupEmptyProperties(T entity) {

        //get all private  properties without inherited fields
        Set<Field> fields = new HashSet<>(Arrays.asList(entity.getClass().getDeclaredFields()));
        //get all inherited fields without private  properties
        fields.addAll(Arrays.asList(entity.getClass().getFields()));
        Set<String> stringTypeProperties = new HashSet<>();
        Set<String> otherTypeProperties = new HashSet<>();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            if (String.class.equals(fieldType)) {
                stringTypeProperties.add(field.getName());
            } else {
                otherTypeProperties.add(field.getName());
            }
        }
        Set<String> allProperties = new HashSet<>();
        allProperties.addAll(stringTypeProperties);
        allProperties.addAll(otherTypeProperties);
        log.info(entity.getClass().getSimpleName() + "所有属性名为：{}", allProperties.toString());

        String json = new Gson().toJson(entity);
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        for (String stringKey : stringTypeProperties) {
            if (jsonObject.get(stringKey) != null && StringUtils.isEmpty(jsonObject.get(stringKey).getAsString())) {
                jsonObject.remove(stringKey);
            }
        }
        log.info(entity.getClass().getSimpleName() + "非空属性名为：{}", jsonObject.keySet().toString());
        //The default behavior that is implemented in Gson is that null object fields are ignored
        Set<String> fieldsWithValue = jsonObject.keySet();
        allProperties.removeAll(fieldsWithValue);
        log.info(entity.getClass().getSimpleName() + "此实体缺少的属性名为：{}", allProperties);
        return allProperties;
    }
}
