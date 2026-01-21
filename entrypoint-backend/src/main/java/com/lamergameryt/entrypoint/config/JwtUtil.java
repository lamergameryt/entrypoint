package com.lamergameryt.entrypoint.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lamergameryt.entrypoint.model.Role;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

	private final Key key;
	private final long expiration;

	public JwtUtil(@Value("${security.jwt.secret}") String secret,
			@Value("${security.jwt.expiration-ms}") long expiration) {

		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expiration = expiration;
	}

	// Generate JWT token
	public String generateToken(String username, Set<Role> roles) {
		return Jwts.builder().setSubject(username)
				.claim("roles", roles.stream().map(Role::getName).collect(Collectors.toList())).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	// Validate JWT token
	public Jws<Claims> validateToken(String token) throws JwtException {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
	}
}
