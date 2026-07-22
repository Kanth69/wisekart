package in.wisekart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProductImageRequest {

    @NotBlank(message = "Image URL is required")
    @Size(max = 2048, message = "Image URL must not exceed 2048 characters")
    private String imageUrl;

    @Size(max = 1024, message = "Alt text must not exceed 1024 characters")
    private String altText;

    @NotNull(message = "Display order is required")
    @Min(value = 0, message = "Display order must be zero or positive")
    private Integer displayOrder;

    @NotNull(message = "isPrimary flag is required")
    private Boolean isPrimary;
}
