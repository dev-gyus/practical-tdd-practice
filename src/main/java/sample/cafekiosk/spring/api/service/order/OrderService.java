package sample.cafekiosk.spring.api.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.api.controller.order.request.order.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredOrderDateTime) {
        // product
        List<String> productNumbers = request.getProductNumbers();
        List<Product> products = this.findProductsBy(productNumbers);
        // 재고 차감 체크가 필요한 상품을 필터
        List<String> stockProductNumbers = products.stream()
                .filter(product -> ProductType.containsStockType(product.getType()))
                .map(Product::getProductNumber)
                .collect(Collectors.toList());
        // 재고 엔티티 조회
        List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
        Map<String, Stock> stockMap = stocks.stream()
                .collect(Collectors.toMap(Stock::getProductNumber, s -> s));
        // 상품별 counting
        Map<String, Long> productCountingMap = stockProductNumbers.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
        // 재고 차감 시도
        for(String stockProductNumber : stockProductNumbers) {
            Stock stock = stockMap.get(stockProductNumber);
            int orderQuantity = productCountingMap.get(stockProductNumber).intValue();
            if (stock.isQuantityLessThan(orderQuantity)) {
                throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");
            }
            stock.deductQuantity(orderQuantity);
        }
        // order
        Order order = Order.create(products, registeredOrderDateTime);
        Order savedOrder = orderRepository.save(order);
        // response
        return OrderResponse.of(savedOrder);
    }

    private List<Product> findProductsBy(List<String> productNumbers) {
        List<Product> productList = productRepository.findAllByProductNumberIn(productNumbers);
        Map<String, Product> productMap =
                productList.stream()
                        .collect(Collectors.toMap(Product::getProductNumber, p -> p));
        List<Product> products = productNumbers.stream()
                .map(productMap::get)
                .collect(Collectors.toList());
        return products;
    }
}
