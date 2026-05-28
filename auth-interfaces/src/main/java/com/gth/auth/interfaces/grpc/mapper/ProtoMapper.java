package com.gth.auth.interfaces.grpc.mapper;

import com.gth.auth.application.command.*;
import com.gth.auth.application.command.result.*;
import com.gth.auth.v1.*;
import org.springframework.stereotype.Component;

@Component
public class ProtoMapper {

    // Request -> Command
    public RegisterCommand toCommand(RegisterRequest request) {
        return RegisterCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .build();
    }

//    public LoginCommand toCommand(LoginRequest request) {
//        return LoginCommand.builder()
//                .email(request.getEmail())
//                .password(request.getPassword())
//                .userAgent(request.getUserAgent())
//                .ipAddress(request.getIpAddress())
//                .build();
//    }

    // Result -> Response
    public RegisterResponse toResponse(RegisterCommandResult result) {
        return RegisterResponse.newBuilder()
                .setUserId(result.getUserId().toString())
                .setAccessToken(result.getAccessToken() == null ? "" : result.getAccessToken())
                .setRefreshToken(result.getRefreshToken() == null ? "" : result.getRefreshToken())
                .build();
    }

//    public LoginResponse toResponse(LoginCommandResult result) {
//        return LoginResponse.newBuilder()
//                .setUserId(result.getUserId().toString())
//                .setAccessToken(result.getAccessToken())
//                .setRefreshToken(result.getRefreshToken())
//                .setExpiresIn(result.getExpiresIn())
//                .build();
//    }
}
