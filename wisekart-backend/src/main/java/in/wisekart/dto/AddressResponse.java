package in.wisekart.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressResponse {

    private final Long id;
    private final String fullName;
    private final String phoneNumber;
    private final String addressLine1;
    private final String addressLine2;
    private final String landmark;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String country;
    private final boolean isDefault;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
