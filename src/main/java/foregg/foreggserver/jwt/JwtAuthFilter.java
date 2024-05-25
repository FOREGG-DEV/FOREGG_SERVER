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

        // 토큰이 유효하다면
//        if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
//            // 토큰으로부터 유저 정보를 받아
//            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
//
//            // SecurityContext 에 객체 저장
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            logger.info("Security Context에 '{}' 인증 정보를 저장했습니다, uri:" + authentication.getName());
//
//        } else {
//            logger.info("유효한 JWT 토큰이 없습니다, uri: {}");
//        }
//
//        chain.doFilter(request, response);
//    }

        if (jwt != null && doNotLogoutOrWithdrawal(jwt)) {
            // try {
            jwtTokenProvider.getClaim(jwt);
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Security Context에 '{}' 인증 정보를 저장했습니다, uri:" + authentication.getName());
//            } catch (SignatureException e) {
//                logger.info("잘못된 JWT 서명입니다.");
//                throw e;
//            } catch (ExpiredJwtException e) {
//                logger.info("만료된 JWT 토큰입니다.");
//                throw e;
//            } catch (UnsupportedJwtException e) {
//                logger.info("지원되지 않는 JWT 토큰입니다.");
//                throw e;
//            } catch (IllegalArgumentException e) {
//                logger.info("JWT 토큰이 잘못되었습니다.");
//                throw e;
//            }
        }
        chain.doFilter(request, response);
    }

    private boolean doNotLogoutOrWithdrawal(String accessToken) {
        String isLogout = redisService.getData(accessToken);
        if (isLogout == null) {
            return true;
        }
        return false;
    }
}
