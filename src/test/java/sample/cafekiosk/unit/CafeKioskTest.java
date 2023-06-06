package sample.cafekiosk.unit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.cafekiosk.unit.beverage.Americano;
import sample.cafekiosk.unit.beverage.Latte;
import sample.cafekiosk.unit.order.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CafeKioskTest {
    
    @Test
    @DisplayName("키오스크에 음료 추가가 정상적으로 되는가?")
    public void add() throws Exception {
        //given;;
        CafeKiosk cafeKiosk = new CafeKiosk();

        //when
        cafeKiosk.add(new Americano());

        //then
        assertThat(cafeKiosk.getBeverages()).hasSize(1);
        assertThat(cafeKiosk.getBeverages().get(0).getName()).isEqualTo("아메리카노");
        assertThat(cafeKiosk.getBeverages().get(0).getPrice()).isEqualTo(4000);
    }

    @Test
    @DisplayName("키오스크에 음료 1개 이상 추가가 정상적으로 동작하는가?")
    public void addSeveralBeverages() throws Exception {
        //given
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        //when
        cafeKiosk.add(americano, 2);

        //then
        assertThat(cafeKiosk.getBeverages()).hasSize(2);
        assertThat(cafeKiosk.getBeverages().get(0)).isEqualTo(americano);
        assertThat(cafeKiosk.getBeverages().get(1)).isEqualTo(americano);
    }

    @Test
    @DisplayName("키오스크에 음료를 1개 미만으로 추가할 경우 예외가 발생 하는가?")
    public void addZeroBeverages() throws Exception {
        //given
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        //when
        Assertions.assertThatThrownBy(() -> cafeKiosk.add(americano, 0)).isInstanceOf(IllegalArgumentException.class);
        //then
    }

    @Test
    @DisplayName("키오스크에 추가된 음료들의 가격 합 구하기 테스트")
    public void sumTotalPrice() {
        // given
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Americano());
        cafeKiosk.add(new Latte());
        // when
        int totalPrice = cafeKiosk.calculateTotalPrice();
        // then
        assertThat(totalPrice).isEqualTo(8500);
    }

    @Test
    @DisplayName("키오스크에 음료 삭제가 정상적으로 되는가?")
    public void remove() throws Exception {
        //given
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        cafeKiosk.add(americano);

        //when
        cafeKiosk.delete(americano);

        //then
        assertThat(cafeKiosk.getBeverages()).isEmpty();
    }

    @Test
    @DisplayName("키오스크에 음료 모두 삭제가 정상적으로 작동 하는가?")
    public void clear() throws Exception {
        //given
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        Latte latte = new Latte();
        cafeKiosk.add(americano);
        cafeKiosk.add(latte);

        //when
        cafeKiosk.clear();

        //then
        assertThat(cafeKiosk.getBeverages()).isEmpty();
    }

    @Test
    @DisplayName("가게 영업시간: 10:00 ~ 22:00 동안엔 주문이 정상적으로 생성 되는가?")
    public void createOrderInsideOpenTime() throws Exception {
        //given
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        cafeKiosk.add(americano);
        LocalDateTime openBorderDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0, 0));

        //when
        Order order = cafeKiosk.createOrder(openBorderDateTime);

        //then
        assertThat(order.getOrderDateTime()).isEqualTo(openBorderDateTime);
        assertThat(order.getBeverages()).isEqualTo(cafeKiosk.getBeverages());
    }

    @Test
    @DisplayName("가게 영업시간: 10:00 ~ 22:00 외엔 주문이 생성 되지 않는가?")
    public void createOrderOutsideOpenTime() throws Exception {
        //given
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        cafeKiosk.add(americano);
        LocalDateTime openBorderDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0, 0));

        //when
        //then
        assertThatThrownBy(() -> cafeKiosk.createOrder(openBorderDateTime)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 시간이 아닙니다. 관리자에게 문의 해 주세요.");
    }

}