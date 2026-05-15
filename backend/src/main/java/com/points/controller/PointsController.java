package com.points.controller;

import com.points.entity.PointsRecord;
import com.points.enums.PointsSource;
import com.points.service.PointsService;
import com.points.service.SignInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
@CrossOrigin(origins = "*")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    @Autowired
    private SignInService signInService;

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        Map<String, Object> result = new HashMap<>();
        try {
            PointsRecord record = signInService.signIn(userId);
            result.put("success", true);
            result.put("data", record);
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/signin/check/{userId}")
    public ResponseEntity<Map<String, Object>> checkSignIn(@PathVariable String userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", signInService.hasSignedToday(userId));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/records/{userId}")
    public ResponseEntity<Map<String, Object>> getPointsRecords(@PathVariable String userId) {
        List<PointsRecord> records = pointsService.getUserPointsRecords(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", records);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/summary/{userId}")
    public ResponseEntity<Map<String, Object>> getPointsSummary(@PathVariable String userId) {
        Map<PointsSource, Integer> summary = pointsService.getPointsSummary(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", summary);
        return ResponseEntity.ok(result);
    }
}
