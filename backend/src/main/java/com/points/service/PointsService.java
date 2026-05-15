package com.points.service;

import com.points.entity.PointsRecord;
import com.points.entity.User;
import com.points.enums.MemberLevel;
import com.points.enums.PointsSource;
import com.points.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PointsService {

    @Autowired
    private UserService userService;

    @Autowired
    private RiskControlService riskControlService;

    public PointsRecord addPoints(String userId, int points, PointsSource source, String description, String orderId) {
        User user = userService.getUser(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.isInRiskControl()) {
            throw new RuntimeException("用户在风控名单中，无法获取积分");
        }

        int dailyPoints = getDailyPoints(userId, source);
        if (dailyPoints + points > source.getDailyLimit()) {
            throw new RuntimeException("今日" + source.getDescription() + "积分已达上限");
        }

        int totalSourcePoints = getTotalSourcePoints(userId, source);
        if (totalSourcePoints + points > source.getTotalLimit()) {
            throw new RuntimeException(source.getDescription() + "总积分已达上限");
        }

        PointsRecord record = new PointsRecord();
        record.setRecordId(UUID.randomUUID().toString());
        record.setUserId(userId);
        record.setPoints(points);
        record.setSource(source);
        record.setDescription(description);
        record.setOrderId(orderId);
        record.setCreatedAt(LocalDateTime.now());
        record.setExpired(false);
        DataStorage.POINTS_RECORDS.put(record.getRecordId(), record);

        user.setTotalPoints(user.getTotalPoints() + points);
        user.setAvailablePoints(user.getAvailablePoints() + points);

        updateMemberLevel(user);

        riskControlService.checkAbnormalPointsGain(userId);

        return record;
    }

    public void deductPoints(String userId, int points, PointsSource source, String orderId) {
        User user = userService.getUser(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        int availablePoints = user.getAvailablePoints();
        if (availablePoints >= points) {
            user.setAvailablePoints(availablePoints - points);
        } else {
            user.setAvailablePoints(0);
            int owed = points - availablePoints;
            user.setOwedPoints(user.getOwedPoints() + owed);
        }

        user.setTotalPoints(Math.max(0, user.getTotalPoints() - points));
        user.setUsedPoints(user.getUsedPoints() + points);

        updateMemberLevel(user);
    }

    private int getDailyPoints(String userId, PointsSource source) {
        LocalDate today = LocalDate.now();
        return DataStorage.POINTS_RECORDS.values().stream()
                .filter(r -> r.getUserId().equals(userId) && r.getSource() == source && r.getPoints() > 0)
                .filter(r -> r.getCreatedAt().toLocalDate().equals(today))
                .mapToInt(PointsRecord::getPoints)
                .sum();
    }

    private int getTotalSourcePoints(String userId, PointsSource source) {
        return DataStorage.POINTS_RECORDS.values().stream()
                .filter(r -> r.getUserId().equals(userId) && r.getSource() == source && r.getPoints() > 0)
                .mapToInt(PointsRecord::getPoints)
                .sum();
    }

    private void updateMemberLevel(User user) {
        MemberLevel currentLevel = user.getCurrentLevel();
        MemberLevel newLevel = MemberLevel.getLevelByPoints(user.getTotalPoints());

        if (newLevel != currentLevel) {
            user.setCurrentLevel(newLevel);
            if (newLevel.getValidityDays() > 0) {
                user.setLevelExpireTime(LocalDateTime.now().plusDays(newLevel.getValidityDays()));
            }
        }
    }

    public List<PointsRecord> getUserPointsRecords(String userId) {
        return DataStorage.POINTS_RECORDS.values().stream()
                .filter(r -> r.getUserId().equals(userId))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public Map<PointsSource, Integer> getPointsSummary(String userId) {
        return DataStorage.POINTS_RECORDS.values().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.groupingBy(
                        PointsRecord::getSource,
                        Collectors.summingInt(PointsRecord::getPoints)
                ));
    }
}
