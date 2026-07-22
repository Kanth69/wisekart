package in.wisekart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WishlistResponse {

    private final Long id;
    private final Long productId;
    private final String productName;
    private final String productSlug;
    private final String thumbnailUrl;
    private final BigDecimal price;
    private final BigDecimal discountPrice;
    private final LocalDateTime createdAt;
}
