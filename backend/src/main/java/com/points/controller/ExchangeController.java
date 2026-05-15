package com.points.controller;

import com.points.entity.ExchangeRecord;
import com.points.entity.Product;
import com.points.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
@CrossOrigin(origins = "*")
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> exchange(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String productId = request.get("productId");

        Map<String, Object> result = new HashMap<>();
        try {
            ExchangeRecord record = exchangeService.exchange(userId, productId);
            result.put("success", true);
            result.put("data", record);
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        List<Product> products = exchangeService.getAllProducts();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", products);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/records/{userId}")
    public ResponseEntity<Map<String, Object>> getUserExchangeRecords(@PathVariable String userId) {
        List<ExchangeRecord> records = exchangeService.getUserExchangeRecords(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", records);
        return ResponseEntity.ok(result);
    }
}
