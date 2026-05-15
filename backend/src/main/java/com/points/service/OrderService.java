package com.points.service;

import com.points.entity.Order;
import com.points.entity.PointsRecord;
import com.points.entity.User;
import com.points.enums.PointsSource;
import com.points.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private UserService userService;

    @Autowired
    private PointsService pointsService;

    public Order createOrder(String userId, double amount) {
        User user = userService.getUser(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        double discount = user.getCurrentLevel().getDiscount();
        double actualAmount = amount * discount;
        int pointsEarned = (int) actualAmount;

        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setUserId(userId);
        order.setAmount(actualAmount);
        order.setPointsEarned(pointsEarned);
        order.setRefunded(false);
        order.setCreatedAt(LocalDateTime.now());
        DataStorage.ORDERS.put(order.getOrderId(), order);

        if (pointsEarned > 0) {
            pointsService.addPoints(userId, pointsEarned, PointsSource.CONSUMPTION, "消费获得积分", order.getOrderId());
        }

        return order;
    }

    public void refundOrder(String orderId) {
        Order order = DataStorage.ORDERS.get(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.isRefunded()) {
            throw new RuntimeException("订单已退款");
        }

        order.setRefunded(true);

        if (order.getPointsEarned() > 0) {
            pointsService.deductPoints(order.getUserId(), order.getPointsEarned(), PointsSource.CONSUMPTION, orderId);
        }
    }

    public Order getOrder(String orderId) {
        return DataStorage.ORDERS.get(orderId);
    }

    public java.util.List<Order> getUserOrders(String userId) {
        return DataStorage.ORDERS.values().stream()
                .filter(o -> o.getUserId().equals(userId))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(java.util.stream.Collectors.toList());
    }
}
