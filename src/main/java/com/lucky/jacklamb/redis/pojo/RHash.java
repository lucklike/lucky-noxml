package com.lucky.jacklamb.redis.pojo;

import com.lucky.jacklamb.redis.JedisFactory;
import com.lucky.jacklamb.rest.LSON;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis-Hash
 * @author fk7075
 * @version 1.0.0
 * @date 2020/8/29 2:58 下午
 */
public class RHash<Field,Pojo> implements RedisKey {

    private static LSON lson=new LSON();
    private Jedis jedis;
    private Type fieldType;
    private Type pojoType;
    private String key;

    public RHash(String key){
        jedis= JedisFactory.getJedis();
        fieldType= ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        pojoType= ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        this.key = "RHash<"+fieldType.getTypeName()+","+pojoType.getTypeName()+">-["+key+"]";
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     * @param field
     * @param increment
     * @return
     */
    public Long hincrBy(Field field,Long increment){
        return jedis.hincrBy(key,lson.toJsonByGson(field),increment);
    }

    /**
     * 为哈希表 key 中的指定字段的浮点数值加上增量 increment
     * @param field
     * @param increment
     * @return
     */
    public Double hincrByFloat(Field field,double increment){
        return jedis.hincrByFloat(key,lson.toJsonByGson(field),increment);
    }

    /**
     * 获取RedisKey
     * @return
     */
    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String newKey) {
        this.key = "RHash<"+fieldType.getTypeName()+","+pojoType.getTypeName()+">-["+newKey+"]";
    }

    /**
     * 将哈希表中的字段 field 的值设为 value
     * @param field key字段
     * @param pojo 值
     * @return
     */
    public Long hset(Field field, Pojo pojo){
        return jedis.hset(key,lson.toJsonByGson(field),lson.toJsonByGson(pojo));
    }

    /**
     * 获取存储在哈希表中指定字段的值。
     * @param field key
     * @return
     */
    public Pojo hget(Field field){
        return (Pojo) lson.fromJson(pojoType,jedis.hget(key,lson.toJsonByGson(field)));
    }

    /**
     * 查看哈希表 key 中，指定的字段是否存在。
     * @param field key
     * @return
     */
    public boolean hexists(Field field){
        return jedis.hexists(key,lson.toJsonByGson(field));
    }

    /**
     * 删除一个或多个哈希表字段
     * @param fields 要删除字段的keys
     */
    public Long hdel(Field...fields){
        String[] fieldStrs=new String[fields.length];
        for (int i = 0,j=fields.length; i < j; i++) {
            fieldStrs[i]=lson.toJson(fields[i]);
        }
        return jedis.hdel(key,fieldStrs);
    }

    /**
     * 获取所有哈希表中的字段
     * @return
     */
    public Set<Field> hkeys(){
        Set<Field> keySet= new HashSet<>();
        Set<String> strKeys = jedis.hkeys(key);
        for (String strKey : strKeys) {
            keySet.add((Field) lson.fromJson(fieldType,strKey));
        }
        return keySet;
    }

    /**
     * 获取hash表中元素的个数
     * @return
     */
    public Long size(){
        return jedis.hlen(key);
    }

    /**
     * 获取所有给定字段的值
     * @param fields
     * @return
     */
    public List<Pojo> hmget(Field...fields){
        String[] fieldStrs=new String[fields.length];
        for (int i = 0,j=fields.length; i < j; i++) {
            fieldStrs[i]=lson.toJson(fields[i]);
        }
        return jedis.hmget(key,fieldStrs).stream().map((k)->{
            Pojo pojo= (Pojo) lson.fromJson(pojoType,k);
            return pojo;
        }).collect(Collectors.toList());
    }

    /**
     *同时将多个 field-value (域-值)对设置到哈希表 key 中。
     * @param map
     */
    public String hmset(Map<Field,Pojo> map){
        Map<String,String> strMap=new HashMap<>();
        for(Map.Entry<Field,Pojo> entry:map.entrySet()){
            strMap.put(lson.toJsonByGson(entry.getKey()),lson.toJson(entry.getValue()));
        }
        return jedis.hmset(key,strMap);
    }

    /**
     * 获取哈希表中所有值。
     * @return
     */
    public List<Pojo> hvals(){
        return jedis.hvals(key).stream().map((p)->{
            Pojo pojo= (Pojo) lson.fromJson(pojoType,p);
            return pojo;
        }).collect(Collectors.toList());
    }

    /**
     * 只有在字段 field 不存在时，设置哈希表字段的值。
     * @param field
     * @param pojo
     * @return
     */
    public Long hsetnx(Field field, Pojo pojo){
        return jedis.hsetnx(key,lson.toJsonByGson(field),lson.toJsonByGson(pojo));
    }

    /**
     * 获取在哈希表中指定 key 的所有字段和值
     * @return
     */
    public Map<Field,Pojo> hgetall(){
        Map<Field,Pojo> kvMap=new HashMap<>();
        Map<String, String> kvStrMap = jedis.hgetAll(key);
        for(Map.Entry<String,String> entry:kvStrMap.entrySet()){
            kvMap.put((Field) lson.fromJson(fieldType,entry.getKey()),(Pojo) lson.fromJson(pojoType,entry.getValue()));
        }
        return kvMap;
    }

    /**
     * 获取一个用于遍历该hash的迭代器
     * @param cursor 游标
     * @return
     */
    public ScanResult<Map.Entry<Field,Pojo>> hscan(String cursor){
        return hscan(cursor,new ScanParams());
    }

    /**
     * 获取一个用于遍历该hash的迭代器
     * @param cursor 游标
     * @param params
     * @return
     */
    public ScanResult<Map.Entry<Field,Pojo>> hscan(String cursor, ScanParams params){
        ScanResult<Map.Entry<String, String>> hscan = jedis.hscan(key, cursor,params);
        List<Map.Entry<String, String>> result = hscan.getResult();
        List<Map.Entry<Field, Pojo>> results = new ArrayList<Map.Entry<Field, Pojo>>();
        for (Map.Entry<String, String> entry : result) {
            results.add(new AbstractMap.SimpleEntry<Field, Pojo>((Field) lson.fromJson(fieldType,entry.getKey()),(Pojo) lson.fromJson(pojoType,entry.getValue())));
        }
        return new ScanResult<Map.Entry<Field,Pojo>>(cursor,results);
    }

    /**
     * 关闭Redis连接
     */
    public void close(){
        jedis.close();
    }
}
