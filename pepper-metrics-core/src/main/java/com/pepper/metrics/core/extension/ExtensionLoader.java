package com.pepper.metrics.core.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhangrongbincool@163.com
 * @version 19-8-10
 * 实现SPI扩展点的加载，由于默认JDK的ServiceLoader无法实现很多复杂元数据的表达
 * 所以使用classloader读取所有jar包中META-INF/services/文件内容的方式实现复杂的表达语义
 * 此处代码参考了Apache Dubbo/Apache Motan两个项目的ExtensionLoader部分，并做了大量精简，在这里对原作者表示感谢
 */
public class ExtensionLoader<T> {
    private static final Logger logger = LoggerFactory.getLogger(ExtensionLoader.class);
    private ConcurrentMap<String, T> singletonInstances = null;
    private ConcurrentMap<String, Class<T>> extensionClasses = null;

    private static final String SPI_LOCATION = "META-INF/services/";
    private ClassLoader classLoader;
    private Class<T> type;
    private volatile boolean init = false;
    private static final Map<Class<?>, ExtensionLoader<?>> extensionLoaders = new ConcurrentHashMap<>();

    private ExtensionLoader(Class<T> type) {
        this(type, Thread.currentThread().getContextClassLoader());
    }

    private ExtensionLoader(Class<T> type, ClassLoader classLoader) {
        this.type = type;
        this.classLoader = classLoader;
    }

    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        ExtensionLoader<T> loader = (ExtensionLoader<T>) extensionLoaders.get(type);
        if (loader == null) {
            loader = initExtensionLoader(type);
        }
        return loader;
    }

    @SuppressWarnings("unchecked")
    private static synchronized <T> ExtensionLoader<T> initExtensionLoader(Class<T> type) {
        ExtensionLoader<T> loader = (ExtensionLoader<T>) extensionLoaders.get(type);
        if (loader == null) {
            loader = new ExtensionLoader<>(type);
            extensionLoaders.putIfAbsent(type, loader);
            loader = (ExtensionLoader<T>) extensionLoaders.get(type);
        }
        return loader;
    }

    @SuppressWarnings("unchecked")
    public List<T> getExtensions() {
        checkAndInit();
        List<T> extensions = new ArrayList<>(extensionClasses.size());
        for (Map.Entry<String, Class<T>> entry : extensionClasses.entrySet()) {
            extensions.add(getExtension(entry.getKey()));
        }
        extensions.sort(new ExtensionOrderComparator<T>());
        return extensions;
    }

    public T getExtension(String name) {
        checkAndInit();
        if (name == null) {
            return null;
        }
        try {
            Spi spi = type.getAnnotation(Spi.class);
            if (spi.scope() == Scope.SINGLETON) {
                return getSingletonInstance(name);
            } else {
                Class<T> clz = extensionClasses.get(name);
                if (clz == null) {
                    return null;
                }
                return clz.newInstance();
            }
        } catch (Exception e) {
            new RuntimeException(type.getName() + ":Error when getExtension " + name, e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private T getSingletonInstance(String name) throws InstantiationException, IllegalAccessException {
        T obj = singletonInstances.get(name);
        if (obj != null) {
            return obj;
        }
        Class<T> clz = extensionClasses.get(name);
        if (clz == null) {
            return null;
        }
        synchronized (singletonInstances) {
            obj = singletonInstances.get(name);
            if (obj != null) {
                return obj;
            }
            obj = clz.newInstance();
            singletonInstances.put(name, obj);
        }
        return obj;
    }

    private void checkAndInit() {
        if (!init) {
            loadExtensionClasses();
        }
    }

    private synchronized void loadExtensionClasses() {
        if (init) {
            return;
        }
        extensionClasses = loadExtensionClasses(SPI_LOCATION);
        singletonInstances = new ConcurrentHashMap<>();
        init = true;
    }

    private ConcurrentMap<String, Class<T>> loadExtensionClasses(String prefix) {
        String fullName = prefix + type.getName();
        List<String> classNames = new ArrayList<String>();
        try {
            Enumeration<URL> urls;
            if (classLoader == null) {
                urls = ClassLoader.getSystemResources(fullName);
            } else {
                urls = classLoader.getResources(fullName);
            }
            if (urls == null || !urls.hasMoreElements()) {
                return new ConcurrentHashMap<>();
            }
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                parseUrl(type, url, classNames);
            }
        } catch (Exception e) {
            throw new RuntimeException("ExtensionLoader loadExtensionClasses error, prefix: " + prefix + " type: " + type.getClass(), e);
        }
        return loadClass(classNames);
    }


    @SuppressWarnings("unchecked")
    private ConcurrentMap<String, Class<T>> loadClass(List<String> classNames) {
        ConcurrentMap<String, Class<T>> map = new ConcurrentHashMap<String, Class<T>>();
        for (String className : classNames) {
            try {
                Class<T> clz;
                if (classLoader == null) {
                    clz = (Class<T>) Class.forName(className);
                } else {
                    clz = (Class<T>) Class.forName(className, true, classLoader);
                }
                checkExtensionType(clz);
                String spiName = getSpiName(clz);
                if (map.containsKey(spiName)) {
                    new RuntimeException(clz.getName() + ":Error spiName already exist " + spiName);
                } else {
                    map.put(spiName, clz);
                }
            } catch (Exception e) {
                logger.error(type.getName() + ":" + "Error load spi class", e);
            }
        }
        return map;

    }

    private void checkExtensionType(Class<T> clz) {
        checkClassPublic(clz);

        checkConstructorPublic(clz);

        checkClassInherit(clz);
    }

    private void checkClassPublic(Class<T> clz) {
        if (!Modifier.isPublic(clz.getModifiers())) {
            new RuntimeException(clz.getName() + ":Error is not a public class");
        }
    }

    private void checkClassInherit(Class<T> clz) {
        if (!type.isAssignableFrom(clz)) {
            new RuntimeException(clz.getName() + ":Error is not instanceof " + type.getName());
        }
    }

    private void checkConstructorPublic(Class<T> clz) {
        Constructor<?>[] constructors = clz.getConstructors();

        if (constructors == null || constructors.length == 0) {
            new RuntimeException(clz.getName() + ":Error has no public no-args constructor");
        }

        for (Constructor<?> constructor : constructors) {
            if (Modifier.isPublic(constructor.getModifiers()) && constructor.getParameterTypes().length == 0) {
                return;
            }
        }

        new RuntimeException(clz.getName() + ":Error has no public no-args constructor");
    }



    public String getSpiName(Class<?> clz) {
        SpiMeta spiMeta = clz.getAnnotation(SpiMeta.class);
        return (spiMeta != null && !"".equals(spiMeta.name())) ? spiMeta.name() : clz.getSimpleName();
    }

    private void parseUrl(Class<T> type, URL url, List<String> classNames) throws ServiceConfigurationError {
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = url.openStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            int indexNumber = 0;
            while ((line = reader.readLine()) != null) {
                indexNumber++;
                parseLine(type, url, line, indexNumber, classNames);
            }
        } catch (Exception x) {
            logger.error(type.getName() + ":" + "Error reading spi configuration file", x);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException y) {
                logger.error(type.getName() + ":" + "Error closing spi configuration file", y);
            }
        }
    }

    private void parseLine(Class<T> type, URL url, String line, int lineNumber, List<String> names) throws IOException,
            ServiceConfigurationError {
        int ci = line.indexOf('#');
        if (ci >= 0) {
            line = line.substring(0, ci);
        }
        line = line.trim();
        if (line.length() <= 0) {
            return;
        }
        if ((line.indexOf(' ') >= 0) || (line.indexOf('\t') >= 0)) {
            throw new RuntimeException(type.getName() + ": " + "Illegal spi configuration-file syntax");
        }
        int cp = line.codePointAt(0);
        if (!Character.isJavaIdentifierStart(cp)) {
            throw new RuntimeException(type.getName() + ": " + url + ": " + line + ": " + "Illegal spi provider-class name: " + line);
        }
        for (int i = Character.charCount(cp); i < line.length(); i += Character.charCount(cp)) {
            cp = line.codePointAt(i);
            if (!Character.isJavaIdentifierPart(cp) && (cp != '.')) {
                throw new RuntimeException(type.getName() + ": " + url + ": " + line + ": " + "Illegal spi provider-class name: " + line);
            }
        }
        if (!names.contains(line)) {
            names.add(line);
        }
    }
}
