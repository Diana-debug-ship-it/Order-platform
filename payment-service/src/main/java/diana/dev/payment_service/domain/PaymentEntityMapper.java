package diana.dev.payment_service.domain;

import diana.dev.payment_service.api.CreatePaymentRequestDto;
import diana.dev.payment_service.api.CreatePaymentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface PaymentEntityMapper {

    PaymentEntity toEntity(CreatePaymentRequestDto requestDto);

    @Mapping(source = "id", target = "paymentId")
    CreatePaymentResponseDto toResponseDto(PaymentEntity entity);

}
