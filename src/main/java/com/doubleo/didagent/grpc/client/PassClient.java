package com.doubleo.didagent.grpc.client;

import com.doubleo.passservice.grpc.server.PassServiceGrpc;
import com.doubleo.passservice.grpc.server.UpdateConnectionStatusRequest;
import com.doubleo.passservice.grpc.server.UpdateConnectionStatusResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PassClient {

    @GrpcClient("pass-service")
    private PassServiceGrpc.PassServiceBlockingStub blockingStub;

    public UpdateConnectionStatusResponse updateConnectionStatus(
            String tenantId, Long passId, String connectionId) {

        try {
            UpdateConnectionStatusRequest.Builder builder =
                    UpdateConnectionStatusRequest.newBuilder();
            UpdateConnectionStatusRequest request =
                    builder.setTenantId(tenantId)
                            .setPassId(passId)
                            .setConnectionId(connectionId)
                            .build();
            return blockingStub.updateConnectionState(request);

        } catch (StatusRuntimeException e) {
            log.error("Pass Connection Status 업데이트 실패: {}", e.getMessage());
            throw new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getMessage()).withCause(e));
        }
    }
}
