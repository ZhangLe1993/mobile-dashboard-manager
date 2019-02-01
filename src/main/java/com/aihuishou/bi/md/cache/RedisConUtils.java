package com.aihuishou.bi.md.cache;

import com.aihuishou.bi.md.Application;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by joey.chen on 2016-04-12.
 */
public class RedisConUtils {

    private static String redisUrl;
    private static String redisPort;

    private static final Logger logger = LoggerFactory.getLogger(RedisConUtils.class);

    static {
        redisUrl = Application.getPro("redis_host");
        redisPort = Application.getPro("redis_port");
    }

    public static String get(final String key) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            value = jedis.get(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return value;
    }

    public static Set<String> getAllKeys(String pattern) {
        Jedis jedis = null;
        Set<String> keys = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            keys = jedis.keys(pattern);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return keys;
    }

    public static void set(final String key, final String value) {
        Jedis jedis = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
    }

    public static void rename(String oldKey, String newKey) {
        Jedis jedis = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            if (jedis.exists(oldKey)) {
                jedis.rename(oldKey, newKey);
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
    }

    public static void lrem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            jedis.lrem(key, 0, value);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
    }

    public static void removeKey(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            jedis.del(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
    }

    public static boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            return jedis.exists(key);
        } catch (Exception e) {
            logger.error("", e);
            return false;
        } finally {
            close(jedis);
        }
    }


    public static String hmset(final String key, final Map<String, String> hash) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            jedis.hmset(key, hash);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return result;
    }

    public static Map<String, String> hmget(final String key) {
        Jedis jedis = null;
        Map<String, String> result = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            result = jedis.hgetAll(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return result;
    }

    public static List<String> hmget(final String key, final String... fields) {
        Jedis jedis = null;
        List<String> result = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            result = jedis.hmget(key, fields);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return result;
    }

    public static String hget(String key, String field) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            result = jedis.hget(key, field);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return result;
    }

    public static void hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            jedis.hset(key, field, value);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
    }

    public static Long publish(final String channel, final String message) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            result = jedis.publish(channel, message);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return result;
    }

    /*public static void subscribe(final String channel, JedisPubSub jedisPubSub) {
        Jedis jedis = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            jedis.subscribe(jedisPubSub, channel);
        } catch (Exception e) {
            logger.error("", e);
        }finally {
            close(jedis);
        }
    }*/

    public static Set<String> hkeys(final String key) {
        Jedis jedis = null;
        Set<String> result = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            result = jedis.hkeys(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return result;
    }

    public static String flushDB() {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            result = jedis.flushDB();
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return result;
    }

    public static Long del(String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            result = jedis.del(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return result;
    }

    /**
     * 清除指定前缀的cache key
     *
     * @param key
     */
    public static void delPrefix(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            Set<String> keySet = jedis.keys(key + "*");
//            jedis.del(keySet.toArray(new String[keySet.size()]));
            for (String k : keySet) {
                jedis.del(k);
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
    }

    public static String getInfoByKey(String key) {
        String info = info();
        String[] infos = info.split("\r\n");
        Map<String, String> map = new HashMap();
        for (int i = 0; i < infos.length; i++) {
            if (infos[i].length() == 0 || infos[i].startsWith("#")) {
                continue;
            } else {
                String[] kv = infos[i].split(":");
                if (kv.length == 2) {
                    map.put(kv[0], kv[1]);
                }

            }
        }
        return map.get(key);
    }

    public static String info() {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            result = jedis.info();
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return result;
    }

    public static Long llen(final String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            result = jedis.llen(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return result;
    }

    public static String lindex(final String key, final long index) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            result = jedis.lindex(key, index);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return result;
    }

    public static List<String> lrange(final String key) {
        Jedis jedis = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            Long len = jedis.llen(key);
            if (len > 0) {
                return jedis.lrange(key, 0, len);
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return new ArrayList<>();
    }


    public static Long expire(final String key, final int seconds) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            result = jedis.expire(key, seconds);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            close(jedis);
        }
        return result;
    }

    public static Long rpush(final String key, final String... strings) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisConPool.getRecource(redisUrl, redisPort);
            result = jedis.rpush(key, strings);
        } catch (Exception e) {
            logger.error("key:" + key + "\nstrings:" + StringUtils.join(strings, "  ----  "), e);
        } finally {
            close(jedis);
        }
        return result;
    }

    public static void close(Jedis jedis) {
        try {
            if (jedis != null) {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    static class ConfUtils {

        private static Properties pro = new Properties();
        private static final Logger logger = LoggerFactory.getLogger(ConfUtils.class);

        static {
            try {
                pro.load(ConfUtils.class.getResourceAsStream("/properties/application.properties"));
            } catch (IOException e) {
                logger.error("", e);
            }
        }

        public static String get(String key) {
            String val = System.getenv(key);
            if (val == null) {
                val = System.getProperties().getProperty(key);
                if (val == null) {
                    val = pro.getProperty(key);
                }
            }
            return val;
        }
    }


}




