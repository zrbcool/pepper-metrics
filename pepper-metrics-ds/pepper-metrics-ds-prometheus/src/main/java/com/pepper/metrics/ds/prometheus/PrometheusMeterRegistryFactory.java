package com.pepper.metrics.ds.prometheus;

import com.pepper.metrics.core.MeterRegistryFactory;
import com.pepper.metrics.core.extension.SpiMeta;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * @author zhangrongbincool@163.com
 * @version 19-8-9
 */
@SpiMeta(name = "promMeterRegistryFactory")
public class PrometheusMeterRegistryFactory implements MeterRegistryFactory {
    private static final PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    static {
        try {
            final int port = NumberUtils.toInt(System.getProperty("pepper.port"), 9146);
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/metrics", httpExchange -> {
                String response = prometheusRegistry.scrape();
                httpExchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            });
            new Thread(server::start).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MeterRegistry createMeterRegistry() {
        return prometheusRegistry;
    }
}
