package space.nov29.cataria.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;


@Service
@Slf4j
public class JwtProvider {

    //為了不要每次驗證都生成新的 key
    private Key key;
    private JwtParser jwtParser;
    @Value("${cataria.config.jwtExpirationMs}")
    private int jwtExpirationMs;

    @PostConstruct
    public void init() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String jwtToken) {
        try {
            jwtParser.parseClaimsJws(jwtToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Jwt validate failure: {}", e.getMessage());
        }
        return false;
    }

    public String getUsernameFromJwtToken(String jwtToken) {
        Claims claims = jwtParser.parseClaimsJws(jwtToken).getBody();
        return claims.getSubject();
    }

}
