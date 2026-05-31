package com.gth.auth.interfaces.grpc;

import com.gth.auth.application.command.*;
import com.gth.auth.application.command.result.*;
import com.gth.auth.application.handler.*;
import com.gth.auth.application.query.ValidateTokenQuery;
import com.gth.auth.application.query.result.ValidateTokenQueryResult;
import com.gth.auth.domain.exception.*;
import com.gth.auth.interfaces.grpc.mapper.ProtoMapper;
import com.gth.auth.v1.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

    private final RegisterCommandHandler registerHandler;
    private final LoginCommandHandler loginHandler;
    private final RefreshTokenCommandHandler refreshTokenHandler;
    private final LogoutCommandHandler logoutHandler;
    private final ProtoMapper mapper;
    private final ValidateQueryHandler validateTokenHandler;

    @Override
    public void register(RegisterRequest request,
                         StreamObserver<RegisterResponse> responseObserver) {
        try {
            log.info("gRPC register called for email: {}", request.getEmail());

            RegisterCommand command = mapper.toCommand(request);
            RegisterCommandResult result = registerHandler.handle(command);
            RegisterResponse response = mapper.toResponse(result);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("User registered successfully: {}", request.getEmail());

        } catch (EmailAlreadyExistsException e) {
            responseObserver.onError(
                    Status.ALREADY_EXISTS
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        } catch (DomainException e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        } catch (IllegalArgumentException e){
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void login(LoginRequest request,
                         StreamObserver<LoginResponse> responseObserver) {
        try {
            log.info("gRPC login called for email: {}", request.getEmail());

            LoginCommand command = mapper.toCommand(request);
            LoginCommandResult result = loginHandler.handle(command);
            LoginResponse response = mapper.toResponse(result);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("User login successfully: {}", request.getEmail());

        } catch (UserNotFoundException e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        } catch (DomainException e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        } catch (IllegalArgumentException e){
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void refreshToken(RefreshTokenRequest request,
                             StreamObserver<RefreshTokenResponse> responseObserver) {
        try {
            RefreshTokenCommand command = new RefreshTokenCommand(request.getRefreshToken());
            RefreshTokenCommandResult result = refreshTokenHandler.handle(command);

            RefreshTokenResponse response = RefreshTokenResponse.newBuilder()
                    .setAccessToken(result.getAccessToken())
                    .setRefreshToken(result.getRefreshToken())
                    .setExpireIn(result.getExpireIn())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                    Status.UNAUTHENTICATED
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void logout(LogoutRequest request, StreamObserver<LogoutResponse> responseObserver) {
        try {
            LogoutCommand command = new LogoutCommand(request.getRefreshToken());
            LogoutCommandResult result = logoutHandler.handle(command);

            LogoutResponse response = LogoutResponse.newBuilder()
                    .setSuccess(true)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.UNAUTHENTICATED
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void validateToken(ValidateTokenRequest request, StreamObserver<ValidateTokenResponse> responseObserver) {
        ValidateTokenQuery query = new ValidateTokenQuery(request.getToken());
        ValidateTokenQueryResult result = validateTokenHandler.handle(query);

        responseObserver.onNext(ValidateTokenResponse.newBuilder()
                .setValid(result.isValid())
                .setUserId(result.getUserId())
                .setExpiresAt(result.getExpires_at().toEpochMilli())
                .build()
        );
        responseObserver.onCompleted();
    }
}
