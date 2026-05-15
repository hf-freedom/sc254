package com.points.controller;

import com.points.entity.Order;
import com.points.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        double amount = Double.parseDouble(request.get("amount").toString());
        Map<String, Object> result = new HashMap<>();
        try {
            Order order = orderService.createOrder(userId, amount);
            result.put("success", true);
            result.put("data", order);
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refund/{orderId}")
    public ResponseEntity<Map<String, Object>> refundOrder(@PathVariable String orderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            orderService.refundOrder(orderId);
            result.put("success", true);
            result.put("message", "退款成功");
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable String orderId) {
        Order order = orderService.getOrder(orderId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", order);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserOrders(@PathVariable String userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", orders);
        return ResponseEntity.ok(result);
    }
}
