package in.wisekart.controller;

import in.wisekart.dto.AdminOrderResponse;
import in.wisekart.dto.UpdateOrderStatusRequest;
import in.wisekart.service.AdminOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping("/api/admin/orders")
    public ResponseEntity<Page<AdminOrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(adminOrderService.getAllOrders(page, size, sortBy, sortDirection, orderStatus, paymentStatus, keyword));
    }

    @GetMapping("/api/admin/orders/{orderId}")
    public ResponseEntity<AdminOrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(adminOrderService.getOrderById(orderId));
    }

    @PatchMapping("/api/admin/orders/{orderId}/status")
    public ResponseEntity<AdminOrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(adminOrderService.updateOrderStatus(orderId, request));
    }

    @PatchMapping("/api/admin/orders/{orderId}/cancel")
    public ResponseEntity<AdminOrderResponse> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(adminOrderService.cancelOrder(orderId));
    }
}
