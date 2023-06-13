package sample.cafekiosk.spring.domain.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTypeTest {

    @DisplayName("상품 타입이 재고 관련 타입인지 체크한다.")
    @Test
    public void successContainsStockType() throws Exception{
        //given
        ProductType handmade = ProductType.HANDMADE;

        //when
        boolean isStockType = ProductType.containsStockType(handmade);

        //then
        Assertions.assertThat(isStockType).isFalse();
    }

    @DisplayName("상품 타입이 재고 관련 타입인지 체크한다.")
    @Test
    public void failContainsStockType() throws Exception{
        //given
        ProductType bottle = ProductType.BOTTLE;

        //when
        boolean isStockType = ProductType.containsStockType(bottle);

        //then
        Assertions.assertThat(isStockType).isTrue();
    }
}