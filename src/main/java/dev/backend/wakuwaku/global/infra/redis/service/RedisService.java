package dev.backend.wakuwaku.global.infra.redis.service;

import dev.backend.wakuwaku.global.infra.google.places.dto.Places;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static dev.backend.wakuwaku.global.infra.redis.constant.RedisConstant.*;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void savePlaces(String key, List<Places> placesList) {
        ListOperations<String, Object> operations = redisTemplate.opsForList();

        String realKey = key.replaceAll("\\s+", "");

        for (Places places : placesList) {
            operations.rightPush(realKey, places);
        }

        operations.leftPush(realKey, calculateTotalPage(placesList.size()));

        redisTemplate.expire(realKey, REDIS_TTL, TimeUnit.DAYS);
    }

    private int calculateTotalPage(int size) {
        int remain = size % 10;
        int quotient = size / 10;

        if (remain == 0 && quotient == 0) {
            return 0;
        }

        if (remain == 0) {
            return quotient;
        }

        return quotient + 1;
    }

    public List<Places> getPlacesByRedis(String key, int page) {
        if (page <= 0) {
            return Collections.emptyList();
        }

        ListOperations<String, Object> operations = redisTemplate.opsForList();

        String realKey = key.replaceAll("\\s+", "");

        int startIdx = 10 * (page - 1);

        if (page == BELOW_DEFAULT_PAGE) {
            startIdx = DEFAULT_START_INDEX;
        }

        List<Object> objects = operations.range(realKey, startIdx + 1, startIdx + PLUS_END_INDEX);

        if (objects == null || objects.isEmpty()) {
            return Collections.emptyList();
        }

        redisTemplate.expire(realKey, REDIS_TTL, TimeUnit.DAYS);

        return objects.stream()
                .filter(Places.class::isInstance)
                .map(Places.class::cast)
                .toList();
    }

    public int getTotalPage(String key) {
        ListOperations<String, Object> operations = redisTemplate.opsForList();

        String realKey = key.replaceAll("\\s+", "");

        Object totalPage = operations.index(realKey, 0);

        if (totalPage == null) {
            return 0;
        }

        return (int) totalPage;
    }

    public void deletePlaces(String key) {
        redisTemplate.delete(key);
    }
}
