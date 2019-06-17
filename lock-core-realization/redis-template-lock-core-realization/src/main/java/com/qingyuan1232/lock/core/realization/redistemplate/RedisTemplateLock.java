package com.qingyuan1232.lock.core.realization.redistemplate;

import com.qingyuan1232.redis.lock.core.AbstractLockTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * redis template lock
 *
 * @author: zhao qingyuan
 * @date: 2019-06-11 13:53
 */
public class RedisTemplateLock extends AbstractLockTemplate {
    private static final Logger logger = LoggerFactory.getLogger(RedisTemplateLock.class);

    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean tryLock(String key) {

        try {
            RedisCallback<Boolean> callback = (connection) -> {
                String uuid = UUID.randomUUID().toString();
                return connection.set(key.getBytes(Charset.forName("UTF-8")), uuid.getBytes(Charset.forName("UTF-8")), Expiration.seconds(getLockTimeout()), RedisStringCommands.SetOption.SET_IF_ABSENT);
            };
            return redisTemplate.execute(callback);
        } catch (Exception e) {
            logger.error("set redis throw an exception", e);
        }
        return false;
    }

    @Override
    public boolean releaseLock(String key) {
        redisTemplate.delete(key);
        return true;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
