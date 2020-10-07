package org.diplom.blog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
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
import java.util.stream.Collectors;

/**
 * @author Andrey.Kazakov
 * @date 03.10.2020
 */
@NoArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(SecurityConstant.HEADER_NAME);

        if(header != null && header.startsWith(SecurityConstant.TOKEN_PREFIX)) {
            UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request){
        String token = request.getHeader(SecurityConstant.HEADER_NAME);

        if(token != null) {

            try {      // исключение может быть брошено, если, например, время действия токена истекло
                // 4. Проверяем токен
                Jws<Claims> claims = Jwts.parserBuilder()
                        .setSigningKey(SecurityConstant.SECRET.getBytes())
                        .build()
                        .parseClaimsJws(token.replace(SecurityConstant.TOKEN_PREFIX + " ",""));

                String username = claims.getBody().getSubject();

                if (username != null) {
                    @SuppressWarnings("unchecked")
                    List<String> authorities = (List<String>) claims.getBody().get("authorities");

                    return new UsernamePasswordAuthenticationToken(
                            username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                }

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                e.printStackTrace();
            }
        }

        return null;
    }
}
