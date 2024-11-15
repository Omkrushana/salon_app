package com.paarsh.salonkatta.config;

import java.io.IOException;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.paarsh.salonkatta.serviceImpl.JwtService;
import com.paarsh.salonkatta.serviceImpl.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {


    private final Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtService jwtService;

    public JwtRequestFilter(UserDetailsServiceImpl userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }


    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request, @SuppressWarnings("null") HttpServletResponse response, @SuppressWarnings("null") FilterChain chain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        System.out.println(requestTokenHeader+"  request header ");

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtService.extractUsername(jwtToken);
                logger.info("the username factched from jwt!!" + username);

            } catch (IllegalArgumentException e) {
                logger.info("Illegal Argument while fetching the username !!" + e.getMessage());

            } catch (ExpiredJwtException e) {
                logger.info("Given jwt token is expired !!" , e);
            } catch (MalformedJwtException e) {
                logger.info("Some changed has done in token !! Invalid Token",e);

            } catch (Exception e) {
                logger.info("Some error got",e);

            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String" + jwtToken);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//fetch user detail from username
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwtToken, userDetails)) {
                //set the authentication

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}
