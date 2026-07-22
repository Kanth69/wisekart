package in.wisekart.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemResponse {

    private final Long cartId;
    private final Long productId;
    private final String productName;
    private final String productSlug;
    private final String thumbnailUrl;
    private final BigDecimal price;
    private final BigDecimal discountPrice;
    private final Integer quantity;
    private final BigDecimal subtotal;
}
