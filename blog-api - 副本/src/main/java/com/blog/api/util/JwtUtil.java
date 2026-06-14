package com.blog.api.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET="博客项目密钥";
    public static final long EXPIRATIME=7*24*60*60*1000;

    public static String generateToken(String username,Integer id){
        return JWT.create().
                withClaim("id",id).
                withClaim("username",username).
                withExpiresAt(new Date(System.currentTimeMillis()+EXPIRATIME)).
                sign(Algorithm.HMAC256(SECRET));
    }
    public static Integer getId(String token){
        return JWT.require(Algorithm.
                HMAC256(SECRET)).
                build().
                verify(token)
                .getClaim("id")
                .asInt();
    }
    public static boolean verifyToken(String token){
        try{
            JWT.require(Algorithm.HMAC256(SECRET)).build()
                    .verify(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
