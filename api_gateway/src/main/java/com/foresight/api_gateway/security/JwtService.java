package com.foresight.api_gateway.security;



import com.foresight.api_gateway.exception.ErrorCode;
import com.foresight.api_gateway.exception.RuntimeErrorCodedException;
import com.foresight.api_gateway.model.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;


@Component
public class JwtService {

    @Value("${jwt.secretKey}")
    private String SECRET;




    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private Claims extractAllClaims(String token) throws JwtException {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();



    }



    public UserInfo extractUserInfo(String token)
    {
        String email,role;
        try{
            final Claims claims = extractAllClaims(token);
             email =  claims.get("sub",String.class);
             role = claims.get("role", String.class);

        }
        catch (JwtException ex)
        {
            throw new RuntimeErrorCodedException(ErrorCode.INVALID_AUTHENTICATION_TOKEN);
        }


        return new UserInfo(role,email);

    }



}
