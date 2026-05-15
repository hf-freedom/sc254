package com.points.service;

import com.points.entity.User;
import com.points.storage.DataStorage;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    public User createUser(String username) {
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setAvailablePoints(0);
        user.setTotalPoints(0);
        user.setUsedPoints(0);
        user.setOwedPoints(0);
        user.setInRiskControl(false);
        DataStorage.USERS.put(user.getUserId(), user);
        return user;
    }

    public User getUser(String userId) {
        return DataStorage.USERS.get(userId);
    }

    public java.util.List<User> getAllUsers() {
        return new java.util.ArrayList<>(DataStorage.USERS.values());
    }
}
