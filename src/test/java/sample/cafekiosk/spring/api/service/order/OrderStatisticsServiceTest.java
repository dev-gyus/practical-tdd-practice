package sample.cafekiosk.spring.api.service.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.controller.client.MailSendClient;
import sample.cafekiosk.spring.domain.mail.MailSendHistory;
import sample.cafekiosk.spring.domain.mail.MailSendHistoryRepository;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.order.OrderStatus;
import sample.cafekiosk.spring.domain.orderproduct.OrderProductRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.BOTTLE;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

@SpringBootTest
class OrderStatisticsServiceTest {
    @Autowired
    OrderStatisticsService orderStatisticsService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderProductRepository orderProductRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    MailSendHistoryRepository mailSendHistoryRepository;
    @MockBean
    MailSendClient mailSendClient;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        mailSendHistoryRepository.deleteAllInBatch();
    }

    @DisplayName("결제 완료 주문들을 조회하여 매출 통계 메일을 전송한다.")
    @Test
    void sendOrderStatisticsMail() throws Exception{
        //given
        LocalDateTime now = LocalDateTime.of(2023, 3, 5, 0, 0);
        Product product1 = createProducts(HANDMADE, "001", 1000);
        Product product2 = createProducts(HANDMADE, "002", 2000);
        Product product3 = createProducts(HANDMADE, "003", 3000);
        List<Product> savedProductList = productRepository.saveAll(List.of(product1, product2, product3));

        Order order1 = this.createPaymentCompletedOrder(product1, product2, product3, LocalDateTime.of(2023,3,4,23,59,59));
        Order order2 = this.createPaymentCompletedOrder(product1, product2, product3, now);
        Order order3 = this.createPaymentCompletedOrder(product1, product2, product3, LocalDateTime.of(2023,3,5,23,59,59));
        Order order4 = this.createPaymentCompletedOrder(product1, product2, product3, LocalDateTime.of(2023,3,6,0,0,0));

        when(mailSendClient.sendMail(any(), any(), any(), any())).thenReturn(true);
        //when
        boolean result =
                orderStatisticsService.sendOrderStatisticsMail(LocalDate.of(2023, 3, 5), "test@test.com");

        //then
        assertThat(result).isTrue();
        List<MailSendHistory> histories = mailSendHistoryRepository.findAll();
        assertThat(histories).hasSize(1)
                .extracting("content")
                .contains("총 매출 합계는 12000원 입니다");
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

    private Order createPaymentCompletedOrder(Product product1, Product product2, Product product3, LocalDateTime now) {
        Order order = Order.builder()
                .productList(List.of(product1, product2, product3))
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .registeredDateTime(now)
                .build();
        return orderRepository.save(order);
    }

}