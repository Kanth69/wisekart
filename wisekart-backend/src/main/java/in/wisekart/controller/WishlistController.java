package in.wisekart.controller;

import in.wisekart.dto.WishlistResponse;
import in.wisekart.service.WishlistService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping("/api/wishlist")
    public ResponseEntity<List<WishlistResponse>> getWishlist() {
        return ResponseEntity.ok(wishlistService.getWishlistForCurrentUser());
    }

    @PostMapping("/api/wishlist/{productId}")
    public ResponseEntity<WishlistResponse> addProductToWishlist(@PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.addProductToWishlist(productId));
    }

    @DeleteMapping("/api/wishlist/{productId}")
    public ResponseEntity<Void> removeProductFromWishlist(@PathVariable Long productId) {
        wishlistService.removeProductFromWishlist(productId);
        return ResponseEntity.noContent().build();
    }
}
