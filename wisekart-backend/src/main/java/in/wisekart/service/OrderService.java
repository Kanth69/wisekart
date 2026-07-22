package in.wisekart.service;

import in.wisekart.dto.CheckoutRequest;
import in.wisekart.dto.OrderResponse;
import java.util.List;

public interface OrderService {

    OrderResponse checkout(CheckoutRequest request);

    List<OrderResponse> getOrdersForCurrentUser();

    OrderResponse getOrderByIdForCurrentUser(Long orderId);
}
