package sample.cafekiosk.spring.domain.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;

class OrderTest {

    @DisplayName("주문 생성 시 상품 리스트에서 주문 금액의 총 금액을 계산한다.")
    @Test
    public void calculateTotalPrice() throws Exception{
        //given
        Product product1 = createProducts("001", 1000);
        Product product2 = createProducts("002", 3000);
        Product product3 = createProducts("003", 5000);

        //when
        Order order = Order.create(List.of(product1, product2, product3), LocalDateTime.now());

        //then
        assertThat(order.getTotalPrice()).isEqualTo(1000 + 3000 + 5000);
    }

    @DisplayName("주문 생성 시 초기 주문 상태는 INIT 이다")
    @Test
    public void initOrderStatusTest() throws Exception{
        //given
        Product product1 = createProducts("001", 1000);
        Product product2 = createProducts("002", 3000);
        Product product3 = createProducts("003", 5000);

        //when
        Order order = Order.create(List.of(product1, product2, product3), LocalDateTime.now());

        //then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.INIT);
    }

    @DisplayName("주문 생성 시 주문 등록 시간을 저장 한다")
    @Test
    public void registeredOrderDateTime() throws Exception{
        // given
        Product product1 = createProducts("001", 1000);
        Product product2 = createProducts("002", 3000);
        Product product3 = createProducts("003", 5000);

        // when
        LocalDateTime registeredDateTime = LocalDateTime.now();
        Order order = new Order(List.of(product1, product2, product3), registeredDateTime);

        // then
        assertThat(order.getRegisteredDateTime()).isEqualTo(registeredDateTime);
    }

    private Product createProducts(String productNumber, int price) {
        return Product.builder()
                .productNumber(productNumber)
                .type(ProductType.HANDMADE)
                .price(price)
                .sellingStatus(SELLING)
                .name("메뉴 이름")
                .build();
    }

}