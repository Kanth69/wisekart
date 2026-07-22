package in.wisekart.dto;

import in.wisekart.entity.OrderStatus;
import in.wisekart.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminOrderResponse {

    private final Long orderId;
    private final String orderNumber;
    private final Long userId;
    private final String userEmail;
    private final BigDecimal totalAmount;
    private final OrderStatus orderStatus;
    private final PaymentStatus paymentStatus;
    private final LocalDateTime createdAt;
}
