package antifraud.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public class SecurityAuthority implements GrantedAuthority {
    private final String roles;

    @Override
    public String getAuthority() {
        return roles;
    }
}
