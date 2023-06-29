package sample.cafekiosk.spring.api.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProductService productService;

    @DisplayName("신규 상품을 등록한다.")
    @Test
    public void createProduct() throws Exception{
        //given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();
        //when
        ResultActions action = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/products/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        //then
        action.andExpect(status().isOk());
        action.andDo(print());
    }

    @DisplayName("신규 상품을 등록할때 상품 타입은 필수 값이다")
    @Test
    public void createProductWithoutProductType() throws Exception {
        //given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(null)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(4000)
                .build();
        //when
        ResultActions action = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/products/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        //then
        action.andDo(print());
        action.andExpect(status().isBadRequest());
        action.andExpect(jsonPath("$.code").value(400));
        action.andExpect(jsonPath("$.message").value("BAD_REQUEST"));
        action.andExpect(jsonPath("$.data").value("상품 타입은 필수 값 입니다."));
    }

    @DisplayName("신규 상품을 등록할때 상품 판매 상태는 필수 값이다")
    @Test
    public void createProductWithoutProductSellingType() throws Exception {
        //given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(null)
                .name("아메리카노")
                .price(4000)
                .build();
        //when
        ResultActions action = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/products/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        //then
        action.andDo(print());
        action.andExpect(status().isBadRequest());
        action.andExpect(jsonPath("$.code").value(400));
        action.andExpect(jsonPath("$.message").value("BAD_REQUEST"));
        action.andExpect(jsonPath("$.data").value("상품 판매 상태는 필수 값 입니다."));
    }

    @DisplayName("신규 상품을 등록할때 상품 명은 필수 값 이다.")
    @Test
    public void createProductWithoutName() throws Exception {
        //given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name(null)
                .price(4000)
                .build();
        //when
        ResultActions action = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/products/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        //then
        action.andDo(print());
        action.andExpect(status().isBadRequest());
        action.andExpect(jsonPath("$.code").value(400));
        action.andExpect(jsonPath("$.message").value("BAD_REQUEST"));
        action.andExpect(jsonPath("$.data").value("상품 명은 필수 값 입니다."));
    }

    @DisplayName("신규 상품을 등록할때 상품 명은 빈 칸일 수 없다")
    @Test
    public void createProductWithBlankName() throws Exception {
        //given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("")
                .price(4000)
                .build();
        //when
        ResultActions action = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/products/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        //then
        action.andDo(print());
        action.andExpect(status().isBadRequest());
        action.andExpect(jsonPath("$.code").value(400));
        action.andExpect(jsonPath("$.message").value("BAD_REQUEST"));
        action.andExpect(jsonPath("$.data").value("상품 명은 필수 값 입니다."));
    }

    @DisplayName("신규 상품을 등록할때 상품 가격은 양수여야 한다.")
    @Test
    public void createProductWithZeroPrice() throws Exception {
        //given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .type(ProductType.HANDMADE)
                .sellingStatus(ProductSellingStatus.SELLING)
                .name("아메리카노")
                .price(0)
                .build();
        //when
        ResultActions action = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/products/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        //then
        action.andDo(print());
        action.andExpect(status().isBadRequest());
        action.andExpect(jsonPath("$.code").value(400));
        action.andExpect(jsonPath("$.message").value("BAD_REQUEST"));
        action.andExpect(jsonPath("$.data").value("상품 가격은 양의 값이어야 합니다."));
    }

    @DisplayName("판매 상품을 조회 한다.")
    @Test
    public void getSellingProducts() throws Exception {
        //given
        List<ProductResponse> responseList = List.of();
        Mockito.when(productService.getSellingProducts()).thenReturn(responseList);
        //when
        ResultActions action = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/selling"));
        //then
        action.andDo(print());
        action.andExpect(status().isOk());
        action.andExpect(jsonPath("$.code").value(200));
        action.andExpect(jsonPath("$.message").value("OK"));
        action.andExpect(jsonPath("$.data").isArray());
    }

}