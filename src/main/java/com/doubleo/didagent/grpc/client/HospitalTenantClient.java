package com.doubleo.didagent.grpc.client;

import com.doubleo.tenantservice.domain.tenant.grpc.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HospitalTenantClient {

    @GrpcClient("tenant-service")
    private HospitalTenantServiceGrpc.HospitalTenantServiceBlockingStub blockingStub;

    public UpdateTokensResponse updateTokens(Map<String, String> tokens) {
        UpdateTokensRequest.Builder builder = UpdateTokensRequest.newBuilder();
        try {
            for (Map.Entry<String, String> entry : tokens.entrySet()) {
                TenantWalletToken token =
                        TenantWalletToken.newBuilder()
                                .setTenantId(entry.getKey())
                                .setWalletToken(entry.getValue())
                                .build();
                builder.addTokens(token);
            }
            return blockingStub.updateTokensByTenantId(builder.build());
        } catch (StatusRuntimeException e) {
            throw new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getMessage()).withCause(e));
        }
    }

    public GetTokenResponse getTokenByTenantId(String tenantId) {
        try {
            return blockingStub.getTokenByTenantId(
                    GetTokensRequest.newBuilder().setTenantId(tenantId).build());

        } catch (StatusRuntimeException e) {
            throw new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getMessage()).withCause(e));
        }
    }

    public String getTenantIdByHospitalId(Long hospitalId) {

        try {
            HospitalIdToTenantIdResponse response =
                    blockingStub.getTenantIdByHospitalId(
                            HospitalIdToTenantIdRequest.newBuilder()
                                    .setHospitalId(hospitalId)
                                    .build());
            return response.getTenantId();
        } catch (Exception e) {
            throw new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getMessage()).withCause(e));
        }
    }
}
