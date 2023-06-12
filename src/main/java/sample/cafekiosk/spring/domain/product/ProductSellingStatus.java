package sample.cafekiosk.spring.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum ProductSellingStatus {
    SELLING("판매중"),
    HOLD("판매 보류"),
    STOP_SELLING("판매 중지");

    private final String text;

    public static Set<ProductSellingStatus> forDisplay() {
        return Set.of(SELLING, HOLD);
    }
}
