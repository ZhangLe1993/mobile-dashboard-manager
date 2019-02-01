package com.aihuishou.bi.md.cache;

import com.aihuishou.bi.md.Application;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by joey.chen on 2016-02-22.
 */
public class RedisConPool {

    private static JedisPool pool;
    private static JedisPoolConfig config = new JedisPoolConfig();

    private static int redisDbIndex = Integer.parseInt(Application.getPro("redis_db"));

    public static Jedis getRecource(String host, String port) throws Exception {
        if (pool == null) {
            synchronized (RedisConPool.class) {
                //最大连接数
                Integer maxTotal = 2000;
                String max_total = Application.getPro("redis_max_con");
                maxTotal = max_total == null ? maxTotal : Integer.parseInt(max_total);
                config.setMaxTotal(maxTotal);
                //空闲数
                config.setMaxIdle(200);
                //最大等待时间
                config.setMaxWaitMillis(1000 * 30);
                //是否进行测试
                config.setTestOnBorrow(true);
                config.setTestOnCreate(true);
                config.setTestOnReturn(true);
                config.setMinIdle(50);
                if (pool == null) {
                    pool = new JedisPool(config, host, Integer.parseInt(port), 1000 * 20,Application.getPro("redis_pw"));
                }
            }
        }
        Jedis jedis = pool.getResource();
        jedis.select(redisDbIndex);
        return jedis;
    }

    public static void close() {
        pool.destroy();
    }

}
