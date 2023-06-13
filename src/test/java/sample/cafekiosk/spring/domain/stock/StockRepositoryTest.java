package sample.cafekiosk.spring.domain.stock;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StockRepositoryTest {
    @Autowired
    private StockRepository stockRepository;

    @AfterEach
    void tearDown() {
        stockRepository.deleteAll();
    }

    @DisplayName("상품 넘버 목록으로 재고 데이터 목록을 조회 할 수 있다")
    @Test
    void findAllByProductNumberIn() {
        // given
        Stock stock1 = Stock.create("001", 1);
        Stock stock2 = Stock.create("002", 2);
        Stock stock3 = Stock.create("003", 3);
        stockRepository.saveAll(List.of(stock1, stock2, stock3));

        // when
        List<Stock> stocks = stockRepository.findAllByProductNumberIn(List.of("001", "002"));

        // then
        assertThat(stocks).hasSize(2)
                .extracting("productNumber", "quantity")
                .containsExactlyInAnyOrder(
                        tuple("001", 1),
                        tuple("002", 2)
                );
    }

    @DisplayName("재고의 수량이 주문한 수량보다 작은지 확인한다.")
    @Test
    void isQuantityLessThan() {
        // given
        Stock stock = Stock.create("001", 1);
        int qauntity = 2;
        // when
        boolean isQuantityLessThan = stock.isQuantityLessThan(qauntity);
        // then
        assertThat(isQuantityLessThan).isTrue();
    }

    @DisplayName("재고를 주문한 갯수 만큼 차감 할 수 있다.")
    @Test
    void deductQuantity() {
        // given
        Stock stock = Stock.create("001", 2);
        int qauntity = 2;
        // when
        stock.deductQuantity(qauntity);
        // then
        assertThat(stock.getQuantity()).isZero();
    }

    @DisplayName("재고보다 많은 수량으로 차감 하는 경우 예외가 발생 한다.")
    @Test
    void exceptionThrownByOverQuantityOrder() {
        // given
        Stock stock = Stock.create("001", 2);
        int qauntity = 3;
        // when then
        Assertions.assertThatThrownBy(() -> stock.deductQuantity(qauntity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 수량이 부족합니다");
    }
}