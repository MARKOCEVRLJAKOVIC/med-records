package dev.marko.MedRecords.security;

import dev.marko.MedRecords.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtService {

    private final JwtConfig jwtConfig;

    @Value("${spring.jwt.secret}")
    private String secret;

    public Jwt generateToken(User user){
        return generateAccessToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public Jwt generateRefreshToken(User user){
        return generateAccessToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    public Jwt generateAccessToken(User user, long tokenExpiration){

        var claims =  Jwts.claims()
                .subject(user.getId().toString())
                .add("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .build();

        return new Jwt(claims, jwtConfig.getSecretKey());

    }

    public Jwt parseToken(String token){
        try{

            var claims = getClaims(token);
            return new Jwt(claims, jwtConfig.getSecretKey());

        } catch (JwtException ex) {
            return null;
        }

    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token){
        try{

            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getExpiration().after(new Date());

        }
        catch (JwtException ex){

            return false;

        }
    }

}
