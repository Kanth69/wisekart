package in.wisekart.service;

import in.wisekart.dto.WishlistResponse;
import java.util.List;

public interface WishlistService {

    List<WishlistResponse> getWishlistForCurrentUser();

    WishlistResponse addProductToWishlist(Long productId);

    void removeProductFromWishlist(Long productId);
}
