package sample.cafekiosk.spring.api.service.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.controller.order.request.order.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.orderproduct.OrderProductRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.*;

@ActiveProfiles("test")
@SpringBootTest
//@DataJpaTest
class OrderServiceTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StockRepository stockRepository;

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        orderProductRepository.deleteAll();
        productRepository.deleteAll();
        stockRepository.deleteAll();
    }

    /**
     * 재고 감소 -> 동시성 고민
     */
    @DisplayName("주문 번호 리스트를 받아 주문을 생성한다.")
    @Test
    public void createOrder() throws Exception{
        //given
        Product product1 = createProducts(HANDMADE, "001", 1000);
        Product product2 = createProducts(HANDMADE, "002", 3000);
        Product product3 = createProducts(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));
        OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "002"))
                .build();

        //when
        LocalDateTime registeredDateTime = LocalDateTime.now();
        OrderResponse response = orderService.createOrder(orderCreateRequest.toServiceRequest(), registeredDateTime);

        //then
        assertThat(response.getId()).isNotNull();
        assertThat(response).extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 1000 + 3000);
        assertThat(response.getProducts()).hasSize(2)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 1000),
                        tuple("002", 3000)
                );
    }

    @DisplayName("중복되는 상품 번호로도 주문이 가능 하다")
    @Test
    public void createOrderWithDuplicateProductNumbers() throws Exception {
        //given
        Product product1 = createProducts(HANDMADE, "001", 1000);
        Product product2 = createProducts(HANDMADE, "002", 3000);
        Product product3 = createProducts(HANDMADE, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));
        OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "001"))
                .build();

        //when
        OrderResponse orderResponse = orderService.createOrder(orderCreateRequest.toServiceRequest(), LocalDateTime.now());

        //then
        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.getProducts())
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 1000),
                        tuple("001", 1000)
                );
    }

    @DisplayName("재고와 관련된 상품이 포함되어 있는 주문 번호 리스트를 받아 주문을 생성한다.")
    @Test
    public void createOrderWithStock() throws Exception{
        //given
        Product product1 = createProducts(BOTTLE, "001", 1000);
        Product product2 = createProducts(BOTTLE, "002", 3000);
        Product product3 = createProducts(BAKERY, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));
        OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "001", "002", "003"))
                .build();
        Stock stock1 = Stock.create("001", 2);
        Stock stock2 = Stock.create("002", 2);
        Stock stock3 = Stock.create("003", 2);
        stockRepository.saveAll(List.of(stock1, stock2, stock3));

        //when
        LocalDateTime registeredDateTime = LocalDateTime.now();
        OrderResponse response = orderService.createOrder(orderCreateRequest.toServiceRequest(), registeredDateTime);

        //then
        assertThat(response.getId()).isNotNull();
        assertThat(response).extracting("registeredDateTime", "totalPrice")
                .contains(registeredDateTime, 1000 + 1000 + 3000 + 5000);;
        assertThat(response.getProducts()).hasSize(4)
                .extracting("productNumber", "price")
                .containsExactlyInAnyOrder(
                        tuple("001", 1000),
                        tuple("001", 1000),
                        tuple("002", 3000),
                        tuple("003", 5000)
                );;
        List<Stock> stocks = stockRepository.findAll();;
        assertThat(stocks).hasSize(3)
                .extracting("productNumber", "quantity")
                .containsExactlyInAnyOrder(
                        tuple("001", 0),
                        tuple("002", 1),
                        tuple("003", 1)
                );
    }

    @DisplayName("재고와 부족한 상품으로 주문을 생성하는 경우 예외가 발생한다.")
    @Test
    public void createOrderWithNoStock() throws Exception{
        //given
        Product product1 = createProducts(BOTTLE, "001", 1000);
        Product product2 = createProducts(BOTTLE, "002", 3000);
        Product product3 = createProducts(BAKERY, "003", 5000);
        productRepository.saveAll(List.of(product1, product2, product3));
        OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "001", "002", "003"))
                .build();
        Stock stock1 = Stock.create("001", 1);
        Stock stock2 = Stock.create("002", 1);
        Stock stock3 = Stock.create("003", 2);
        stockRepository.saveAll(List.of(stock1, stock2, stock3));

        //when
        LocalDateTime registeredDateTime = LocalDateTime.now();
        Assertions.assertThatThrownBy(() -> orderService.createOrder(orderCreateRequest.toServiceRequest(), registeredDateTime))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("재고가 부족한 상품이 있습니다.");

        //then
        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(3)
                .extracting("productNumber", "quantity")
                .containsExactlyInAnyOrder(
                        tuple("001", 1),
                        tuple("002", 1),
                        tuple("003", 2)
                );
    }

    private Product createProducts(ProductType type, String productNumber, int price) {
        return Product.builder()
                .productNumber(productNumber)
                .type(type)
                .price(price)
                .sellingStatus(SELLING)
                .name("메뉴 이름")
                .build();
    }
}