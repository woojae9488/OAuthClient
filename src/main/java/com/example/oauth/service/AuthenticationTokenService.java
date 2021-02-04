package com.example.oauth.service;

import com.example.oauth.config.token.AuthenticationTokenProperties;
import com.example.oauth.model.AuthenticationTokenType;
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
public class AuthenticationTokenService {
    private static final String JWT_SUBJECT_PREFIX = "USER/";
    private final AuthenticationTokenProperties tokenProperties;

    // TODO : change expiration msec by token type
    public String createToken(AuthenticationTokenType tokenType, SocialUser socialUser) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenProperties.getTokenExpirationMsec());

        TokenAttributes tokenAttributes = TokenAttributes.extract(tokenType, socialUser);
        assert tokenAttributes != null;

        return Jwts.builder()
                .setSubject(JWT_SUBJECT_PREFIX + socialUser.getId())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .setClaims(tokenAttributes.getAttributesMap())
                .signWith(SignatureAlgorithm.HS256, tokenProperties.getTokenSecret())
                .compact();
    }

    public SocialUser getPseudoSocialUserFromToken(AuthenticationTokenType tokenType, String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(tokenProperties.getTokenSecret())
                .parseClaimsJws(token)
                .getBody();

        TokenAttributes tokenAttributes = TokenAttributes.restore(tokenType, claims);
        assert tokenAttributes != null;

        return tokenAttributes.getPseudoSocialUser();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(tokenProperties.getTokenSecret())
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
