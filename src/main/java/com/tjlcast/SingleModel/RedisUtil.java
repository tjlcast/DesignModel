package com.tjlcast.SingleModel;

/**
 * Created by tangjialiang on 2017/10/31.
 *
 */

import com.tjlcast.SingleModel.others.Device;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisUtil {

    protected static Logger logger = Logger.getLogger(RedisUtil.class);

    //Redis服务器IP
    private static String ADDR_ARRAY = "10.108.218.64,10.108.218.64" ; //FileUtil.getPropertyValue("/properties/redis.properties", "server");

    //Redis的端口号
    private static int PORT = 6379 ; //FileUtil.getPropertyValueInt("/properties/redis.properties", "port");

    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = 8 ; //FileUtil.getPropertyValueInt("/properties/redis.properties", "max_active");;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 8 ; //FileUtil.getPropertyValueInt("/properties/redis.properties", "max_idle");;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = -1 ; // FileUtil.getPropertyValueInt("/properties/redis.properties", "max_wait");;

    //超时时间
    private static int TIMEOUT = 4000 ; // FileUtil.getPropertyValueInt("/properties/redis.properties", "timeout");;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true ; // FileUtil.getPropertyValueBoolean("/properties/redis.properties", "test_on_borrow");;

    private static JedisPool jedisPool = null;

    /**
     * redis过期时间,以秒为单位
     */
    public final static int EXRP_HOUR = 60*60;          //一小时
    public final static int EXRP_DAY = 60*60*24;        //一天
    public final static int EXRP_MONTH = 60*60*24*30;   //一个月

    /**
     * 初始化Redis连接池
     */
    private static void initialPool(){
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_ACTIVE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            config.setTestOnReturn(true);
            jedisPool = new JedisPool(config, ADDR_ARRAY.split(",")[0], PORT, TIMEOUT);
        } catch (Exception e) {
            logger.error("First create JedisPool error : "+e);
            try{
                JedisPoolConfig config = new JedisPoolConfig();
                config.setMaxTotal(MAX_ACTIVE);
                config.setMaxIdle(MAX_IDLE);
                config.setMaxWaitMillis(MAX_WAIT);
                config.setTestOnBorrow(TEST_ON_BORROW);
                jedisPool = new JedisPool(config, ADDR_ARRAY.split(",")[1], PORT, TIMEOUT);
            }catch(Exception e2){
                logger.error("Second create JedisPool error : "+e2);
            }
        }
    }


    /**
     * 在多线程环境同步初始化
     */
    private static synchronized void poolInit() {
        if (jedisPool == null) {
            initialPool();
        }
    }


    /**
     * 同步获取Jedis实例
     * @return Jedis
     */
    private synchronized static Jedis getJedis() {
        if (jedisPool == null) {
            poolInit();
        }
        Jedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
            }
        } catch (Exception e) {
            logger.error("Get jedis error : "+e);
        }
        return jedis;
    }


    /**
     * 释放jedis资源
     * @param jedis
     */
    private static void returnResource(final Jedis jedis) {
        if (jedis != null && jedisPool !=null) {
            jedisPool.returnResource(jedis);
        }
    }

    public static void hmset(String uid, Map<String, String> map) {
        Jedis jedis = getJedis();
        try {
            if (map==null || map.entrySet().isEmpty()) {
                logger.info("Set an empty info") ;
                return ;
            }
            jedis.hmset(uid, map) ;
        } catch (Exception e) {
            logger.error("Set key error : " + e);
        } finally{
            returnResource(jedis);
        }
    }

    public static Map<String,String> hmgetAll(String key) {
        Jedis jedis = getJedis();
        try {
            Map<String,String> hmget = jedis.hgetAll(key);
            return hmget ;
        } catch (Exception e) {
            logger.error("get key error : " + key) ;
        }finally{
            returnResource(jedis);
        }
        return null ;
    }

    public static boolean  hmSetOne(String key, String uId, String Token) {
        Jedis jedis = getJedis();
        try {
            long time  = jedis.hset(key, uId, Token);
            return true ;
        } catch (Exception e) {
            logger.error("get key error : " + key) ;
        }finally{
            returnResource(jedis);
        }
        return false;
    }

    public static List<String> hmget(String uid, String ...args) {
        Jedis jedis = getJedis();
        try {
            List<String> hmget = jedis.hmget(uid, args);
            return hmget ;
        } catch (Exception e) {
            logger.error("get key error : " + uid) ;
        }finally{
            returnResource(jedis);
        }
        return null ;
    }

    public static HashMap<String, Device> getAllDevices() {
        HashMap<String, Device> devices = new HashMap<>() ;
        Jedis jedis = getJedis();
        try {
            Set<String> uids = jedis.keys("*");
            for(String uid : uids) {
                List<String> hmget = jedis.hmget(uid, "uid", "deviceAccess", "deviceId", "deviceName", "info");
                devices.put(hmget.get(0), new Device(hmget.get(0),hmget.get(3))) ;
            }
            return devices ;
        } catch (Exception e) {
            logger.error("get all device : " + e) ;
        } finally{
            returnResource(jedis);
        }
        return null ;
    }

    public static void setAllDevices(Map<String, Device> devices) {
        Jedis jedis = getJedis() ;
        try {
            Transaction tx = jedis.multi() ;
            // 事务中某个操作失败，并不会回滚其他操作
            for(Map.Entry<String, Device> et : devices.entrySet()) {
                tx.hmset(et.getKey(), et.getValue().toMap());
            }
            tx.exec();
        } catch (Exception e) {
            logger.error("set all device : " + e) ;
        } finally {
            returnResource(jedis);
        }
    }
}