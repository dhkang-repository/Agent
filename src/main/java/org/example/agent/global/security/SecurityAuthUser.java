package org.example.agent.global.security;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@ToString(callSuper = false)
@Getter
public class SecurityAuthUser extends User {

    private final Long userId;
    private final String username;
    private final String password;
    private final String email;

    public SecurityAuthUser(Long userId,
                            String username,
                            String password,
                            String email,
                            Collection<GrantedAuthority> authorities) {
        super(email, password, authorities);
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
