package com.example.oauth.service;

import com.example.oauth.config.token.AuthorizationTokenProperties;
import com.example.oauth.model.TokenType;
import com.example.oauth.model.TokenAttributes;
import com.example.oauth.repository.model.SocialUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthorizationTokenService {
    private static final String JWT_SUBJECT_PREFIX = "USER/";
    private final AuthorizationTokenProperties tokenProperties;

    public String createToken(TokenType tokenType, SocialUser socialUser) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenProperties.getTokenExpirationMsec(tokenType));

        TokenAttributes tokenAttributes = TokenAttributes.extract(tokenType, socialUser);
        assert tokenAttributes != null;

        return Jwts.builder()
                .setSubject(JWT_SUBJECT_PREFIX + socialUser.getId())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .setClaims(tokenAttributes.getAttributesMap())
                .signWith(SignatureAlgorithm.HS256, tokenProperties.getTokenSecret(tokenType))
                .compact();
    }

    public SocialUser getPseudoSocialUserFromToken(TokenType tokenType, String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(tokenProperties.getTokenSecret(tokenType))
                .parseClaimsJws(token)
                .getBody();

        TokenAttributes tokenAttributes = TokenAttributes.restore(tokenType, claims);
        assert tokenAttributes != null;
        return tokenAttributes.getPseudoSocialUser();
    }

    public boolean validateToken(TokenType tokenType, String token) {
        try {
            Jwts.parser()
                    .setSigningKey(tokenProperties.getTokenSecret(tokenType))
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
