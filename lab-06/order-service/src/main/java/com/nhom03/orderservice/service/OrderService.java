package com.nhom03.orderservice.service;

import com.nhom03.orderservice.dto.*;
import com.nhom03.orderservice.exception.ResourceNotFoundException;
import com.nhom03.orderservice.exception.ServiceUnavailableException;
import com.nhom03.orderservice.model.Order;
import com.nhom03.orderservice.model.OrderItem;
import com.nhom03.orderservice.model.OrderStatus;
import com.nhom03.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    
    @Value("${services.user.url}")
    private String userServiceUrl;
    
    @Value("${services.food.url}")
    private String foodServiceUrl;
    
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());
        
        validateUser(request.getUserId());
        
        List<OrderItem> items = new ArrayList<>();
        double totalAmount = 0;
        
        for (OrderItemRequest itemRequest : request.getItems()) {
            FoodResponse food = getFoodDetails(itemRequest.getFoodId());
            
            double subtotal = food.getPrice() * itemRequest.getQuantity();
            totalAmount += subtotal;
            
            OrderItem item = OrderItem.builder()
                .foodId(food.getId())
                .foodName(food.getName())
                .price(food.getPrice())
                .quantity(itemRequest.getQuantity())
                .subtotal(subtotal)
                .build();
            
            items.add(item);
        }
        
        Order order = Order.builder()
            .userId(request.getUserId())
            .items(items)
            .totalAmount(totalAmount)
            .note(request.getNote())
            .status(OrderStatus.PENDING)
            .build();
        
        order = orderRepository.save(order);
        log.info("Order created successfully with id: {}", order.getId());
        
        return toResponse(order);
    }
    
    public List<OrderResponse> getAllOrders(Long userId) {
        List<Order> orders;
        if (userId != null) {
            orders = orderRepository.findByUserId(userId);
        } else {
            orders = orderRepository.findAll();
        }
        
        return orders.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return toResponse(order);
    }
    
    public OrderResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(orderStatus);
            order = orderRepository.save(order);
            log.info("Order {} status updated to {}", id, status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        
        return toResponse(order);
    }
    
    private void validateUser(Long userId) {
        try {
            String url = userServiceUrl + "/users/" + userId;
            log.info("Validating user at: {}", url);
            UserResponse user = restTemplate.getForObject(url, UserResponse.class);
            if (user == null) {
                throw new ResourceNotFoundException("User not found: " + userId);
            }
            log.info("User validated: {}", user.getUsername());
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("User not found: " + userId);
        } catch (RestClientException e) {
            log.error("User service unavailable", e);
            throw new ServiceUnavailableException("User service unavailable");
        }
    }
    
    private FoodResponse getFoodDetails(Long foodId) {
        try {
            String url = foodServiceUrl + "/foods/" + foodId;
            log.info("Getting food details at: {}", url);
            FoodResponse food = restTemplate.getForObject(url, FoodResponse.class);
            if (food == null) {
                throw new ResourceNotFoundException("Food not found: " + foodId);
            }
            log.info("Food found: {}", food.getName());
            return food;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Food not found: " + foodId);
        } catch (RestClientException e) {
            log.error("Food service unavailable", e);
            throw new ServiceUnavailableException("Food service unavailable");
        }
    }
    
    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
            .map(item -> OrderItemResponse.builder()
                .id(item.getId())
                .foodId(item.getFoodId())
                .foodName(item.getFoodName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .build())
            .collect(Collectors.toList());
        
        return OrderResponse.builder()
            .id(order.getId())
            .userId(order.getUserId())
            .items(itemResponses)
            .totalAmount(order.getTotalAmount())
            .note(order.getNote())
            .status(order.getStatus().name())
            .createdAt(order.getCreatedAt())
            .build();
    }
}
