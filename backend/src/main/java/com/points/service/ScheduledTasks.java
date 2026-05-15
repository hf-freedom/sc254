package com.points.service;

import com.points.entity.PointsRecord;
import com.points.entity.User;
import com.points.enums.MemberLevel;
import com.points.storage.DataStorage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ScheduledTasks {

    @Scheduled(cron = "0 0 2 * * ?")
    public void expirePoints() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        DataStorage.POINTS_RECORDS.values().stream()
                .filter(r -> !r.isExpired() && r.getPoints() > 0 && r.getCreatedAt().isBefore(oneYearAgo))
                .forEach(r -> {
                    r.setExpired(true);
                    User user = DataStorage.USERS.get(r.getUserId());
                    if (user != null) {
                        user.setAvailablePoints(Math.max(0, user.getAvailablePoints() - r.getPoints()));
                    }
                });
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void checkLevelExpiration() {
        LocalDateTime now = LocalDateTime.now();
        DataStorage.USERS.values().forEach(user -> {
            if (user.getLevelExpireTime() != null && user.getLevelExpireTime().isBefore(now)) {
                MemberLevel newLevel = MemberLevel.getLevelByPoints(user.getTotalPoints());
                user.setCurrentLevel(newLevel);
                if (newLevel.getValidityDays() > 0) {
                    user.setLevelExpireTime(LocalDateTime.now().plusDays(newLevel.getValidityDays()));
                } else {
                    user.setLevelExpireTime(null);
                }
            }
        });
    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void maintainLevel() {
        DataStorage.USERS.values().forEach(user -> {
            if (user.getTotalPoints() > 0) {
                MemberLevel currentLevel = user.getCurrentLevel();
                MemberLevel actualLevel = MemberLevel.getLevelByPoints(user.getTotalPoints());
                if (actualLevel.compareTo(currentLevel) > 0) {
                    user.setCurrentLevel(actualLevel);
                    if (actualLevel.getValidityDays() > 0) {
                        user.setLevelExpireTime(LocalDateTime.now().plusDays(actualLevel.getValidityDays()));
                    }
                }
            }
        });
    }
}
