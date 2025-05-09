package com.circleon.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class AdminCsrfFilter extends OncePerRequestFilter {

    private final String ADMIN_PATH_PREFIX = "/api/admin";
    private final String ALLOWED_DOMAIN = "localhost";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        if(uri.startsWith(ADMIN_PATH_PREFIX)){
            String origin = request.getHeader("Origin");
            String referer = request.getHeader("Referer");

            log.info("Origin: {}, Referer: {}", origin, referer);

            if( !(isValidOrigin(origin) || isValidReferer(referer)) ){
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF suspected");
                log.warn("CSRF suspected - origin: {}, referer: {}", origin, referer);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidOrigin(String origin){
        return origin != null && origin.contains(ALLOWED_DOMAIN);
    }

    private boolean isValidReferer(String referer){
        return referer != null && referer.contains(ALLOWED_DOMAIN);
    }
}
