package antifraud.config;

import antifraud.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests(auth -> {
                    auth.antMatchers(HttpMethod.POST, "/api/auth/user").permitAll();
                    auth.antMatchers("/actuator/shutdown").permitAll();
                    auth.antMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasAuthority("MERCHANT");
                    auth.antMatchers(HttpMethod.GET,"/api/auth/list").hasAnyAuthority("ADMINISTRATOR", "SUPPORT");
                    auth.antMatchers(HttpMethod.DELETE,"/api/auth/user/{username}").hasAuthority("ADMINISTRATOR");
                    auth.antMatchers(HttpMethod.PUT, "/api/auth/access").hasAuthority("ADMINISTRATOR");
                    auth.antMatchers(HttpMethod.PUT, "/api/auth/role").hasAuthority("ADMINISTRATOR");
                    auth.antMatchers(HttpMethod.POST, "/api/antifraud/suspicious-ip").hasAuthority("SUPPORT");
                    auth.antMatchers(HttpMethod.DELETE, "/api/antifraud/suspicious-ip/{ip}").hasAuthority("SUPPORT");
                    auth.antMatchers(HttpMethod.GET, "/api/antifraud/suspicious-ip").hasAuthority("SUPPORT");
                    auth.antMatchers(HttpMethod.POST, "/api/antifraud/stolencard").hasAuthority("SUPPORT");
                    auth.antMatchers(HttpMethod.DELETE, "/api/antifraud/stolencard/{number}").hasAuthority("SUPPORT");
                    auth.antMatchers(HttpMethod.GET, "/api/antifraud/stolencard").hasAuthority("SUPPORT");
                    auth.antMatchers(HttpMethod.GET, "/api/antifraud/history").hasAuthority("SUPPORT");
                    auth.antMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasAuthority("SUPPORT");
                })
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().build();
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
