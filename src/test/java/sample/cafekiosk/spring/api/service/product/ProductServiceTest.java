package sample.cafekiosk.spring.api.service.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.controller.product.dto.response.ProductCreateResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.List;

import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

@ActiveProfiles("test")
@SpringBootTest
class ProductServiceTest {
    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }

    @DisplayName("신규 상품을 등록한다. 상품 번호는 가장 최근 상품의 상품 번호에서 1 증가한 값이다.")
    @Test
    void createProduct() throws Exception{
        //given
        Product americano = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        productRepository.save(americano);
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("카푸치노")
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .price(5000)
                .build();
        //when
        ProductCreateResponse response = productService.createProduct(request);
        //them
        Assertions.assertThat(response)
                .extracting(
                        "productNumber", "type", "sellingStatus", "name", "price"
                ).contains("002", HANDMADE, SELLING, "카푸치노", 5000);
        Assertions.assertThat(productRepository.findAll()).hasSize(2)
                .extracting(
                        "productNumber", "type", "sellingStatus", "name", "price"
                )
                .contains(
                        tuple("001", HANDMADE, SELLING, "아메리카노", 4000),
                        tuple("002", HANDMADE, SELLING, "카푸치노", 5000)
                );
    }

    private Product createProduct(String productNumber,
                                  ProductType productType,
                                  ProductSellingStatus sellingStatus,
                                  String name,
                                  int price) {
        return Product.builder()
                .productNumber(productNumber)
                .type(productType)
                .sellingStatus(sellingStatus)
                .name(name)
                .price(price)
                .build();
    }
}