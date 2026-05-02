package com.example.marketplace.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.marketplace.user.User;
import com.example.marketplace.user.UserRepository;

import io.jsonwebtoken.Claims;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository repository;

    public JwtAuthenticationFilter(UserRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;

    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String jwt = authHeader.substring(7);

        try {
            Claims claims = jwtService.extractAllClaims(jwt);
            Long userId = jwtService.extractId(claims);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                if (!jwtService.isTokenExpired(claims)) {
                    Optional<User> optionalUser = repository.findById(userId);
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();

                        UserPrincipal principal = new UserPrincipal(user);

                        if (principal.isEnabled()) {

                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    principal,
                                    null,
                                    principal.getAuthorities());

                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }

                }
            }
        } catch (Exception e) {

        }
        filterChain.doFilter(request, response);
    }

}