package sample.cafekiosk.spring.api.controller.product.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import javax.validation.constraints.*;

@Getter
public class ProductCreateRequest {
    @NotNull(message = "상품 타입은 필수 값 입니다.")
    private ProductType type;
    @NotNull(message = "상품 판매 상태는 필수 값 입니다.")
    private ProductSellingStatus sellingStatus;
    @NotBlank(message = "상품 명은 필수 값 입니다.")
    private String name;
    @Positive(message = "상품 가격은 양의 값이어야 합니다.")
    private int price;

    @Builder
    public ProductCreateRequest(ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
        this.type = type;
        this.sellingStatus = sellingStatus;
        this.name = name;
        this.price = price;
    }

    public ProductCreateServiceRequest toServiceRequest() {
        return ProductCreateServiceRequest.builder()
                .type(this.type)
                .sellingStatus(this.sellingStatus)
                .name(this.name)
                .price(this.price)
                .build();
    }
}
