package com.points.storage;

import com.points.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStorage {
    public static final Map<String, User> USERS = new ConcurrentHashMap<>();
    public static final Map<String, PointsRecord> POINTS_RECORDS = new ConcurrentHashMap<>();
    public static final Map<String, Order> ORDERS = new ConcurrentHashMap<>();
    public static final Map<String, Product> PRODUCTS = new ConcurrentHashMap<>();
    public static final Map<String, ExchangeRecord> EXCHANGE_RECORDS = new ConcurrentHashMap<>();
    public static final List<String> RISK_CONTROL_LIST = new ArrayList<>();

    static {
        initData();
    }

    private static void initData() {
        Product p1 = new Product();
        p1.setProductId("P001");
        p1.setName("100元优惠券");
        p1.setPointsRequired(1000);
        p1.setStock(100);
        p1.setLockedStock(0);
        PRODUCTS.put(p1.getProductId(), p1);

        Product p2 = new Product();
        p2.setProductId("P002");
        p2.setName("精美保温杯");
        p2.setPointsRequired(5000);
        p2.setStock(50);
        p2.setLockedStock(0);
        PRODUCTS.put(p2.getProductId(), p2);

        Product p3 = new Product();
        p3.setProductId("P003");
        p3.setName("高端耳机");
        p3.setPointsRequired(20000);
        p3.setStock(20);
        p3.setLockedStock(0);
        PRODUCTS.put(p3.getProductId(), p3);
    }
}
