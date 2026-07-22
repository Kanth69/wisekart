package in.wisekart.service;

import in.wisekart.dto.AdminOrderResponse;
import in.wisekart.dto.UpdateOrderStatusRequest;
import org.springframework.data.domain.Page;

public interface AdminOrderService {

    Page<AdminOrderResponse> getAllOrders(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            String orderStatus,
            String paymentStatus,
            String keyword);

    AdminOrderResponse getOrderById(Long orderId);

    AdminOrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

    AdminOrderResponse cancelOrder(Long orderId);
}
