package foregg.foreggserver.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import foregg.foreggserver.apiPayload.code.ErrorReasonDTO;
import foregg.foreggserver.apiPayload.code.status.ErrorStatus;
import foregg.foreggserver.service.redisService.RedisService;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.LOGOUT_USER;

@RequiredArgsConstructor
@Component
@Configuration
public class JwtAuthFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 헤더에서 토큰 받아오기
        String jwt = jwtTokenProvider.resolveToken2((HttpServletRequest) request);

        if (jwt != null && doNotLogoutOrWithdrawal(jwt)) {
            // try {
            jwtTokenProvider.getClaim(jwt);
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private boolean doNotLogoutOrWithdrawal(String accessToken) {
        if (!redisService.hasKeyBlackList(accessToken)) {
            return true;
        }
        return false;
    }
}
