package com.example.projectv1.config;

import com.example.projectv1.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();
        final String jwt;
        if (requestURI.startsWith("/api/v1/auth/authenticated")) {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                writeJsonResponse(response, "Unauthorized: Missing or invalid Authorization header");
                return;
            }
        } else if (!requestURI.startsWith("/api/v1/auth/authenticated")) {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        jwt = authHeader.substring(7);

        try {
            String userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (MalformedJwtException e) {
            // Handle MalformedJwtException
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJsonResponse(response, "Unauthorized: Malformed token");
            return;
        } catch (JwtException e) {
            // Handle other JWT related exceptions
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJsonResponse(response, "Unauthorized: User not found");
            return;
        } catch (NoSuchElementException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJsonResponse(response, e.getMessage());
            return;
        } catch (IllegalStateException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJsonResponse(response, e.getMessage());
            return;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJsonResponse(response, "Internal Server Error");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeJsonResponse(HttpServletResponse response, String message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(mapper.writeValueAsString(Collections.singletonMap("error", message)));
    }
}

