package com.nhom03.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long foodId;
    private String foodName;
    private Integer quantity;
    private Double price;
    private Double subtotal;
}
