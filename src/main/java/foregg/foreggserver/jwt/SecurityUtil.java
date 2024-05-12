package foregg.foreggserver.jwt;

import foregg.foreggserver.apiPayload.code.status.ErrorStatus;
import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class SecurityUtil {

    public static String getCurrentUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();

            return username;
        } catch (Exception e) {
            throw new UserHandler(ErrorStatus.USER_NOT_FOUND);
        }
    }

    public static boolean ifCurrentUserIsHusband() {
        try {
            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            // authorities 컬렉션 반복
            for (GrantedAuthority authority : authorities) {
                // ROLE_HUSBAND와 같은 역할을 찾으면 true 반환
                if (authority.getAuthority().equals("ROLE_HUSBAND")) {
                    return true;
                }
            }
            // ROLE_HUSBAND와 같은 역할을 찾지 못했을 때 false 반환
            return false;
        } catch (Exception e) {
            throw new UserHandler(ErrorStatus.USER_NOT_FOUND);
        }
    }
}