package sample.cafekiosk.spring.domain.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.groups.Tuple.tuple;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

@ActiveProfiles("test")
//@SpringBootTest
@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
    @Test
    public void findAllBySellingStatusIn() throws Exception{
        //given
        Product americano = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product latte = createProduct("002", HANDMADE, HOLD, "라떼", 4500);
        Product iceFlake = createProduct("003", HANDMADE, STOP_SELLING, "팥빙수", 7000);
        productRepository.saveAll(List.of(americano, latte, iceFlake));
        //when
        List<Product> productList = productRepository.findAllBySellingStatusIn(forDisplay());

        //then
        Assertions.assertThat(productList).hasSize(2)
                .extracting("productNumber", "name", "sellingStatus")
                .containsExactlyInAnyOrder(
                        tuple("001", "아메리카노", SELLING),
                        tuple("002", "라떼", HOLD)
                );

    }

    @DisplayName("상품 번호 리스트로 상품들을 조회한다.")
    @Test
    public void findAllByProductNumberIn() throws Exception {
        //given
        Product americano = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product latte = createProduct("002", HANDMADE, HOLD, "라떼", 4500);
        Product iceFlake = createProduct("003", HANDMADE, STOP_SELLING, "팥빙수", 7000);
        productRepository.saveAll(List.of(americano, latte, iceFlake));
        //when
        List<Product> productList = productRepository.findAllByProductNumberIn(List.of("001", "002"));

        //then
        Assertions.assertThat(productList).hasSize(2)
                .extracting("productNumber", "name", "sellingStatus")
                .containsExactlyInAnyOrder(
                        tuple("001", "아메리카노", SELLING),
                        tuple("002", "라떼", HOLD)
                );
    }

    @DisplayName("가장 마지막에 저장된 상품의 상품 번호를 조회할 수 있다.")
    @Test
    public void findLatestProduct() throws Exception {
        //given
        Product americano = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product latte = createProduct("002", HANDMADE, HOLD, "라떼", 4500);
        String tarProductNumber = "003";
        Product iceFlake = createProduct(tarProductNumber, HANDMADE, STOP_SELLING, "팥빙수", 7000);
        productRepository.saveAll(List.of(americano, latte, iceFlake));
        //when
        String latestProductNumber = productRepository.findLatestProductNumber();

        //then
        Assertions.assertThat(latestProductNumber).isEqualTo(tarProductNumber);
    }

    @DisplayName("가장 마지막에 저장된 상품의 상품 번호를 조회할때, 상품이 하나도 없는 경우라면 null을 반환한다.")
    @Test
    public void findLatestProductWhenNoSavedProduct() throws Exception {
        //when
        String latestProductNumber = productRepository.findLatestProductNumber();

        //then
        Assertions.assertThat(latestProductNumber).isNull();
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