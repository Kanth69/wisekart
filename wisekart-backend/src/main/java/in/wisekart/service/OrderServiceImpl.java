package in.wisekart.service;

import in.wisekart.dto.CheckoutRequest;
import in.wisekart.dto.OrderItemResponse;
import in.wisekart.dto.OrderResponse;
import in.wisekart.entity.Cart;
import in.wisekart.entity.Order;
import in.wisekart.entity.OrderItem;
import in.wisekart.entity.OrderStatus;
import in.wisekart.entity.PaymentStatus;
import in.wisekart.entity.Product;
import in.wisekart.entity.User;
import in.wisekart.exception.ResourceNotFoundException;
import in.wisekart.repository.CartRepository;
import in.wisekart.repository.OrderRepository;
import in.wisekart.repository.ProductRepository;
import in.wisekart.repository.UserRepository;
import in.wisekart.security.UserPrincipal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponse checkout(CheckoutRequest request) {
        User currentUser = getCurrentUser();
        List<Cart> carts = cartRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        if (carts.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(currentUser);
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : carts) {
            Product product = cart.getProduct();
            if (product.getIsActive() == null || !product.getIsActive()) {
                throw new IllegalArgumentException("Product is not available: " + product.getName());
            }
            if (product.getStock() == null || cart.getQuantity() > product.getStock()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            BigDecimal unitPrice = product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(cart.getQuantity());
            item.setPrice(unitPrice);
            item.setSubtotal(subtotal);
            order.getItems().add(item);

            product.setStock(product.getStock() - cart.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        cartRepository.deleteAll(carts);

        return toResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getOrdersForCurrentUser() {
        User currentUser = getCurrentUser();
        return orderRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderByIdForCurrentUser(Long orderId) {
        User currentUser = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }
        return toResponse(order);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }

        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userPrincipal.getId()));
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream().map(this::toItemResponse).toList())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}
