package com.points.controller;

import com.points.entity.User;
import com.points.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        User user = userService.createUser(username);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", user);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String userId) {
        User user = userService.getUser(userId);
        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            result.put("success", true);
            result.put("data", user);
        } else {
            result.put("success", false);
            result.put("message", "用户不存在");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", users);
        return ResponseEntity.ok(result);
    }
}
