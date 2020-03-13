package redis.clients.jedis;

import com.pepper.metrics.core.extension.ExtensionLoader;
import com.pepper.metrics.integration.jedis.JedisProxyFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import redis.clients.jedis.exceptions.InvalidURIException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.JedisURIHelper;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * 定制版PjedisFactory，基于jedis2.9.0，实现namespace的注入，及为Jedis对象包装动态代理
 *
 * @author zhangrongbincool@163.com
 * @version 19-8-7
 */
class PjedisFactory implements PooledObjectFactory<Jedis> {
  private final AtomicReference<HostAndPort> hostAndPort = new AtomicReference<HostAndPort>();
  private final int connectionTimeout;
  private final int soTimeout;
  private final String password;
  private final int database;
  private final String clientName;
  private final boolean ssl;
  private final SSLSocketFactory sslSocketFactory;
  private SSLParameters sslParameters;
  private HostnameVerifier hostnameVerifier;
  private String namespace = "default";

  public PjedisFactory(final String host, final int port, final int connectionTimeout,
                      final int soTimeout, final String password, final int database, final String clientName,
                      final boolean ssl, final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
                      final HostnameVerifier hostnameVerifier) {
    this.hostAndPort.set(new HostAndPort(host, port));
    this.connectionTimeout = connectionTimeout;
    this.soTimeout = soTimeout;
    this.password = password;
    this.database = database;
    this.clientName = clientName;
    this.ssl = ssl;
    this.sslSocketFactory = sslSocketFactory;
    this.sslParameters = sslParameters;
    this.hostnameVerifier = hostnameVerifier;
    if (StringUtils.isNotEmpty(JedisPropsHolder.NAMESPACE.get())) {
      this.namespace = JedisPropsHolder.NAMESPACE.get();
    }
  }

  public PjedisFactory(final URI uri, final int connectionTimeout, final int soTimeout,
                      final String clientName, final boolean ssl, final SSLSocketFactory sslSocketFactory,
                      final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
    if (!JedisURIHelper.isValid(uri)) {
      throw new InvalidURIException(String.format(
              "Cannot open Redis connection due invalid URI. %s", uri.toString()));
    }

    this.hostAndPort.set(new HostAndPort(uri.getHost(), uri.getPort()));
    this.connectionTimeout = connectionTimeout;
    this.soTimeout = soTimeout;
    this.password = JedisURIHelper.getPassword(uri);
    this.database = JedisURIHelper.getDBIndex(uri);
    this.clientName = clientName;
    this.ssl = ssl;
    this.sslSocketFactory = sslSocketFactory;
    this.sslParameters = sslParameters;
    this.hostnameVerifier = hostnameVerifier;
    if (StringUtils.isNotEmpty(JedisPropsHolder.NAMESPACE.get())) {
      this.namespace = JedisPropsHolder.NAMESPACE.get();
    }
  }

  public void setHostAndPort(final HostAndPort hostAndPort) {
    this.hostAndPort.set(hostAndPort);
  }

  @Override
  public void activateObject(PooledObject<Jedis> pooledJedis) throws Exception {
    final BinaryJedis jedis = pooledJedis.getObject();
    if (jedis.getDB() != database) {
      jedis.select(database);
    }
  }

  @Override
  public void destroyObject(PooledObject<Jedis> pooledJedis) throws Exception {
    final BinaryJedis jedis = pooledJedis.getObject();
    if (jedis.isConnected()) {
      try {
        try {
          jedis.quit();
        } catch (Exception e) {
        }
        jedis.disconnect();
      } catch (Exception e) {

      }
    }
  }

  private static final Class[] classes = {String.class, int.class, int.class,
          int.class, boolean.class, SSLSocketFactory.class,
          SSLParameters.class, HostnameVerifier.class};
  private static final JedisProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(JedisProxyFactory.class).getExtension("cglib");

  @Override
  public PooledObject<Jedis> makeObject() throws Exception {
    final HostAndPort hostAndPort = this.hostAndPort.get();
//    final Jedis jedis = new Jedis(hostAndPort.getHost(), hostAndPort.getPort(), connectionTimeout,
//        soTimeout, ssl, sslSocketFactory, sslParameters, hostnameVerifier);

    final Jedis jedis = proxyFactory.getProxy(Jedis.class, namespace, classes, new Object[]{
            hostAndPort.getHost(), hostAndPort.getPort(), connectionTimeout,
            soTimeout, ssl, sslSocketFactory, sslParameters, hostnameVerifier
    });

    try {
      jedis.connect();
      if (null != this.password) {
        jedis.auth(this.password);
      }
      if (database != 0) {
        jedis.select(database);
      }
      if (clientName != null) {
        jedis.clientSetname(clientName);
      }
    } catch (JedisException je) {
      jedis.close();
      throw je;
    }
    return new DefaultPooledObject<>(jedis);
  }

  @Override
  public void passivateObject(PooledObject<Jedis> pooledJedis) throws Exception {
    // TODO maybe should select db 0? Not sure right now.
  }

  @Override
  public boolean validateObject(PooledObject<Jedis> pooledJedis) {
    final BinaryJedis jedis = pooledJedis.getObject();
    try {
      HostAndPort hostAndPort = this.hostAndPort.get();

      String connectionHost = jedis.getClient().getHost();
      int connectionPort = jedis.getClient().getPort();

      return hostAndPort.getHost().equals(connectionHost)
          && hostAndPort.getPort() == connectionPort && jedis.isConnected()
          && jedis.ping().equals("PONG");
    } catch (final Exception e) {
      return false;
    }
  }
}