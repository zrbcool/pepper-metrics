package redis.clients.jedis;

/**
 * @author zhangrongbincool@163.com
 * @date 19-8-16
 */
public class JedisPropsHolder {
    public static final ThreadLocal<String> NAMESPACE = new ThreadLocal<>();
}
