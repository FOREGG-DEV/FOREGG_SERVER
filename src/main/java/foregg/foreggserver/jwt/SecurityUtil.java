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
            if (authorities.contains("ROLE_HUSBAND")) {
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            throw new UserHandler(ErrorStatus.USER_NOT_FOUND);
        }
    }
}