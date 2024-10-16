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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {


        log.warn("Not Authenticated: {}", authException.getMessage());
        log.warn("Request URI: {}", request.getRequestURI());

        CommonResponseStatus accessTokenInvalid = CommonResponseStatus.ACCESS_TOKEN_INVALID;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorMessage(accessTokenInvalid.getMessage())
                .errorCode(accessTokenInvalid.getCode())
                .build();

        String responseBody = objectMapper.writeValueAsString(errorResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(accessTokenInvalid.getHttpStatus());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}
