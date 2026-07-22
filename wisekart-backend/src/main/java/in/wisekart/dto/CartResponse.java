package in.wisekart.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartResponse {

    private final List<CartItemResponse> items;
    private final Integer totalItems;
    private final Integer totalQuantity;
    private final BigDecimal totalAmount;
}
