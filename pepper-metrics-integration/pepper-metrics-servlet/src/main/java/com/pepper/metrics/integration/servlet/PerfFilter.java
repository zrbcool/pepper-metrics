package com.pepper.metrics.integration.servlet;

import com.pepper.metrics.core.Profiler;
import com.pepper.metrics.core.Stats;
import com.pepper.metrics.integration.servlet.utils.HttpUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *  Servlet系 Web容器过滤器，性能数据收集统计
 *
 * @author zhiminxu
 * @version 2019-08-13
 */
public class PerfFilter implements Filter {
    private static final Stats PROFILER_STAT = Profiler.Builder
            .builder()
            .type("http")
            .subType("in")
            .namespace("default")
            .build();

    private static final Stats PROFILER_STAT_HTTPSTATUS = Profiler.Builder
            .builder()
            .type("http-status")
            .subType("in")
            .namespace("default")
            .build();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest sRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse sResponse = (HttpServletResponse) servletResponse;

        String url = HttpUtil.getPatternUrl(sRequest.getRequestURI());

        long begin = System.currentTimeMillis();

        String[] tags = {"method", sRequest.getMethod(), "url", url, "type", "exception"};

        PROFILER_STAT.incConc(tags);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (IOException | ServletException e) {
            PROFILER_STAT.error(tags);
            throw e;
        } finally {
            PROFILER_STAT.decConc(tags);
            String httpStatus = String.valueOf(sResponse.getStatus());
            String[] httpStatusTags = {"method", sRequest.getMethod(), "url", url, "type", "status", "status", httpStatus};
            PROFILER_STAT.observe(System.currentTimeMillis() - begin, TimeUnit.MILLISECONDS, tags);
            PROFILER_STAT_HTTPSTATUS.observe(System.currentTimeMillis() - begin, TimeUnit.MILLISECONDS, httpStatusTags);
        }
    }

    @Override
    public void destroy() { }
}
