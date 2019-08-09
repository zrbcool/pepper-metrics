package redis.clients.jedis;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.JedisURIHelper;
import redis.clients.util.Pool;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.net.URI;

/**
 * @author zhangrongbincool@163.com
 * @date 19-8-7
 * @description
 * 定制版JedisPool，基于jedis2.9.0，实现namespace的注入
 */
public class PjedisPool extends Pool<Jedis> {
  public static final String DEFAULT_NAMESPACE = "default";
  private String namespace;

  public PjedisPool() {
    this(Protocol.DEFAULT_HOST, Protocol.DEFAULT_PORT);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host) {
    this(poolConfig, host, Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT, null,
        Protocol.DEFAULT_DATABASE, null);
  }

  public PjedisPool(String host, int port) {
    this(new GenericObjectPoolConfig(), host, port, Protocol.DEFAULT_TIMEOUT, null,
        Protocol.DEFAULT_DATABASE, null);
  }

  /**
   * namespace aware
   * @param host
   * @param namespace
   */
  public PjedisPool(final String host, final String namespace) {
    URI uri = URI.create(host);
    if (JedisURIHelper.isValid(uri)) {
      String h = uri.getHost();
      int port = uri.getPort();
      String password = JedisURIHelper.getPassword(uri);
      int database = JedisURIHelper.getDBIndex(uri);
      boolean ssl = uri.getScheme().equals("rediss");
      this.internalPool = new GenericObjectPool<Jedis>(new PjedisFactory(h, port,
          Protocol.DEFAULT_TIMEOUT, Protocol.DEFAULT_TIMEOUT, password, database, null,
            ssl, null, null, null, namespace), new GenericObjectPoolConfig());
    } else {
      this.internalPool = new GenericObjectPool<Jedis>(new PjedisFactory(host,
          Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT, Protocol.DEFAULT_TIMEOUT, null,
          Protocol.DEFAULT_DATABASE, null, false, null, null, null, namespace), new GenericObjectPoolConfig());
    }
  }

  public PjedisPool(final String host) {
    this(host, "default");
  }

  /**
   * namespace aware
   * @param host
   * @param sslSocketFactory
   * @param sslParameters
   * @param hostnameVerifier
   * @param namespace
   */
  public PjedisPool(final String host, final SSLSocketFactory sslSocketFactory,
                    final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier, final String namespace) {
    URI uri = URI.create(host);
    if (JedisURIHelper.isValid(uri)) {
      String h = uri.getHost();
      int port = uri.getPort();
      String password = JedisURIHelper.getPassword(uri);
      int database = JedisURIHelper.getDBIndex(uri);
      boolean ssl = uri.getScheme().equals("rediss");
      this.internalPool = new GenericObjectPool<Jedis>(new PjedisFactory(h, port,
          Protocol.DEFAULT_TIMEOUT, Protocol.DEFAULT_TIMEOUT, password, database, null, ssl,
            sslSocketFactory, sslParameters, hostnameVerifier, namespace),
          new GenericObjectPoolConfig());
    } else {
      this.internalPool = new GenericObjectPool<Jedis>(new PjedisFactory(host,
          Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT, Protocol.DEFAULT_TIMEOUT, null,
          Protocol.DEFAULT_DATABASE, null, false, null, null, null, namespace), new GenericObjectPoolConfig());
    }
  }

  public PjedisPool(final String host, final SSLSocketFactory sslSocketFactory,
                    final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
    this(host, sslSocketFactory, sslParameters, hostnameVerifier, DEFAULT_NAMESPACE);
  }

  public PjedisPool(final URI uri) {
    this(new GenericObjectPoolConfig(), uri, Protocol.DEFAULT_TIMEOUT);
  }

  /**
   * namespace aware
   * @param uri
   * @param sslSocketFactory
   * @param sslParameters
   * @param hostnameVerifier
   * @param namespace
   */
  public PjedisPool(final URI uri, final SSLSocketFactory sslSocketFactory,
                    final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier, final String namespace) {
    this(new GenericObjectPoolConfig(), uri, Protocol.DEFAULT_TIMEOUT, sslSocketFactory,
        sslParameters, hostnameVerifier, namespace);
  }

  public PjedisPool(final URI uri, final SSLSocketFactory sslSocketFactory,
                    final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
    this(uri, sslSocketFactory, sslParameters, hostnameVerifier, DEFAULT_NAMESPACE);
  }

