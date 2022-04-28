package com.breadme.breadcloud.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * JWT 加解密工具类
 *
 * @author breadme@foxmail.com
 * @date 2022/4/28 2:43
 */
public class JwtUtils {
    public static final String APP_SECRET = "kihLAoUwi38LjQsh0nHIl3kmp2od7BN1";

    private JwtUtils() {

    }

    /**
     * 使用 id 和盐获取 token
     *
     * @param id 用户 id
     * @param salt 盐
     * @return token
     */
    public static String token(String id, String salt) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("bread-cloud")
                .setIssuedAt(new Date())
                .claim("id", id)
                .claim("salt", salt)
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();
    }

    /**
     * 使用 token 获取 id
     *
     * @param token token
     * @return id
     */
    public static String id(String token) {
        if (!StringUtils.hasText(token)) {
            return "";
        }
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("id");
    }
}
