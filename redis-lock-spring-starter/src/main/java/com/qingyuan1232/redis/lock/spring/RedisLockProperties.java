package com.qingyuan1232.redis.lock.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: zhao qingyuan
 * @date: 2019-06-11 11:10
 */
@Data
@ConfigurationProperties(prefix = "redis.lock")
public class RedisLockProperties {
    private long awaitTimeout;
    private long lockTimeout;
}
