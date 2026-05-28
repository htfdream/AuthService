package com.gth.auth.interfaces.grpc;

import com.gth.auth.application.command.*;
import com.gth.auth.application.command.result.*;
import com.gth.auth.application.handler.RegisterCommandHandler;
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
    private final ProtoMapper mapper;

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
        }
    }
}
