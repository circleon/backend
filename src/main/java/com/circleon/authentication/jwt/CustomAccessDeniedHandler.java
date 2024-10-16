package com.circleon.authentication.jwt;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.warn("Not Authenticated: {}", accessDeniedException.getMessage());
        log.warn("Request URI: {}", request.getRequestURI());

        CommonResponseStatus forbiddenAccess = CommonResponseStatus.FORBIDDEN_ACCESS;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorMessage(forbiddenAccess.getMessage())
                .errorCode(forbiddenAccess.getCode())
                .build();

        String responseBody = objectMapper.writeValueAsString(errorResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(forbiddenAccess.getHttpStatus());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}
