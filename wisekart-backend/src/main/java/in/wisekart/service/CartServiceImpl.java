package in.wisekart.service;

import in.wisekart.dto.AddToCartRequest;
import in.wisekart.dto.CartItemResponse;
import in.wisekart.dto.CartResponse;
import in.wisekart.dto.UpdateCartRequest;
import in.wisekart.entity.Cart;
import in.wisekart.entity.Product;
import in.wisekart.entity.User;
import in.wisekart.exception.ResourceNotFoundException;
import in.wisekart.repository.CartRepository;
import in.wisekart.repository.ProductRepository;
import in.wisekart.repository.UserRepository;
import in.wisekart.security.UserPrincipal;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public CartResponse getCartForCurrentUser() {
        User currentUser = getCurrentUser();
        List<Cart> carts = cartRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        return buildCartResponse(carts);
    }

    @Override
    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        User currentUser = getCurrentUser();
        Product product = getActiveProductById(request.getProductId());
        validateQuantity(request.getQuantity());
        validateStock(product, request.getQuantity());

        Cart existingCart = cartRepository.findByUserIdAndProductId(currentUser.getId(), product.getId()).orElse(null);
        if (existingCart != null) {
            int newQuantity = existingCart.getQuantity() + request.getQuantity();
            validateStock(product, newQuantity);
            existingCart.setQuantity(newQuantity);
            return buildCartResponse(cartRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId()));
        }

        Cart cart = new Cart();
        cart.setUser(currentUser);
        cart.setProduct(product);
        cart.setQuantity(request.getQuantity());
        cartRepository.save(cart);

        return buildCartResponse(cartRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId()));
    }

    @Override
    @Transactional
    public CartResponse updateCart(Long cartId, UpdateCartRequest request) {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartId));

        if (!cart.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Cart item not found with id: " + cartId);
        }

        validateQuantity(request.getQuantity());
        validateStock(cart.getProduct(), request.getQuantity());
        cart.setQuantity(request.getQuantity());

        return buildCartResponse(cartRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId()));
    }

    @Override
    @Transactional
    public void deleteCartItem(Long cartId) {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartId));

        if (!cart.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Cart item not found with id: " + cartId);
        }

        cartRepository.delete(cart);
    }

    @Override
    @Transactional
    public void clearCart() {
        User currentUser = getCurrentUser();
        List<Cart> carts = cartRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        cartRepository.deleteAll(carts);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }

        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userPrincipal.getId()));
    }

    private Product getActiveProductById(Long productId) {
        return productRepository.findByIdAndIsActiveTrue(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Active product not found with id: " + productId));
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }

    private void validateStock(Product product, int quantity) {
        if (product.getStock() == null || quantity > product.getStock()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock");
        }
    }

    private CartResponse buildCartResponse(List<Cart> carts) {
        List<CartItemResponse> items = carts.stream().map(this::toCartItemResponse).toList();
        Integer totalQuantity = items.stream().mapToInt(CartItemResponse::getQuantity).sum();
        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(items)
                .totalItems(items.size())
                .totalQuantity(totalQuantity)
                .totalAmount(totalAmount)
                .build();
    }

    private CartItemResponse toCartItemResponse(Cart cart) {
        Product product = cart.getProduct();
        BigDecimal unitPrice = product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cart.getQuantity()));

        return CartItemResponse.builder()
                .cartId(cart.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productSlug(product.getSlug())
                .thumbnailUrl(product.getThumbnailUrl())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .quantity(cart.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}
