package in.wisekart.service;

import in.wisekart.dto.AddressRequest;
import in.wisekart.dto.AddressResponse;
import in.wisekart.entity.Address;
import in.wisekart.entity.User;
import in.wisekart.exception.ResourceNotFoundException;
import in.wisekart.repository.AddressRepository;
import in.wisekart.repository.UserRepository;
import in.wisekart.security.UserPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public List<AddressResponse> getAddresses() {
        User currentUser = getCurrentUser();
        return addressRepository.findByUserId(currentUser.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AddressResponse addAddress(AddressRequest request) {
        User currentUser = getCurrentUser();
        if (request.isDefault() && hasDefaultAddress(currentUser.getId())) {
            clearDefaultAddress(currentUser.getId());
        }

        Address address = new Address();
        address.setUser(currentUser);
        address.setFullName(request.getFullName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setLandmark(request.getLandmark());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setDefault(request.isDefault());

        Address saved = addressRepository.save(address);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long id, AddressRequest request) {
        User currentUser = getCurrentUser();
        Address address = addressRepository.findByUserIdAndId(currentUser.getId(), id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));

        if (request.isDefault() && !address.isDefault()) {
            clearDefaultAddress(currentUser.getId());
            address.setDefault(true);
        } else if (!request.isDefault() && address.isDefault()) {
            address.setDefault(false);
        }

        address.setFullName(request.getFullName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setLandmark(request.getLandmark());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());

        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        User currentUser = getCurrentUser();
        Address address = addressRepository.findByUserIdAndId(currentUser.getId(), id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(Long id) {
        User currentUser = getCurrentUser();
        Address address = addressRepository.findByUserIdAndId(currentUser.getId(), id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));

        if (!address.isDefault()) {
            clearDefaultAddress(currentUser.getId());
            address.setDefault(true);
            address = addressRepository.save(address);
        }

        return toResponse(address);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }

        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userPrincipal.getId()));
    }

    private boolean hasDefaultAddress(Long userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId).isPresent();
    }

    private void clearDefaultAddress(Long userId) {
        addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(address -> {
                    address.setDefault(false);
                    addressRepository.save(address);
                });
    }

    private AddressResponse toResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .phoneNumber(address.getPhoneNumber())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.isDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
