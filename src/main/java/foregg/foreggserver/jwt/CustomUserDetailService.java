package foregg.foreggserver.jwt;

import foregg.foreggserver.apiPayload.code.status.ErrorStatus;
import foregg.foreggserver.apiPayload.exception.handler.UserHandler;
import foregg.foreggserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Component
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String keyCode) throws UsernameNotFoundException {
        return userRepository.findByKeyCode(keyCode)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
    }

}