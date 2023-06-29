package sample.cafekiosk.spring.api.controller.product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sample.cafekiosk.spring.api.controller.ApiResponse;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.controller.product.dto.response.ProductCreateResponse;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.api.service.product.ProductService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/api/v1/products/new")
    public ApiResponse<ProductCreateResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductCreateResponse response = productService.createProduct(request.toServiceRequest());
        return ApiResponse.of(HttpStatus.OK, response);
    }

    @GetMapping("/api/v1/products/selling")
    public ApiResponse<List<ProductResponse>> getSellingProducts() {
        return ApiResponse.of(HttpStatus.OK, productService.getSellingProducts());
    }
}
