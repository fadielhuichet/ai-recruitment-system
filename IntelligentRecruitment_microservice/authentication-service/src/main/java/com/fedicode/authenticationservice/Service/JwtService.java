package com.fedicode.authenticationservice.Service;

import com.fedicode.authenticationservice.model.Role;
import com.fedicode.authenticationservice.model.Status;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {

    // Prefer config-server property (jwt.secret), then env (JWT_SECRET), then local base64 fallback.
    @Value("${jwt.secret:${JWT_SECRET:ZGV2LXNlY3JldC1jaGFuZ2UtbWUtdGhpcy1pcy1iYXNlNjQ=}}")
    private String secretKey;

    public String generateToken(int id, String email, String role, String status) {

        Map<String,Object> claims= new HashMap<>();
//        claims.put("recruiter_id",id)
          claims.put("role",role);

          if("RECRUITER".equals(role) && status!=null ||"CANDIDATE".equals(role) && status!=null){
              claims.put("status",status);
          }

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10 ))
                .and()
                .signWith(getSigningKey())
                .compact();
    }

    public String extractRole(String token){
        return extractClaims(token, claims -> claims.get("role",String.class));

    }
    private SecretKey getSigningKey(){
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secretKey);
        } catch (IllegalArgumentException ex) {
            // Support raw secrets from env files when not Base64-encoded.
            keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractEmail(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims,T> claimResolver) {
        final Claims claims= extractAllClaims(token);
        return claimResolver.apply(claims);

    }

    public boolean isTokenValid(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return tokenEmail.equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
