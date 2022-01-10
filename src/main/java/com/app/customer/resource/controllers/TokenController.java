package com.app.customer.resource.controllers;

import com.app.customer.domain.RoleEntity;
import com.app.customer.exception.ServiceException;
import com.app.customer.security.Roles;
import com.app.customer.security.SecurityConstants;
import com.app.customer.service.domain.User;
import com.app.customer.service.user.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.app.customer.security.SecurityConstants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequestMapping("/v1/api")
@RequiredArgsConstructor
@RestController
public class TokenController {

  private final UserService userService;

  //TODO refactor this
  @GetMapping("/token/refresh")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) {

    String authorizationHeader = request.getHeader(AUTHORIZATION);
    if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      try {

        String refreshToken = authorizationHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(refreshToken);
        String username = decodedJWT.getSubject();
        Optional<User> user = userService.getUserByUsernameAndStatusActive(username);
        final Map<String, String> tokens = new HashMap<>();
        if (user.isPresent()) {
          String accessToken = JWT.create()
              .withSubject(user.get().getUsername())
              .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
              .withIssuer(request.getRequestURL().toString())
              .withClaim(ROLES, user.get().getRoles().stream().map(Roles::name).collect(Collectors.toList()))
              .sign(algorithm);
          tokens.put("access_token", accessToken);
          tokens.put("refresh_token", refreshToken);
          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
          new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        }

      } catch (final Exception ex) {
        throw new ServiceException(ex.getMessage(), ex);
      }
    }
  }
}
