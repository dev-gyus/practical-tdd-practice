package sample.cafekiosk.spring.api.controller.product.dto.response;

import lombok.Builder;
import lombok.Getter;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

@Getter
public class ProductCreateResponse {
    private String productNumber;
    private ProductType type;
    private ProductSellingStatus sellingStatus;
    private String name;
    private int price;

    @Builder
    public ProductCreateResponse(String productNumber, ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
        this.productNumber = productNumber;
        this.type = type;
        this.sellingStatus = sellingStatus;
        this.name = name;
        this.price = price;
    }

    public static ProductCreateResponse of(Product savedProduct) {
        return ProductCreateResponse.builder()
                .productNumber(savedProduct.getProductNumber())
                .type(savedProduct.getType())
                .sellingStatus(savedProduct.getSellingStatus())
                .name(savedProduct.getName())
                .price(savedProduct.getPrice())
                .build();
    }
}
