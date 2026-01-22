package santaOps.santaLog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import santaOps.santaLog.domain.User;
import santaOps.santaLog.repository.jpa.UserRepository;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String email){
        return userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException(email));
    }


}