  public PjedisPool(final URI uri, final int timeout) {
    this(new GenericObjectPoolConfig(), uri, timeout);
  }

  /**
   * namespace aware
   * @param uri
   * @param timeout
   * @param sslSocketFactory
   * @param sslParameters
   * @param hostnameVerifier
   * @param namespace
   */
  public PjedisPool(final URI uri, final int timeout, final SSLSocketFactory sslSocketFactory,
                    final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier, final String namespace) {
    this(new GenericObjectPoolConfig(), uri, timeout, sslSocketFactory, sslParameters,
        hostnameVerifier, namespace);
  }


  public PjedisPool(final URI uri, final int timeout, final SSLSocketFactory sslSocketFactory,
                    final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
    this(uri, timeout, sslSocketFactory, sslParameters, hostnameVerifier, DEFAULT_NAMESPACE);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                    int timeout, final String password) {
    this(poolConfig, host, port, timeout, password, Protocol.DEFAULT_DATABASE, null);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                    int timeout, final String password, final boolean ssl) {
    this(poolConfig, host, port, timeout, password, Protocol.DEFAULT_DATABASE, null, ssl);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                    int timeout, final String password, final boolean ssl,
                    final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
                    final HostnameVerifier hostnameVerifier) {
    this(poolConfig, host, port, timeout, password, Protocol.DEFAULT_DATABASE, null, ssl,
        sslSocketFactory, sslParameters, hostnameVerifier);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port) {
    this(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE, null);
  }

