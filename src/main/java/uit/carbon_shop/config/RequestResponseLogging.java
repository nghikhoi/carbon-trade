package uit.carbon_shop.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Slf4j
public class RequestResponseLogging extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain) throws IOException,
            ServletException {

        // Create wrapper to cache response content
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        // Create wrapper to cache request content
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);

        long startTime = System.currentTimeMillis();

        try {
            // Process the request through the chain
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // Log after the request has been processed
            String requestBody = new String(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
            String responseBody = new String(responseWrapper.getContentAsByteArray(), responseWrapper.getCharacterEncoding());

            long duration = System.currentTimeMillis() - startTime;

            log.info("\nREQUEST: [{}] {} \nHeaders: {} \nBody: {} \n" +
                            "RESPONSE: Status: {} \nHeaders: {} \nBody: {} \nDuration: {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    getHeaders(request),
                    requestBody,
                    response.getStatus(),
                    getHeaders(response),
                    responseBody,
                    duration);

            // Copy content to the original response
            responseWrapper.copyBodyToResponse();
        }
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Collections.list(request.getHeaderNames())
                .forEach(headerName -> headers.put(headerName, request.getHeader(headerName)));
        return headers;
    }

    private Map<String, String> getHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        response.getHeaderNames()
                .forEach(headerName -> headers.put(headerName, response.getHeader(headerName)));
        return headers;
    }

}
