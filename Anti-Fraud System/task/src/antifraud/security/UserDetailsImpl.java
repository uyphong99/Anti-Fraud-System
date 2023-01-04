package antifraud.security;

import antifraud.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;



public class UserDetailsImpl implements UserDetails {

    private final User user;

    private final List<SecurityAuthority> role;

    private boolean lock;

    public UserDetailsImpl(User user){
        this.user = user;
        this.lock = user.isNonLocked();
        this.role = List.of(new SecurityAuthority(user.getRole()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 4 remaining methods that just return true
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.lock;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }
}