  /**
   * namespace aware
   * @param poolConfig
   * @param host
   * @param port
   * @param namespace
   */
  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port, String namespace) {
    this(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE, null, namespace);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port,
                    final boolean ssl) {
    this(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE, null,
        ssl);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port,
                    final boolean ssl, final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
                    final HostnameVerifier hostnameVerifier) {
    this(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE, null,
        ssl, sslSocketFactory, sslParameters, hostnameVerifier);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port,
                    final int timeout) {
    this(poolConfig, host, port, timeout, null, Protocol.DEFAULT_DATABASE, null);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port,
                    final int timeout, final boolean ssl) {
    this(poolConfig, host, port, timeout, null, Protocol.DEFAULT_DATABASE, null, ssl);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, final int port,
                    final int timeout, final boolean ssl, final SSLSocketFactory sslSocketFactory,
                    final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
    this(poolConfig, host, port, timeout, null, Protocol.DEFAULT_DATABASE, null, ssl,
        sslSocketFactory, sslParameters, hostnameVerifier);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                    int timeout, final String password, final int database) {
    this(poolConfig, host, port, timeout, password, database, null);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                    int timeout, final String password, final int database, final boolean ssl) {
    this(poolConfig, host, port, timeout, password, database, null, ssl);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                    int timeout, final String password, final int database, final boolean ssl,
                    final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
                    final HostnameVerifier hostnameVerifier) {
    this(poolConfig, host, port, timeout, password, database, null, ssl, sslSocketFactory,
        sslParameters, hostnameVerifier);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                    int timeout, final String password, final int database, final String clientName) {
    this(poolConfig, host, port, timeout, timeout, password, database, clientName, false,
        null, null, null);
  }

  /**
   * namespace aware
   * @param poolConfig
   * @param host
   * @param port
   * @param timeout
   * @param password
   * @param database
   * @param clientName
   * @param namespace
   */
  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                    int timeout, final String password, final int database, final String clientName, final String namespace) {
    this(poolConfig, host, port, timeout, timeout, password, database, clientName, false,
            null, null, null, namespace);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                    int timeout, final String password, final int database, final String clientName,
                    final boolean ssl) {
    this(poolConfig, host, port, timeout, timeout, password, database, clientName, ssl,
        null, null, null);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                    int timeout, final String password, final int database, final String clientName,
                    final boolean ssl, final SSLSocketFactory sslSocketFactory,
                    final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
    this(poolConfig, host, port, timeout, timeout, password, database, clientName, ssl,
        sslSocketFactory, sslParameters, hostnameVerifier);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                    final int connectionTimeout, final int soTimeout, final String password, final int database,
                    final String clientName, final boolean ssl, final SSLSocketFactory sslSocketFactory,
                    final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
    super(poolConfig, new PjedisFactory(host, port, connectionTimeout, soTimeout, password,
        database, clientName, ssl, sslSocketFactory, sslParameters, hostnameVerifier, "default"));
  }

  /**
   * namespace aware
   * @param poolConfig
   * @param host
   * @param port
   * @param connectionTimeout
   * @param soTimeout
   * @param password
   * @param database
   * @param clientName
   * @param ssl
   * @param sslSocketFactory
   * @param sslParameters
   * @param hostnameVerifier
   * @param namespace
   */
  public PjedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                    final int connectionTimeout, final int soTimeout, final String password, final int database,
                    final String clientName, final boolean ssl, final SSLSocketFactory sslSocketFactory,
                    final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier, final String namespace) {
    super(poolConfig, new PjedisFactory(host, port, connectionTimeout, soTimeout, password,
            database, clientName, ssl, sslSocketFactory, sslParameters, hostnameVerifier, namespace));
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final URI uri) {
    this(poolConfig, uri, Protocol.DEFAULT_TIMEOUT);
  }

  /**
   * namespace aware
   * @param poolConfig
   * @param uri
   * @param sslSocketFactory
   * @param sslParameters
   * @param hostnameVerifier
   * @param namespace
   */
  public PjedisPool(final GenericObjectPoolConfig poolConfig, final URI uri,
                    final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
                    final HostnameVerifier hostnameVerifier, final String namespace) {
    this(poolConfig, uri, Protocol.DEFAULT_TIMEOUT, sslSocketFactory, sslParameters,
        hostnameVerifier, namespace);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final URI uri,
                    final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
                    final HostnameVerifier hostnameVerifier) {
    this(poolConfig, uri, sslSocketFactory, sslParameters, hostnameVerifier, DEFAULT_NAMESPACE);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final URI uri, final int timeout) {
    this(poolConfig, uri, timeout, timeout);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final URI uri, final int timeout,
                    final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
                    final HostnameVerifier hostnameVerifier, String namespace) {
    this(poolConfig, uri, timeout, timeout, sslSocketFactory, sslParameters, hostnameVerifier);
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final URI uri,
                    final int connectionTimeout, final int soTimeout) {
    super(poolConfig, new PjedisFactory(uri, connectionTimeout, soTimeout, null, false,
        null, null, null));
  }

  /**
   * namespace aware
   * @param poolConfig
   * @param uri
   * @param connectionTimeout
   * @param soTimeout
   * @param sslSocketFactory
   * @param sslParameters
   * @param hostnameVerifier
   * @param namespace
   */
  public PjedisPool(final GenericObjectPoolConfig poolConfig, final URI uri,
                    final int connectionTimeout, final int soTimeout, final SSLSocketFactory sslSocketFactory,
                    final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier, final String namespace) {
    super(poolConfig, new PjedisFactory(uri, connectionTimeout, soTimeout, null,
        (uri.getScheme() !=null && uri.getScheme().equals("rediss")), sslSocketFactory,
        sslParameters, hostnameVerifier, namespace));
  }

  public PjedisPool(final GenericObjectPoolConfig poolConfig, final URI uri,
                    final int connectionTimeout, final int soTimeout, final SSLSocketFactory sslSocketFactory,
                    final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
    this(poolConfig, uri, connectionTimeout, soTimeout, sslSocketFactory, sslParameters, hostnameVerifier, DEFAULT_NAMESPACE);
  }

  @Override
  public Jedis getResource() {
    Jedis jedis = super.getResource();
    jedis.setDataSource(this);
    return jedis;
  }

  /**
   * @deprecated starting from Jedis 3.0 this method will not be exposed. Resource cleanup should be
   *             done using @see {@link Jedis#close()}
   */
  @Override
  @Deprecated
  public void returnBrokenResource(final Jedis resource) {
    if (resource != null) {
      returnBrokenResourceObject(resource);
    }
  }

  /**
   * @deprecated starting from Jedis 3.0 this method will not be exposed. Resource cleanup should be
   *             done using @see {@link Jedis#close()}
   */
  @Override
  @Deprecated
  public void returnResource(final Jedis resource) {
    if (resource != null) {
      try {
        resource.resetState();
        returnResourceObject(resource);
      } catch (Exception e) {
        returnBrokenResource(resource);
        throw new JedisException("Could not return the resource to the pool", e);
      }
    }
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }
}
