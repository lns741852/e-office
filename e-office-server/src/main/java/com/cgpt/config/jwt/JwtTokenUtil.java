package com.cgpt.config.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtToken工具类
 *
 * @author zhanglishen
 * @since 1.0.0
 */
@Component
public class JwtTokenUtil {
    //荷载claim的名称
    private static final String CLAIM_KEY_USERNAME = "sub";
    //荷载的创建时间
    private static final String CLAIM_KEY_CREATED = "created";
    // jwt令牌的秘钥
    @Value("${jwt.secret}")
    private String secret;
    //jwt的实效时间
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 根据用户信息生成token
     *
     * @param userDetails
     * @return
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 從TOKEN獲取用戶訊息
     * @param token
     * @return
     */
    public String getUserNameFromToken(String token){
        String username = null;
        Claims claims = getClaimsFromToken(token);
        try {
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * token是否有效
     * @param token
     * @param userDetails
     * @return
     */
    public Boolean validateToken(String token,UserDetails userDetails){
        String username = getUserNameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 判断token是否可以被刷新
     * @param token
     * @return
     */
    public Boolean canRefresh(String token){
        return !isTokenExpired(token);
    }

    /**
     * 刷新token
     * @param token
     * @return
     */
    public String refreshToken(String token){
        Claims claims = getClaimsFromToken(token);
        claims.put(CLAIM_KEY_CREATED,new Date());
        return generateToken(claims);
    }

    /**
     * 判斷TOKEN是否過期
     * @param token
     * @return
     */
    private boolean isTokenExpired(String token) {
        Date expireDate = getExpiredDateFromToken(token);
        //到期時間在當前時間前
        return expireDate.before(new Date());
    }

    /**
     * 從TOKEN獲取到期時間
     * @param token
     * @return
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 根據TOKEN獲取荷載
     * @param token
     * @return
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return claims;
    }

    /**
     * 生成JWTToken
     *
     * @param claims
     * @return
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpiration())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 生成token实效时间
     *
     * @return
     */
    private Date generateExpiration() {
        // 当前时间+配置时间
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

}
