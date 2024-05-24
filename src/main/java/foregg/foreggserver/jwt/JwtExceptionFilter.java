package foregg.foreggserver.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(req, res); // go to 'JwtAuthenticationFilter'
        }
        catch (SignatureException ex) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, res, "잘못된 JWT 서명입니다.");
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException ex) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, res, "만료된 JWT 토큰입니다.");
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException ex) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, res, "지원되지 않는 JWT 토큰입니다.");
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException ex) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, res, "JWT 토큰이 잘못되었습니다.");
            logger.info("JWT 토큰이 잘못되었습니다.");
        } catch (JwtException ex) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, res, "JWT 오류입니다");
        }
    }


    public void setErrorResponse(HttpStatus status, HttpServletResponse res, String message) throws IOException {
        res.setStatus(status.value());
        res.setContentType("application/json; charset=UTF-8");

        JwtExceptionResponse jwtExceptionResponse = new JwtExceptionResponse(message, status.getReasonPhrase(), status.value());
        res.getWriter().write(jwtExceptionResponse.convertToJson());
    }
}