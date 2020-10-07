package org.diplom.blog.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.diplom.blog.api.request.UserRequest;
import org.diplom.blog.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author Andrey.Kazakov
 * @date 03.10.2020
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    //private final UserService userService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManage) {/*, UserService userServicer*/
        this.authenticationManager = authenticationManage;
        //this.userService = userServicer;
        // По умолчанию, UsernamePasswordAuthenticationFilter "слушает" путь "/login"
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(SecurityConstant.LOGIN_PATH, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try{
            UserRequest userRequest = new ObjectMapper().readValue(request.getInputStream(),
                                                                   UserRequest.class);
            return authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(userRequest.getEmail(),
                                                                    userRequest.getPassword(),
                                                                    Collections.emptyList())
                    );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {
        long now = System.currentTimeMillis();

        /*String token = JWT.create()
                .withSubject(((User)authResult.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstant.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SecurityConstant.SECRET.getBytes()));*/

        @SuppressWarnings("deprecation")
        String token = Jwts.builder()
                .setSubject(authResult.getName())//((User)authResult.getPrincipal()).getUsername()
                // Конвертируем в список строк(важно!)
                .claim("authorities", authResult.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + SecurityConstant.EXPIRATION_TIME))  // milliseconds!
                .signWith(SignatureAlgorithm.HS256, SecurityConstant.SECRET.getBytes())
                .compact();

        response.addHeader(SecurityConstant.HEADER_NAME,  String.format("%s %s",SecurityConstant.TOKEN_PREFIX, token));
        //userService.login()
        //enrichAuthenticationResponse(response, true, authResult, token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        //enrichAuthenticationResponse(response, false, null, null);
    }
}
