package com.points.controller;

import com.points.service.RiskControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/risk")
@CrossOrigin(origins = "*")
public class RiskControlController {

    @Autowired
    private RiskControlService riskControlService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getRiskControlList() {
        List<String> list = riskControlService.getRiskControlList();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", list);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/remove/{userId}")
    public ResponseEntity<Map<String, Object>> removeFromRiskControl(@PathVariable String userId) {
        riskControlService.removeFromRiskControl(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "已移除风控名单");
        return ResponseEntity.ok(result);
    }
}
