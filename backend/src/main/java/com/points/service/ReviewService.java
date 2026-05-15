package com.points.service;

import com.points.entity.PointsRecord;
import com.points.enums.PointsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    private PointsService pointsService;

    public PointsRecord review(String userId, String orderId, int rating) {
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("评分必须在1-5之间");
        }

        int points = rating * 10;
        return pointsService.addPoints(userId, points, PointsSource.REVIEW, "评价获得" + rating + "星", orderId);
    }
}
