package com.points.service;

import com.points.entity.PointsRecord;
import com.points.entity.User;
import com.points.storage.DataStorage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RiskControlService {

    private static final int MAX_POINTS_IN_HOUR = 500;
    private static final int MAX_RECORDS_IN_HOUR = 10;

    public void checkAbnormalPointsGain(String userId) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        List<PointsRecord> recentRecords = DataStorage.POINTS_RECORDS.values().stream()
                .filter(r -> r.getUserId().equals(userId) && r.getPoints() > 0)
                .filter(r -> r.getCreatedAt().isAfter(oneHourAgo))
                .collect(Collectors.toList());

        int totalPoints = recentRecords.stream()
                .mapToInt(PointsRecord::getPoints)
                .sum();

        if (totalPoints >= MAX_POINTS_IN_HOUR || recentRecords.size() >= MAX_RECORDS_IN_HOUR) {
            User user = DataStorage.USERS.get(userId);
            if (user != null) {
                user.setInRiskControl(true);
                user.setRecentAbnormalPoints(true);
                if (!DataStorage.RISK_CONTROL_LIST.contains(userId)) {
                    DataStorage.RISK_CONTROL_LIST.add(userId);
                }
            }
        }
    }

    public List<String> getRiskControlList() {
        return DataStorage.RISK_CONTROL_LIST;
    }

    public void removeFromRiskControl(String userId) {
        DataStorage.RISK_CONTROL_LIST.remove(userId);
        User user = DataStorage.USERS.get(userId);
        if (user != null) {
            user.setInRiskControl(false);
            user.setRecentAbnormalPoints(false);
        }
    }
}
