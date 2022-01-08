package com.app.customer.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.app.customer.security.SecurityConstants.ACCESS_TOKEN;
import static com.app.customer.security.SecurityConstants.PASSWORD;
import static com.app.customer.security.SecurityConstants.REFRESH_TOKEN;
import static com.app.customer.security.SecurityConstants.ROLES;
import static com.app.customer.security.SecurityConstants.USERNAME;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    String username = request.getParameter(USERNAME);
    String password = request.getParameter(PASSWORD);
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);

    return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
      throws IOException, ServletException {

    User user = (User)authentication.getPrincipal();

    //TODO how should i save this secret
    Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

    String accessToken = JWT.create().withSubject(user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) //10 minutes
        .withIssuer(request.getRequestURL().toString())
        .withClaim(ROLES, user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
        .sign(algorithm);

    String refreshToken = JWT.create().withSubject(user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // 30 minutes
        .withIssuer(request.getRequestURL().toString())
        .withClaim(ROLES, user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
        .sign(algorithm);

    final Map<String, String> tokens = new HashMap<>();
    tokens.put(ACCESS_TOKEN, accessToken);
    tokens.put(REFRESH_TOKEN, refreshToken);

    response.setContentType(APPLICATION_JSON_VALUE);
    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    super.successfulAuthentication(request, response, chain, authentication);
  }
}
