package com.points.controller;

import com.points.entity.PointsRecord;
import com.points.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> review(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        String orderId = (String) request.get("orderId");
        int rating = Integer.parseInt(request.get("rating").toString());

        Map<String, Object> result = new HashMap<>();
        try {
            PointsRecord record = reviewService.review(userId, orderId, rating);
            result.put("success", true);
            result.put("data", record);
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}
