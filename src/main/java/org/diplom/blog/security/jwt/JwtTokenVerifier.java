package org.diplom.blog.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.diplom.blog.security.SecurityConstant;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Andrey.Kazakov
 * @date 03.10.2020
 */
public class JwtTokenVerifier extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        String authorizationHeader = request.getHeader(SecurityConstant.AUTHORIZATION_HEADER);

        if(authorizationHeader != null && authorizationHeader.startsWith(SecurityConstant.TOKEN_PREFIX)) {
            try {

                String token  =   authorizationHeader.split("\\s", 2)[1];
                Jws<Claims> claimsJws = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(SecurityConstant.SECRET_KEY.getBytes()))//??.setSigningKey(SecurityConstant.SECRET_KEY.getBytes())
                        .build()
                        .parseClaimsJws(token);

                Claims claimBody = claimsJws.getBody();
                String username = claimBody.getSubject();

                if (username != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String,String>> authorities = (List<Map<String,String>>) claimBody.get("authorities");
                    Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                                                                .map(m -> new SimpleGrantedAuthority(m.get("authority")))
                                                                .collect(Collectors.toSet());

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                                                    username, null, simpleGrantedAuthorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                e.printStackTrace();
            }
        }

        chain.doFilter(request, response);
    }
}
