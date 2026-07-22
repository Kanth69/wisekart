package in.wisekart.service;

import in.wisekart.dto.AdminOrderResponse;
import in.wisekart.dto.UpdateOrderStatusRequest;
import in.wisekart.entity.Order;
import in.wisekart.entity.OrderStatus;
import in.wisekart.entity.PaymentStatus;
import in.wisekart.exception.ResourceNotFoundException;
import in.wisekart.repository.OrderRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderServiceImpl implements AdminOrderService {

    private static final Set<String> SUPPORTED_SORT_FIELDS = Set.of("createdAt", "totalAmount", "orderNumber");

    private final OrderRepository orderRepository;

    @Override
    public Page<AdminOrderResponse> getAllOrders(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            String orderStatus,
            String paymentStatus,
            String keyword) {
        String normalizedSortBy = sortBy == null ? "createdAt" : sortBy;
        if (!SUPPORTED_SORT_FIELDS.contains(normalizedSortBy)) {
            throw new IllegalArgumentException("Unsupported sort field: " + normalizedSortBy);
        }

        Sort.Direction direction = Sort.Direction.fromString(sortDirection == null ? "DESC" : sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, normalizedSortBy));

        Specification<Order> specification = Specification.where(null);
        if (orderStatus != null && !orderStatus.isBlank()) {
            specification = specification.and(statusEquals(OrderStatus.valueOf(orderStatus.toUpperCase())));
        }
        if (paymentStatus != null && !paymentStatus.isBlank()) {
            specification = specification.and(paymentStatusEquals(PaymentStatus.valueOf(paymentStatus.toUpperCase())));
        }
        if (keyword != null && !keyword.isBlank()) {
            specification = specification.and(orderNumberContains(keyword.trim()));
        }

        return orderRepository.findAll(specification, pageable).map(this::toResponse);
    }

    @Override
    public AdminOrderResponse getOrderById(Long orderId) {
        return toResponse(getOrder(orderId));
    }

    @Override
    @Transactional
    public AdminOrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = getOrder(orderId);
        validateTransition(order.getStatus(), request.getStatus());
        order.setStatus(request.getStatus());
        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public AdminOrderResponse cancelOrder(Long orderId) {
        Order order = getOrder(orderId);
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Order cannot be cancelled after it has shipped");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    private void validateTransition(OrderStatus currentStatus, OrderStatus nextStatus) {
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Cancelled orders cannot be updated");
        }
        if (nextStatus == OrderStatus.CANCELLED) {
            if (currentStatus == OrderStatus.SHIPPED || currentStatus == OrderStatus.DELIVERED) {
                throw new IllegalArgumentException("Order cannot be cancelled after it has shipped");
            }
            return;
        }
        if (currentStatus == OrderStatus.PENDING && nextStatus == OrderStatus.CONFIRMED) {
            return;
        }
        if (currentStatus == OrderStatus.CONFIRMED && nextStatus == OrderStatus.SHIPPED) {
            return;
        }
        if (currentStatus == OrderStatus.SHIPPED && nextStatus == OrderStatus.DELIVERED) {
            return;
        }
        throw new IllegalArgumentException("Invalid order status transition from " + currentStatus + " to " + nextStatus);
    }

    private Specification<Order> statusEquals(OrderStatus status) {
        return (root, query, builder) -> builder.equal(root.get("status"), status);
    }

    private Specification<Order> paymentStatusEquals(PaymentStatus paymentStatus) {
        return (root, query, builder) -> builder.equal(root.get("paymentStatus"), paymentStatus);
    }

    private Specification<Order> orderNumberContains(String keyword) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("orderNumber")), "%" + keyword.toLowerCase() + "%");
    }

    private AdminOrderResponse toResponse(Order order) {
        return AdminOrderResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
