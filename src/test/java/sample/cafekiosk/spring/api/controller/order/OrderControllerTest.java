package sample.cafekiosk.spring.api.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sample.cafekiosk.spring.api.controller.order.request.order.OrderCreateRequest;
import sample.cafekiosk.spring.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.order.OrderService;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class OrderControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    OrderController orderController;
    @MockBean
    OrderService orderService;
    @MockBean
    ProductService productService;
    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("신규 주문을 등록한다.")
    @Test
    public void createOrder() throws Exception{
        //given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001"))
                .build();
        //when
        ResultActions action = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/orders/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        //then
        action.andExpect(status().isOk());
        action.andDo(print());
    }

    @DisplayName("신규 주문시 상품 번호는 한 개 이상이어야 한다.")
    @Test
    public void createOrderWithEmptyProductNumbers() throws Exception {
        //given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(Collections.emptyList())
                .build();
        //when
        ResultActions action = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/orders/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
        //then
        action.andDo(print());
        action.andExpect(status().isBadRequest());
        action.andExpect(jsonPath("$.code").value(400));
        action.andExpect(jsonPath("$.message").value("BAD_REQUEST"));
        action.andExpect(jsonPath("$.data").value("상품 번호는 필수 입니다."));
    }

}