package com.yozosoft.fileserver.service.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * redis实现存取
 *
 * @author zhouf
 */
@Service("redisService")
@Slf4j
public class RedisService<T> {

    @Autowired
    @Qualifier("fileServerRedisTemplate")
    private RedisTemplate<Object, Object> redisTemplate;

    public boolean set(String key, T value) {
        try {
            if (StringUtils.isEmpty(key)) {
                return false;
            }
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("key为" + key + ",redis插入失败", e);
            return false;
        }
    }

    public boolean setnx(String key, Object value, Long time) {
        try {
            if (StringUtils.isEmpty(key)) {
                return false;
            }
            return redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("key为" + key + ",redis setnx插入失败", e);
            return false;
        }
    }

    public boolean set(String key, T value, Long time) {
        try {
            if (StringUtils.isEmpty(key)) {
                return false;
            }
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("key为" + key + ",redis插入失败", e);
            return false;
        }
    }

    public boolean increment(String key) {
        try {
            if (StringUtils.isEmpty(key)) {
                return false;
            }
            Long increment = redisTemplate.opsForValue().increment(key, 1L);
            return true;
        } catch (Exception e) {
            log.error("key为" + key + ",redis自增失败", e);
            return false;
        }
    }

    /**
     * redis不支持Long
     */
    public T get(String key, Class<T> clazz) {
        try {
            if (StringUtils.isEmpty(key)) {
                return null;
            }
            Object obj = redisTemplate.opsForValue().get(key);
            if (clazz != null && clazz == Long.class && obj instanceof Integer) {
                return (T) Long.valueOf(obj.toString());
            }
            return (T) obj;
        } catch (Exception e) {
            log.error("key为" + key + ",redis获取失败", e);
            return null;
        }
    }

    public T get(String key) {
        return get(key, null);
    }

    public boolean exists(String key) {
        try {
            if (StringUtils.isEmpty(key)) {
                return false;
            }
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("key为" + key + ",redis判断存在失败", e);
            return false;
        }
    }

    public boolean clearAll() {
        try {
            return redisTemplate.execute((RedisConnection connection) -> {
                connection.flushDb();
                return true;
            });
        } catch (Exception e) {
            log.error("redis清空失败", e);
            return false;
        }
    }

    public boolean delete(String key) {
        try {
            if (StringUtils.isEmpty(key)) {
                return false;
            }
            redisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            log.error("key为" + key + ",redis删除失败", e);
            return false;
        }
    }

    public boolean push(String key, T value) {
        try {
            if (StringUtils.isEmpty(key)) {
                return false;
            }
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("key为" + key + ",redis push失败", e);
            return false;
        }
    }

    public T pop(String key) {
        try {
            if (StringUtils.isEmpty(key)) {
                return null;
            }
            return (T) redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            log.error("key为" + key + ",redis pop失败", e);
            return null;
        }
    }

    public Long getListLen(String key) {
        try {
            if (StringUtils.isEmpty(key)) {
                return -1L;
            }
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("key为" + key + ",redis获取llen失败", e);
            return -1L;
        }
    }

    public Boolean setHashValue(String key, String hashKey, T value) {
        try {
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(hashKey)) {
                return false;
            }
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            log.error("key为" + key + ",redis Hash插入失败", e);
            return false;
        }
    }

    public T getHashValue(String key, String hashKey) {
        try {
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(hashKey)) {
                return null;
            }
            return (T) redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            log.error("key为" + key + ",redis Hash获取失败", e);
            return null;
        }
    }

    public Boolean existHashKey(String key, String hashKey) {
        try {
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(hashKey)) {
                return false;
            }
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        } catch (Exception e) {
            log.error("key为" + key + ",redis Hash判断存在失败", e);
            return false;
        }
    }

    public Boolean deleteHashKey(String key, String hashKey) {
        try {
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(hashKey)) {
                return false;
            }
            redisTemplate.opsForHash().delete(key, hashKey);
            return true;
        } catch (Exception e) {
            log.error("key为" + key + ",redis Hash删除失败", e);
            return false;
        }
    }
}
