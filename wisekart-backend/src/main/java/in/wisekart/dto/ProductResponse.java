package in.wisekart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {

    private final Long id;
    private final String name;
    private final String sku;
    private final String slug;
    private final String description;
    private final BigDecimal price;
    private final BigDecimal discountPrice;
    private final Integer stock;
    private final String thumbnailUrl;
    private final Long categoryId;
    private final Long brandId;
    private final Boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
