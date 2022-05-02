package com.breadme.breadcloud.util;

import com.breadme.breadcloud.exception.BreadCloudException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

/**
 * 安全工具类
 *
 * @author breadme@foxmail.com
 * @date 2022/5/2 14:32
 */
@Slf4j
public class SecurityUtils {
    public static final String JWT_SECRET = "kihLAoUwi38LjQsh0nHIl3kmp2od7BN1";

    /**
     * 使用私钥解密
     *
     * @param src 密文
     * @return 明文
     */
    public static String decrypt(String src) {
        String ret = null;
        try {
            Properties properties = new Properties();
            properties.load(new FileReader("rsa.properties"));
            String rsa = properties.getProperty("rsa");
            if (!StringUtils.hasText(rsa)) {
                log.error("获取私钥失败");
                throw new BreadCloudException(Code.FAIL, "服务器出错啦!请稍后重试!");
            }
            byte[] inputByte = Base64.getDecoder().decode(src);
            byte[] decoded = Base64.getDecoder().decode(rsa);
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            ret = new String(cipher.doFinal(inputByte));
        } catch (Exception e) {
            log.error("解密失败\n", e);
        }
        return ret;
    }

    /**
     * 获取公钥
     *
     * @return 公钥
     */
    public static String getRsaPub() {
        String rsaPub = null;
        try {
            Properties properties = new Properties();
            properties.load(new FileReader("rsa.properties"));
            rsaPub = properties.getProperty("rsa.pub");
            if (!StringUtils.hasText(rsaPub)) {
                genKeyPair();
                rsaPub = properties.getProperty("rsa.pub");
            }
        } catch (Exception e) {
            log.error("获取公钥出错\n", e);
        }
        return rsaPub;
    }

    /**
     * 生成密钥对
     */
    public static void genKeyPair() {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader("rsa.properties"));
            String rsa = properties.getProperty("rsa");
            String rsaPub = properties.getProperty("rsa.pub");
            if (StringUtils.hasText(rsa) && StringUtils.hasText(rsaPub)) {
                return;
            }
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = generator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            rsa = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            rsaPub = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            properties.setProperty("rsa", rsa);
            properties.setProperty("rsa.pub", rsaPub);
            properties.store(new FileOutputStream("rsa.properties"), null);
        } catch (Exception e) {
            log.error("生成密钥出错\n", e);
        }
    }

    /**
     * 根据用户id和盐获取token
     *
     * @param id 用户id
     * @param salt 盐
     * @return token
     */
    public static String getToken(String id, String salt) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("bread-cloud")
                .setIssuedAt(new Date())
                .claim("id", id)
                .claim("salt", salt)
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }

    /**
     * 根据token获取用户id
     *
     * @param token token
     * @return id
     */
    public static String getUserId(String token) {
        if (!StringUtils.hasText(token)) {
            return "";
        }
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String) claims.get("id");
    }

    /**
     * 使用md5加密
     *
     * @param src 明文
     * @return 密文
     */
    public static String digest(String src) {
        return DigestUtils.md5DigestAsHex(src.getBytes(StandardCharsets.UTF_8));
    }

    private SecurityUtils() {

    }
}
