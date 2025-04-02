package com.zbkj.common.filter;

import com.zbkj.common.wrapper.ContentCachingRequestWrapper;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @ClassName LedgerReportFilter
 * @Description 用于重复读取requestBody
 * @Author HZW
 * @Date 2023/2/22 18:05
 * @Version 1.0
 */
@Component
public class LedgerReportFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String contentType = httpServletRequest.getContentType();
        if (contentType != null && contentType.startsWith("multipart/")) {
            // 文件上传类型
            chain.doFilter(request, response);
            return;
        }
        String requestURI = httpServletRequest.getRequestURI();
        if (requestURI.contains("/publicly") || requestURI.contains("/druid")) {
            chain.doFilter(request, response);
            return;
        }
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
        chain.doFilter(requestWrapper, response);
    }

    @Override
    public void destroy() {

    }
}
