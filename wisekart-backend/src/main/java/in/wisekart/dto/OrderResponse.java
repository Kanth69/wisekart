package in.wisekart.dto;

import in.wisekart.entity.OrderStatus;
import in.wisekart.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponse {

    private final Long orderId;
    private final String orderNumber;
    private final BigDecimal totalAmount;
    private final OrderStatus orderStatus;
    private final PaymentStatus paymentStatus;
    private final LocalDateTime createdAt;
    private final List<OrderItemResponse> items;
}
