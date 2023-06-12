package sample.cafekiosk.spring.api.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.api.controller.order.request.order.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredOrderDateTime) {
        // product
        List<String> productNumbers = request.getProductNumbers();
        List<Product> productList = productRepository.findAllByProductNumberIn(productNumbers);
        // order
        Order order = Order.create(productList, registeredOrderDateTime);
        Order savedOrder = orderRepository.save(order);
        // response
        return OrderResponse.of(savedOrder);
    }
}
