package br.com.devcave.s2it.ponto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                if (StringUtils.isEmpty(rawPassword)){
                    return null;
                }
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (StringUtils.isEmpty(rawPassword) && StringUtils.isEmpty(encodedPassword)){
                    return true;
                }
                if (!StringUtils.isEmpty(rawPassword) && !StringUtils.isEmpty(encodedPassword)){
                    return rawPassword.toString().equals(encodedPassword);
                }
                return false;
            }
        };
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder builder,
                                PasswordEncoder passwordEncoder,
                                UserDetailsService userDetailsService) throws Exception {
        builder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

}
