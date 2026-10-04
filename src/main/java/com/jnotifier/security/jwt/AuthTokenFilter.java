package com.jnotifier.security.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jnotifier.app.JNotifierConstants;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jnotifier.services.impl.UserDetailsServiceImpl;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException ex) {
            logger.warn("JWT token is expired: {}", ex.getMessage());
            // For public/auth/app routes, allow the request to proceed unauthenticated
            // so that Spring Security's permitAll() rules can still apply.
            String requestPath = request.getServletPath();
            String publicBase = JNotifierConstants.API_BASE_URL;
            if (requestPath.startsWith(publicBase + "/public/")
                    || requestPath.startsWith(publicBase + "/auth/")
                    || requestPath.startsWith(publicBase + "/app/")
                    || requestPath.startsWith(publicBase + "/downloads/")
                    || requestPath.equals("/")
                    || requestPath.equals("/error")) {
                filterChain.doFilter(request, response);
                return;
            }
            handleJwtError(response);
            return;
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                return null;
            }
            Optional<Cookie> accessCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("accessToken"))
                    .findFirst();

            headerAuth = accessCookie.map(Cookie::getValue).orElse(null);
            return headerAuth;
        }

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }

    private void handleJwtError(HttpServletResponse response) throws IOException {
        response.setStatus(403);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", "Forbidden");
        errorDetails.put("message", "Token expired");
        errorDetails.put("status", 403);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorDetails);
    }
}
