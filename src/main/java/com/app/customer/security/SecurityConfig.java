package com.app.customer.security;

import com.app.customer.security.filter.CustomAuthenticationFilter;
import com.app.customer.security.filter.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.app.customer.security.Roles.ADMIN_ROLE;
import static com.app.customer.security.Roles.USER_ROLE;
import static com.app.customer.security.SecurityConstants.LOGIN;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserDetailsService userDetailsService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Value("${custom.provided.secret.app}")
  private String accessKey;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    final CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
    customAuthenticationFilter.setFilterProcessesUrl(LOGIN);
    customAuthenticationFilter.setAccessKey(accessKey);

    http
        .headers()
        .xssProtection()
        .and()
        .contentSecurityPolicy("script-src 'self'");

    http.csrf().disable();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.authorizeRequests()
        .antMatchers(LOGIN, "/v1/token/refresh/**").permitAll()
        .antMatchers(POST, "/v1/api/users/**").hasAnyAuthority(ADMIN_ROLE.name())
        .antMatchers(DELETE, "/v1/api/users/**").hasAnyAuthority(ADMIN_ROLE.name())
        .antMatchers(GET, "/v1/api/users/**").hasAnyAuthority(ADMIN_ROLE.name())
        .antMatchers(PUT, "/v1/api/users/**").hasAnyAuthority(ADMIN_ROLE.name())
        .antMatchers(PUT, "/v1/api/users/**").hasAnyAuthority(ADMIN_ROLE.name(), USER_ROLE.name())
        .antMatchers(POST, "/v1/api/customers").hasAnyAuthority(USER_ROLE.name(), ADMIN_ROLE.name())
        .antMatchers(PUT, "/v1/api/customers").hasAnyAuthority(USER_ROLE.name(), ADMIN_ROLE.name())
        .antMatchers(DELETE, "/v1/api/customers").hasAnyAuthority(USER_ROLE.name(), ADMIN_ROLE.name())
        .antMatchers(GET, "/v1/api/customers").hasAnyAuthority(USER_ROLE.name(), ADMIN_ROLE.name());

    http.addFilter(customAuthenticationFilter);
    CustomAuthorizationFilter filter = new CustomAuthorizationFilter();
    filter.setAccessKey(accessKey);
    http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
}
