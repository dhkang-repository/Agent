package org.example.agent.global.security;

import lombok.RequiredArgsConstructor;
import org.example.agent.domain.auth.dto.AuthUserDto;
import org.example.agent.domain.auth.repository.AuthUserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {

    private final AuthUserRepository userRepository;

    @Override
    public SecurityAuthUser loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthUserDto user = AuthUserDto.from(userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email)));
        return new SecurityAuthUser(
                user.getId(), user.getName(), user.getPassword(), user.getEmail(), List.of()
        );
    }
}
