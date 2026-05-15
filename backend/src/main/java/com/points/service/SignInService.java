package com.points.service;

import com.points.entity.PointsRecord;
import com.points.entity.User;
import com.points.enums.PointsSource;
import com.points.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SignInService {

    @Autowired
    private UserService userService;

    @Autowired
    private PointsService pointsService;

    public PointsRecord signIn(String userId) {
        User user = userService.getUser(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        LocalDate today = LocalDate.now();
        boolean alreadySigned = user.getSignInDates().stream()
                .anyMatch(d -> d.toLocalDate().equals(today));

        if (alreadySigned) {
            throw new RuntimeException("今日已签到");
        }

        user.getSignInDates().add(LocalDateTime.now());

        int consecutiveDays = getConsecutiveSignInDays(user);
        int points = 10 + Math.min(consecutiveDays, 5) * 2;

        return pointsService.addPoints(userId, points, PointsSource.SIGN_IN, "连续签到" + consecutiveDays + "天", null);
    }

    private int getConsecutiveSignInDays(User user) {
        int count = 0;
        LocalDate checkDate = LocalDate.now();

        while (true) {
            LocalDate finalCheckDate = checkDate;
            boolean signed = user.getSignInDates().stream()
                    .anyMatch(d -> d.toLocalDate().equals(finalCheckDate));
            if (signed) {
                count++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;
            }
        }

        return count;
    }

    public boolean hasSignedToday(String userId) {
        User user = userService.getUser(userId);
        if (user == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return user.getSignInDates().stream()
                .anyMatch(d -> d.toLocalDate().equals(today));
    }
}
