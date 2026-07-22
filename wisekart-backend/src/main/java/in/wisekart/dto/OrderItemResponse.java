package in.wisekart.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemResponse {

    private final Long id;
    private final Long productId;
    private final String productName;
    private final Integer quantity;
    private final BigDecimal price;
    private final BigDecimal subtotal;
}
