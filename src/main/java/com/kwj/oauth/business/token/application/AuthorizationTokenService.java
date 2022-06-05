package com.kwj.oauth.business.token.application;

import com.kwj.oauth.business.token.model.TokenAttributes;
import com.kwj.oauth.business.token.model.TokenType;
import com.kwj.oauth.config.properties.AuthorizationTokenProperties;
import com.kwj.oauth.business.user.infra.SocialUserRepository;
import com.kwj.oauth.business.token.infra.TokenStoreRepository;
import com.kwj.oauth.business.user.domain.SocialUser;
import com.kwj.oauth.business.token.domain.TokenStore;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.AuthenticationFailedException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthorizationTokenService {

    private static final String JWT_SUBJECT_PREFIX = "USER/";

    private final AuthorizationTokenProperties tokenProperties;
    private final TokenStoreRepository tokenStoreRepository;
    private final SocialUserRepository userRepository;

    public String createAccessToken(SocialUser socialUser) {
        return createToken(TokenType.ACCESS_TOKEN, socialUser);
    }

    public String createRefreshToken(SocialUser socialUser, String accessToken) {
        String refreshToken = createToken(TokenType.REFRESH_TOKEN, socialUser);
        Long existTokenStoreId = tokenStoreRepository.findByUserId(socialUser.getId())
                .orElseGet(TokenStore::new)
                .getId();

        TokenStore tokenStore = TokenStore.builder()
                .id(existTokenStoreId)
                .userId(socialUser.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .createTime(System.currentTimeMillis())
                .build();
        tokenStoreRepository.save(tokenStore);

        return refreshToken;
    }

    private String createToken(TokenType tokenType, SocialUser socialUser) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenProperties.getTokenExpirationMillis(tokenType));

        TokenAttributes tokenAttributes = TokenAttributes.extract(tokenType, socialUser);

        return Jwts.builder()
                .setSubject(JWT_SUBJECT_PREFIX + socialUser.getId())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .setClaims(tokenAttributes.getAttributesMap())
                .signWith(SignatureAlgorithm.HS256, tokenProperties.getTokenSecret(tokenType))
                .compact();
    }

    public SocialUser getPseudoSocialUserFromToken(TokenType tokenType, String token) throws AuthenticationFailedException {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(tokenProperties.getTokenSecret(tokenType))
                    .parseClaimsJws(token)
                    .getBody();

            TokenAttributes tokenAttributes = TokenAttributes.restore(tokenType, claims);
            return tokenAttributes.getPseudoSocialUser();
        } catch (Exception e) {
            throw new AuthenticationFailedException();
        }
    }

    public boolean validateToken(TokenType tokenType, String token) {
        try {
            Jwts.parser()
                    .setSigningKey(tokenProperties.getTokenSecret(tokenType))
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String refreshAccessToken(String expiredAccessToken, String refreshToken) throws AuthenticationFailedException {
        if (refreshToken == null) {
            throw new AuthenticationFailedException();
        }

        SocialUser pseudoSocialUser = getPseudoSocialUserFromToken(TokenType.REFRESH_TOKEN, refreshToken);
        TokenStore tokenStore = tokenStoreRepository.findByUserId(pseudoSocialUser.getId())
                .orElseThrow(AuthenticationFailedException::new);

        if (validateRefreshToken(tokenStore, expiredAccessToken, refreshToken)) {
            SocialUser socialUser = userRepository.findById(pseudoSocialUser.getId())
                    .orElseThrow(AuthenticationFailedException::new);

            String accessToken = createToken(TokenType.ACCESS_TOKEN, socialUser);
            reflectNewAccessTokenToTokenStore(tokenStore, accessToken);

            return accessToken;
        } else {
            throw new AuthenticationFailedException();
        }
    }

    private boolean validateRefreshToken(TokenStore tokenStore, String expiredAccessToken, String refreshToken) {
        return expiredAccessToken.equals(tokenStore.getAccessToken())
                && refreshToken.equals(tokenStore.getRefreshToken());
    }

    private void reflectNewAccessTokenToTokenStore(TokenStore tokenStore, String accessToken) {
        tokenStore.setAccessToken(accessToken);
        tokenStore.setUpdateTime(System.currentTimeMillis());

        tokenStoreRepository.save(tokenStore);
    }

}
