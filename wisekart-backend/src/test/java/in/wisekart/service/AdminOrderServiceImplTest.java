package in.wisekart.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import in.wisekart.dto.UpdateOrderStatusRequest;
import in.wisekart.entity.Order;
import in.wisekart.entity.OrderStatus;
import in.wisekart.entity.PaymentStatus;
import in.wisekart.entity.User;
import in.wisekart.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

class AdminOrderServiceImplTest {

    private OrderRepository orderRepository;
    private AdminOrderServiceImpl service;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        service = new AdminOrderServiceImpl(orderRepository);
    }

    @Test
    void updateOrderStatusRejectsInvalidTransition() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setTotalAmount(new BigDecimal("100.00"));
        User user = new User();
        user.setId(1L);
        user.setEmail("customer@example.com");
        order.setUser(user);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(OrderStatus.DELIVERED);

        assertThrows(IllegalArgumentException.class, () -> service.updateOrderStatus(1L, request));
    }

    @Test
    void getAllOrdersReturnsPagedResult() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD-001");
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setTotalAmount(new BigDecimal("100.00"));
        User user = new User();
        user.setId(1L);
        user.setEmail("customer@example.com");
        order.setUser(user);

        Page<Order> page = new PageImpl<>(java.util.List.of(order));
        when(orderRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Order>>any(), org.mockito.ArgumentMatchers.any(PageRequest.class))).thenReturn(page);

        Page<?> result = service.getAllOrders(0, 10, "createdAt", "DESC", null, null, null);

        assertThrows(IllegalArgumentException.class, () -> service.getAllOrders(0, 10, "invalid", "DESC", null, null, null));
        org.junit.jupiter.api.Assertions.assertEquals(1, result.getTotalElements());
        verify(orderRepository).findAll(org.mockito.ArgumentMatchers.<Specification<Order>>any(), org.mockito.ArgumentMatchers.any(PageRequest.class));
    }
}
