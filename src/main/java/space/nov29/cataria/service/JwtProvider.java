package space.nov29.cataria.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import space.nov29.cataria.controller.AuthController;

import javax.annotation.PostConstruct;
import java.security.Key;


@Service
public class JwtProvider {

    //為了不要每次驗證都生成新的 key
    private Key key;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String jwtToken) {
        try {
            jwtParser.parseClaimsJws(jwtToken);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUsernameFromJwtToken(String jwtToken) {
        Claims claims = jwtParser.parseClaimsJws(jwtToken).getBody();
        return claims.getSubject();
    }

}
