package com.nhom03.orderservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long foodId;
    
    private String foodName;
    
    private Integer quantity;
    
    private Double price;
    
    private Double subtotal;
}
