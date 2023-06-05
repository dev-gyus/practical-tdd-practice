package sample.cafekiosk.unit.beverage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AmericanoTest {

    @Test
    @DisplayName("이름이 잘 출력되는가?")
    void name() {
        // given
        // when
        Americano americano = new Americano();

        // then
        assertEquals(americano.getName(), "아메리카노");
        assertThat(americano.getName()).isEqualTo("아메리카노");
    }

    @Test
    @DisplayName("가격이 잘 출력되는가?")
    void getPrice() {
        // given
        // when
        Americano americano = new Americano();

        // then
        assertThat(americano.getPrice()).isEqualTo(4000);
    }
}