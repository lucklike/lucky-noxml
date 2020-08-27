package com.lucky.jacklamb.sqlcore.jdbc.conversion;

import com.lucky.jacklamb.annotation.orm.NoPackage;
import com.lucky.jacklamb.sqlcore.util.PojoManage;
import com.lucky.jacklamb.tcconversion.typechange.JavaConversion;
import com.lucky.jacklamb.utils.reflect.ClassUtils;
import com.lucky.jacklamb.utils.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/8/13 11:34
 */
public abstract class JDBCConversion {

    public static <E> E conversion(Map<String,Object> queryResult,Class<E> entityClass){
        if(ClassUtils.isBasic(entityClass)){
            for(Map.Entry<String,Object> en:queryResult.entrySet()){
                return (E) JavaConversion.strToBasic(en.getValue().toString(),entityClass);
            }
            return null;
        }
        E result = ClassUtils.newObject(entityClass);
        Field[] allFields = ClassUtils.getAllFields(entityClass);
        String entityFieldName;
        for (Field entityField : allFields) {
            if(entityField.isAnnotationPresent(NoPackage.class)){
                continue;
            }
            if(FieldUtils.isBasicSimpleType(entityField)){
                entityFieldName=PojoManage.getTableField(entityField).toUpperCase();
                if(queryResult.containsKey(entityFieldName)){
                    FieldUtils.setValue(result,entityField,queryResult.get(entityFieldName));
                }
            }else{
                Object fieldObject=conversion(queryResult,entityField.getType());
                FieldUtils.setValue(result,entityField,fieldObject);
            }

        }
        return result;
    }

    public static <E> List<E> conversion(List<Map<String,Object>> queryResult,Class<E> entityClass){
        List<E> result=new ArrayList<>();
        for (Map<String, Object> entry : queryResult) {
            result.add(conversion(entry,entityClass));
        }
        return result;
    }

    public static <E> E conversion(Map<String,Object> queryResult,Class<E> entityClass,Map<String,String> resultMap){
        E result = ClassUtils.newObject(entityClass);
        Field[] allFields = ClassUtils.getAllFields(entityClass);
        for (Field field : allFields) {

        }
        return result;
    }

}
