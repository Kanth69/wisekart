package in.wisekart.service;

import in.wisekart.dto.AddressRequest;
import in.wisekart.dto.AddressResponse;
import java.util.List;

public interface AddressService {

    List<AddressResponse> getAddresses();

    AddressResponse addAddress(AddressRequest request);

    AddressResponse updateAddress(Long id, AddressRequest request);

    void deleteAddress(Long id);

    AddressResponse setDefaultAddress(Long id);
}
