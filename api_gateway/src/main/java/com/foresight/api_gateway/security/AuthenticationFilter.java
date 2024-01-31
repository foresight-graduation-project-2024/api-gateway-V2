package com.foresight.api_gateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foresight.api_gateway.exception.ErrorCode;
import com.foresight.api_gateway.exception.ErrorDetails;
import com.foresight.api_gateway.exception.RuntimeErrorCodedException;
import com.foresight.api_gateway.model.UserInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component

public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {


    @Autowired
    private  JwtService jwtService;


    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) ->
        {
            UserInfo userInfo = new UserInfo();

            if(RouteValidator.shouldAuthenticate(exchange.getRequest()))
            {

                try{
                    Optional<String> header = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("Authorization"));
                    String token  = header.orElseThrow(()-> new RuntimeErrorCodedException(ErrorCode.MISSING_AUTHORIZATION_HEADER));

                     userInfo = jwtService.extractUserInfo(token);



                }
                catch (RuntimeErrorCodedException ex)
                {
                    String body  = makeExceptionResponse(ex);
                    ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    response.setStatusCode(HttpStatus.BAD_REQUEST);

                    return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));


                }


            }


            HttpHeaders modifiedHeaders = new HttpHeaders();
            if (userInfo.getEmail() != null && userInfo.getRole()!=null) {
                modifiedHeaders.add("loggedInUserEmail", userInfo.getEmail());
                modifiedHeaders.add("loggedInUserRole", userInfo.getRole());
            }

            // Create a new mutable request with modified headers
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.addAll(modifiedHeaders)).build();

            // Continue with the modified request in the filter chain
            return chain.filter(exchange.mutate().request(modifiedRequest).build());


//            exchange.getRequest().getHeaders().add("loggedInUserEmail",userInfo.getEmail());
//            exchange.getRequest().getHeaders().add("loggedInUserRole",userInfo.getRole());
//            return chain.filter(exchange);
        });
    }

    public static class Config{}


    private String makeExceptionResponse(RuntimeErrorCodedException ex) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setCode(ex.getErrorCode().getCode());
        errorDetails.setTimeStamp(new Date().getTime());
        ObjectMapper mapper = new ObjectMapper();
        String body = null;
        try {
            body = mapper.writeValueAsString(errorDetails);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return body;

    }

}
