package com.points.service;

import com.points.entity.ExchangeRecord;
import com.points.entity.Product;
import com.points.entity.User;
import com.points.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExchangeService {

    @Autowired
    private UserService userService;

    public ExchangeRecord exchange(String userId, String productId) {
        User user = userService.getUser(userId);
        Product product = DataStorage.PRODUCTS.get(productId);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (user.getOwedPoints() > 0) {
            throw new RuntimeException("用户有欠积分，请先补足");
        }
        if (user.getAvailablePoints() < product.getPointsRequired()) {
            throw new RuntimeException("积分不足");
        }
        if (product.getStock() - product.getLockedStock() <= 0) {
            throw new RuntimeException("库存不足");
        }

        product.setLockedStock(product.getLockedStock() + 1);

        user.setAvailablePoints(user.getAvailablePoints() - product.getPointsRequired());
        user.setUsedPoints(user.getUsedPoints() + product.getPointsRequired());

        product.setLockedStock(product.getLockedStock() - 1);
        product.setStock(product.getStock() - 1);

        ExchangeRecord record = new ExchangeRecord();
        record.setRecordId(UUID.randomUUID().toString());
        record.setUserId(userId);
        record.setProductId(productId);
        record.setProductName(product.getName());
        record.setPointsUsed(product.getPointsRequired());
        record.setStatus("已兑换");
        record.setCreatedAt(LocalDateTime.now());
        DataStorage.EXCHANGE_RECORDS.put(record.getRecordId(), record);

        return record;
    }

    public List<Product> getAllProducts() {
        return DataStorage.PRODUCTS.values().stream()
                .collect(Collectors.toList());
    }

    public List<ExchangeRecord> getUserExchangeRecords(String userId) {
        return DataStorage.EXCHANGE_RECORDS.values().stream()
                .filter(r -> r.getUserId().equals(userId))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
