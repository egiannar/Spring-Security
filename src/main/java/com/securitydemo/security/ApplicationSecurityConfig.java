package com.securitydemo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.concurrent.TimeUnit;

import static com.securitydemo.security.ApplicationUserPermission.COURSE_WRITE;
import static com.securitydemo.security.ApplicationUserRole.*;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)//an annotation to enable method auth
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * This is a method to configure the basic authentication of the app
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//           .csrf()
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())//how csrf tokens are created. The csrf cookies are inaccessible to client side scripts
//           .and()
           .csrf().disable()
           .authorizeRequests()
           .antMatchers("/", "index", "/css/*", "/js/*").permitAll()//for these patterns we permit all
           .antMatchers("/api/**").hasRole(STUDENT.name())//The api/** endpoints is accessed by students only. This is role based authentication.
//                .antMatchers(HttpMethod.DELETE, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission())//Permission based auth. It indicates that is is for a delete method
//                .antMatchers(HttpMethod.POST, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
//                .antMatchers(HttpMethod.PUT, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
//                .antMatchers("/management/api/**").hasAnyRole(ADMIN.name(), ADMINTRAINEE.name())
           .anyRequest()
           .authenticated()
           .and()
//           .httpBasic();//basic auth
           .formLogin()//form based auth
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/courses", true)//redirect to page after successful login
           .and()
           .rememberMe()//extend the session with remember me
                .tokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(21))//change the cookie duration to 21 says. By default it lasts 14 days
                .key("somethingverysecure")//generate md5 key.The input string is the key
           .and()
           .logout()
                .logoutUrl("/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))//this line means whenever i go to this url with this method I logout
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/login");
    }

    /**
     * This method shows how I retrieve methods from the db
     *
     */
    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails leuterisUser = User.builder()
                .username("leuteris")
                .password(passwordEncoder.encode("password"))
//                .roles(STUDENT.name())//used for role based auth
                .authorities(STUDENT.getGrantedAuthorities())//permission based auth
                .build();

        UserDetails tom = User.builder()
                .username("tom")
                .password(passwordEncoder.encode("password123"))
//                .roles(ADMINTRAINEE.name())
                .authorities(ADMIN.getGrantedAuthorities())//permission based auth
                .build();

        UserDetails linda = User.builder()
                .username("linda")
                .password(passwordEncoder.encode("password123"))
//                .roles(ADMIN.name())
                .authorities(ADMINTRAINEE.getGrantedAuthorities())
                .build();

        return new InMemoryUserDetailsManager(
          leuterisUser,
          tom,
          linda
        );

    }


}
