//package org.example.util;
//
//import io.jsonwebtoken.*;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import java.util.Base64;
//import java.util.Date;
//
//public class JwtUtil {
//    private static final String SECRET_KEY = "MySuperSecretKeyForJWT123456"; // 必须 > 32 字节
//    private static final byte[] SECRET_KEY_BYTES = Base64.getEncoder().encode(SECRET_KEY.getBytes());
//    private static final SecretKey key = new SecretKeySpec(SECRET_KEY_BYTES, "HmacSHA256");
//
//    private static final long EXPIRATION_TIME = 86400000; // 1 天
//
//    // 生成 JWT
//    public static String generateToken(String username) {
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(SignatureAlgorithm.HS256, key)
//                .compact();
//    }
//
//    // 解析 JWT
//    public static Claims parseToken(String token) {
//        return Jwts.parser()
//                .setSigningKey(key)
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    // 验证 JWT 是否有效
//    public static boolean validateToken(String token) {
//        try {
//            parseToken(token);
//            return true;
//        } catch (ExpiredJwtException | MalformedJwtException | SignatureException e) {
//            return false;
//        }
//    }
//}
