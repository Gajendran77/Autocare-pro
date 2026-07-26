package com.autocarepro.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Redirects users to the correct dashboard based on their role
 * immediately after a successful login.
 */
@Component
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_CUSTOMER");

        String redirectUrl = switch (role) {
            case "ROLE_ADMIN" -> "/admin/dashboard";
            case "ROLE_MECHANIC" -> "/mechanic/dashboard";
            default -> "/customer/dashboard";
        };

        response.sendRedirect(redirectUrl);
    }
}
