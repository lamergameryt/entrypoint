package com.lamergameryt.entrypoint.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lamergameryt.entrypoint.model.Role;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String AUTH_HEADER = "Authorization";
	private static final String PREFIX = "Bearer ";

	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getRequestURI();
		String method = request.getMethod();

		String header = request.getHeader(AUTH_HEADER);

		if (header == null || !header.startsWith(PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = header.replace(PREFIX, "");

		try {
			Jws<Claims> claimsJws = jwtUtil.validateToken(token);
			Claims claims = claimsJws.getBody();

			String username = claims.getSubject();
			List<String> roles = (List<String>) claims.get("roles");

			var authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

			var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
			SecurityContextHolder.getContext().setAuthentication(auth);

		} catch (Exception e) {
			log.warn("JWT validation failed: {}", e.getMessage());
		}

		filterChain.doFilter(request, response);
	}

}
