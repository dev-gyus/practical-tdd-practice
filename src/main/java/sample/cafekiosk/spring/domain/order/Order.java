package sample.cafekiosk.spring.domain.order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.cafekiosk.spring.domain.orderproduct.OrderProduct;
import sample.cafekiosk.spring.domain.product.BaseEntity;
import sample.cafekiosk.spring.domain.product.Product;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private int totalPrice;
    private LocalDateTime registeredDateTime;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProductList = new ArrayList<>();

    @Builder
    private Order(OrderStatus orderStatus, LocalDateTime registeredDateTime, List<Product> productList) {
        this.orderStatus = orderStatus;
        this.registeredDateTime = registeredDateTime;
        this.orderProductList = productList.stream()
                .map(product -> new OrderProduct(this, product))
                .collect(Collectors.toList());
        this.totalPrice = this.calculateTotalPrice(productList);
    }

    public static Order create(List<Product> productList, LocalDateTime registeredDateTime) {
        return Order.builder()
                .orderStatus(OrderStatus.INIT)
                .productList(productList)
                .registeredDateTime(registeredDateTime)
                .build();
    }

    private int calculateTotalPrice(List<Product> orderProductList) {
        return orderProductList.stream()
                .mapToInt(Product::getPrice)
                .sum();
    }
}
