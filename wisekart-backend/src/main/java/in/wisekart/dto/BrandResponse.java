package in.wisekart.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BrandResponse {

    private final Long id;
    private final String name;
    private final String slug;
    private final String description;
    private final String logoUrl;
    private final Boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
