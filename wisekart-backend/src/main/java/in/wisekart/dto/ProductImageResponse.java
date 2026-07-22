package in.wisekart.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductImageResponse {

    private final Long id;
    private final String imageUrl;
    private final String altText;
    private final Integer displayOrder;
    private final Boolean isPrimary;
    private final Long productId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
