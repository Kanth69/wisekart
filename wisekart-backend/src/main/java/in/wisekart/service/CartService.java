package in.wisekart.service;

import in.wisekart.dto.AddToCartRequest;
import in.wisekart.dto.CartResponse;
import in.wisekart.dto.UpdateCartRequest;

public interface CartService {

    CartResponse getCartForCurrentUser();

    CartResponse addToCart(AddToCartRequest request);

    CartResponse updateCart(Long cartId, UpdateCartRequest request);

    void deleteCartItem(Long cartId);

    void clearCart();
}
