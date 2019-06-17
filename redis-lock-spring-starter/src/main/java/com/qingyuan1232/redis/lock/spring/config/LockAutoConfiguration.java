package com.qingyuan1232.redis.lock.spring.config;

import com.qingyuan1232.redis.lock.core.AbstractLockTemplate;
import com.qingyuan1232.redis.lock.core.LockTemplate;
import com.qingyuan1232.redis.lock.spring.BusinessKeyProvider;
import com.qingyuan1232.redis.lock.spring.RedisLockProperties;
import com.qingyuan1232.redis.lock.spring.handler.LockHandler;
import com.qingyuan1232.lock.core.realization.jedis.JedisLock;
import com.qingyuan1232.lock.core.realization.redisson.RedissonLock;
import com.qingyuan1232.lock.core.realization.redistemplate.RedisTemplateLock;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPool;

/**
 * @author: zhao qingyuan
 * @date: 2019-06-11 11:15
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(RedisLockProperties.class)
@Import(LockHandler.class)
public class LockAutoConfiguration {

    @Autowired
    private RedisLockProperties redisLockProperties;

    @Bean
    @ConditionalOnBean(JedisPool.class)
    public LockTemplate redisLock(JedisPool jedisPool) {
        JedisLock lock = new JedisLock();

        setTimeout(lock);

        lock.setJedisPool(jedisPool);
        return lock;
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public LockTemplate redisTemplateLock(RedisTemplate redisTemplate) {
        RedisTemplateLock lock = new RedisTemplateLock();

        setTimeout(lock);

        lock.setRedisTemplate(redisTemplate);
        return lock;
    }

    @Bean
    @ConditionalOnBean(Redisson.class)
    public LockTemplate redissonLock(Redisson redisson) {
        RedissonLock lock = new RedissonLock();

        setTimeout(lock);

        lock.setRedisson(redisson);
        return lock;
    }

    @Bean
    public BusinessKeyProvider businessKeyProvider() {
        return new BusinessKeyProvider();
    }

    private void setTimeout(AbstractLockTemplate lockTemplate) {
        lockTemplate.setAwaitTimeout(redisLockProperties.getAwaitTimeout());
        lockTemplate.setLockTimeout(redisLockProperties.getLockTimeout());
    }

}
