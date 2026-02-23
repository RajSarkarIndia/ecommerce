package com.ecommerce.Gateway.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Component
public class UserValidationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest,
                                    HttpServletResponse httpResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String url = httpRequest.getRequestURI();
        System.out.println(url);

        // ✅ Public endpoints (unchanged logic)
        if (url.equals("/login") ||
                url.equals("/user/register") ||
                url.startsWith("/product/view/") ||
                url.startsWith("/payment/webhook") ||
                 url.startsWith("/product/all")   ) {

            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }

        JwtUtil util = new JwtUtil();
        String token = null;

        // ✅ Read JWT from cookie
        if (httpRequest.getCookies() != null) {
            for (Cookie cookie : httpRequest.getCookies()) {
                if ("Authorization".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        // ✅ Validate token
        if (token == null || !util.validateToken(token)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String finalToken = token;
        httpRequest.setAttribute("Authorization", "Bearer " + finalToken);

        // ✅ Inject Authorization header for downstream services
        HttpServletRequest wrappedRequest =
                new HttpServletRequestWrapper(httpRequest) {
                    @Override
                    public String getHeader(String name) {
                        if ("Authorization".equalsIgnoreCase(name)) {
                            return "Bearer " + finalToken;
                        }
                        return super.getHeader(name);
                    }

                    @Override
                    public Enumeration<String> getHeaders(String name) {
                        if ("Authorization".equalsIgnoreCase(name)) {
                            return Collections.enumeration(
                                    List.of("Bearer " + finalToken)
                            );
                        }
                        return super.getHeaders(name);
                    }

                    @Override
                    public Enumeration<String> getHeaderNames() {
                        List<String> names = Collections.list(super.getHeaderNames());
                        names.add("Authorization");
                        return Collections.enumeration(names);
                    }

                };

        filterChain.doFilter(wrappedRequest, httpResponse);
    }
}
