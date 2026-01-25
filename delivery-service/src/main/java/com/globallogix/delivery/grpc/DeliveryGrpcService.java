package com.globallogix.delivery.grpc;

import com.globallogix.delivery.entity.Delivery;
import com.globallogix.delivery.entity.DeliveryStatus;
import com.globallogix.delivery.repository.DeliveryRepository;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import java.util.List;

@GrpcService
@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryGrpcService extends DeliveryServiceGrpc.DeliveryServiceImplBase{

    private final DeliveryRepository deliveryRepository;

    @Override
    public void findAvailableDeliveries(Empty request, StreamObserver<DeliveryListResponse> responseObserver) {
        log.info("gRPC findAvailableDeliveries started");
        try {
            List<Delivery> deliveries = deliveryRepository.findByStatusAndCourierIdIsNull(DeliveryStatus.IN_PROGRESS);
            log.info("gRPC findAvailableDeliveries find {} available deliveries", deliveries.size());
            if (deliveries.isEmpty()) {
                log.info("gRPC: Нет доступных доставок");
                DeliveryListResponse emptyResponse = DeliveryListResponse.newBuilder()
                        .build();
                responseObserver.onNext(emptyResponse);
                responseObserver.onCompleted();
                return;
            }
            List<DeliveryResponse> responses = deliveries.stream().map(DeliveryGrpcService::mapFromEntityToResponse).toList();
            DeliveryListResponse deliveryListResponse = DeliveryListResponse.newBuilder().addAllResponses(responses).build();
            responseObserver.onNext(deliveryListResponse);
            responseObserver.onCompleted();

            log.info("gRPC method: answer sent");

        } catch (Exception e){
            log.error("Error in gRPC method");
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").withCause(e).asRuntimeException());
        }
    }

    private static DeliveryResponse mapFromEntityToResponse(Delivery delivery){
        return DeliveryResponse.newBuilder()
                .setId(delivery.getId() != null ? delivery.getId().toString() : "")
                .setSenderId(delivery.getSenderId() != null ?
                        delivery.getSenderId().toString() : "")
                .setCourierId(delivery.getCourierId() != null ?
                        delivery.getCourierId().toString() : "")
                .setFromAirport(delivery.getFromAirport() != null ?
                        delivery.getFromAirport() : "")
                .setToAirport(delivery.getToAirport() != null ?
                        delivery.getToAirport() : "")
                .setDescription(delivery.getDescription() != null ?
                        delivery.getDescription() : "")
                .setWeight(delivery.getWeight() != null ?
                        delivery.getWeight().toString() : "")
                .setDimensions(delivery.getDimensions() != null ?
                        delivery.getDimensions() : "")
                .setPrice(delivery.getPrice() != null ?
                        delivery.getPrice().toString() : "0.0")
                .setStatus(delivery.getStatus() != null ?
                        delivery.getStatus().toString() : "")
                .build();
    }


}