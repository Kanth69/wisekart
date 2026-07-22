package in.wisekart.service;

import in.wisekart.dto.WishlistResponse;
import in.wisekart.entity.Product;
import in.wisekart.entity.User;
import in.wisekart.entity.Wishlist;
import in.wisekart.exception.DuplicateResourceException;
import in.wisekart.exception.ResourceNotFoundException;
import in.wisekart.repository.ProductRepository;
import in.wisekart.repository.UserRepository;
import in.wisekart.repository.WishlistRepository;
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
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public List<WishlistResponse> getWishlistForCurrentUser() {
        User currentUser = getCurrentUser();
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public WishlistResponse addProductToWishlist(Long productId) {
        User currentUser = getCurrentUser();
        Product product = getActiveProductById(productId);

        if (wishlistRepository.existsByUserIdAndProductId(currentUser.getId(), productId)) {
            throw new DuplicateResourceException("Product is already in the wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(currentUser);
        wishlist.setProduct(product);
        return toResponse(wishlistRepository.save(wishlist));
    }

    @Override
    @Transactional
    public void removeProductFromWishlist(Long productId) {
        User currentUser = getCurrentUser();
        wishlistRepository.deleteByUserIdAndProductId(currentUser.getId(), productId);
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

    private WishlistResponse toResponse(Wishlist wishlist) {
        Product product = wishlist.getProduct();
        return WishlistResponse.builder()
                .id(wishlist.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productSlug(product.getSlug())
                .thumbnailUrl(product.getThumbnailUrl())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .createdAt(wishlist.getCreatedAt())
                .build();
    }
}
