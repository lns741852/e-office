package com.cgpt.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final int timeout = 60 * 30;  //30分鐘

    /**
     * 存儲數據到 Redis，設置過期時間
     *
     * @param key     鍵
     * @param value   值
     * @param timeout 過期時間（秒）
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 根據鍵取出數據
     *
     * @param key 鍵
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 刪除 Redis 中指定的數據
     *
     * @param key 鍵
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 判斷指定的鍵是否存在於 Redis 中
     *
     * @param key 鍵
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 儲存 Set 數據到 Redis
     *
     * @param key 鍵
     * @param set Set 數據
     */
    public void addSet(String key, Set<Object> set) {
        redisTemplate.opsForSet().add(key, set);
    }

    /**
     * 根據鍵取出 Set 數據
     *
     * @param key 鍵
     * @return Set 數據
     */
    public Set<Object> getSet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 儲存 List 數據到 Redis
     *
     * @param key  鍵
     * @param list List 數據
     */
    public void addList(String key, List<Object> list) {
        redisTemplate.opsForList().rightPushAll(key, list);
    }

    /**
     * 根據鍵取出 List 數據
     *
     * @param key 鍵
     * @return List 數據
     */
    public List<Object> getList(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

}