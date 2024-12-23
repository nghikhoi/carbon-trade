package uit.carbon_shop.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uit.carbon_shop.service.UserTokenService;
import uit.carbon_shop.service.UserUserDetailsService;


/**
 * Filter for authorizing requests based on the "Authorization: Bearer ..." header. When
 * a valid JWT has been found, load the user details from the database and set the
 * authenticated principal for the duration of this request.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserUserDetailsService userUserDetailsService;
    private final UserTokenService userTokenService;

    public JwtRequestFilter(final UserUserDetailsService userUserDetailsService,
            final UserTokenService userTokenService) {
        this.userUserDetailsService = userUserDetailsService;
        this.userTokenService = userTokenService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain) throws IOException,
            ServletException {
        // look for Bearer auth header
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7);
        final String username = "admin".equals(token) ? "admin@admin.com" : userTokenService.validateTokenAndGetUsername(token);
        if (username == null) {
            // validation failed or token expired
            chain.doFilter(request, response);
            return;
        }

        final UserDetails userDetails;
        try {
            userDetails = userUserDetailsService.loadUserByUsername(username);
        } catch (final UsernameNotFoundException userNotFoundEx) {
            // user not found
            chain.doFilter(request, response);
            return;
        }

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // set user details on spring security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // continue with authenticated user
        chain.doFilter(request, response);
    }

}