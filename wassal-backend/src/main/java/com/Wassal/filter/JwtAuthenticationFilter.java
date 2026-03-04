package com.Wassal.filter;

import com.Wassal.security.UserDetailsImpl;
import com.Wassal.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/*
 * Intercept request and extract Bearer token from Header
 * Call JwtService to check signature
 * Populate SecurityContextHolder
*/

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        //Check if authHeader is empty (no Bearer Token) -> Delegate it to filterChain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        //Set jwt
        final String jwt = authHeader.substring(7); //Bearer <jwt>
        //Check jwt signature validity
        if (jwtService.isTokenValid(jwt)) {
            String userIdStr = jwtService.extractSubject(jwt);
            Long userId = Long.parseLong(userIdStr);
            String email = jwtService.extractEmail(jwt);
            List<String> roles = jwtService.extractRoles(jwt);
            //Check if userId is set and user is not authenticated
            if(SecurityContextHolder.getContext().getAuthentication() == null) {
                //Convert List<String> to List<SimpleGrantedAuthority>
                var authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                //Create userDetails object
                UserDetailsImpl userDetails = new UserDetailsImpl(
                        userId,
                        email,
                        "", //password
                        authorities
                );

                //Create UsernamePasswordAuthenticationToken
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //Populate SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
