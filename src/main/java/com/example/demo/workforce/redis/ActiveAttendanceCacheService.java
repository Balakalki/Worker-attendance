package com.example.demo.workforce.redis;

import com.example.demo.workforce.Site;
import com.example.demo.workforce.Worker;
import com.example.demo.workforce.dto.ActiveAttendanceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class ActiveAttendanceCacheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveAttendanceCacheService.class);
    private static final String ACTIVE_WORKERS_KEY = "attendance:active-workers";
    private static final String ACTIVE_WORKER_KEY_PREFIX = "attendance:active-worker:";
    private static final Duration ACTIVE_WORKER_TTL = Duration.ofHours(16);

    private final RedisTemplate<String, Object> redisTemplate;

    public ActiveAttendanceCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addActiveWorker(Worker worker, Site site, Instant clockInAt) {
        ActiveAttendanceResponse activeAttendance = new ActiveAttendanceResponse(
                worker.getId(),
                worker.getName(),
                worker.getPhone(),
                worker.getDesignation(),
                worker.getDailyWageRate(),
                site.getId(),
                site.getName(),
                site.getLocation(),
                clockInAt
        );

        try {
            redisTemplate.opsForValue().set(activeWorkerKey(worker.getId()), activeAttendance, ACTIVE_WORKER_TTL);
            redisTemplate.opsForSet().add(ACTIVE_WORKERS_KEY, worker.getId().toString());
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis unavailable while adding active worker {}", worker.getId(), exception);
        }
    }

    public void removeActiveWorker(Long workerId) {
        try {
            redisTemplate.delete(activeWorkerKey(workerId));
            redisTemplate.opsForSet().remove(ACTIVE_WORKERS_KEY, workerId.toString());
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis unavailable while removing active worker {}", workerId, exception);
        }
    }

    public void invalidateWorker(Long workerId) {
        try {
            redisTemplate.delete(activeWorkerKey(workerId));
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis unavailable while invalidating worker {}", workerId, exception);
        }
    }

    public List<ActiveAttendanceResponse> findAllActiveWorkers() {
        try {
            Set<Object> workerIds = redisTemplate.opsForSet().members(ACTIVE_WORKERS_KEY);
            if (workerIds == null || workerIds.isEmpty()) {
                return Collections.emptyList();
            }

            List<ActiveAttendanceResponse> activeAttendances = new ArrayList<>();
            for (Object workerId : workerIds) {
                Object cachedValue = redisTemplate.opsForValue().get(activeWorkerKey(workerId.toString()));
                if (cachedValue instanceof ActiveAttendanceResponse) {
                    activeAttendances.add((ActiveAttendanceResponse) cachedValue);
                } else {
                    redisTemplate.opsForSet().remove(ACTIVE_WORKERS_KEY, workerId);
                }
            }
            return activeAttendances;
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis unavailable while reading active workers", exception);
            return Collections.emptyList();
        }
    }

    public boolean exists(Long workerId) {
    try {
        Boolean exists = redisTemplate.hasKey(activeWorkerKey(workerId));
        return Boolean.TRUE.equals(exists);
    } catch (RuntimeException exception) {
        LOGGER.warn("Redis unavailable while checking active worker {}", workerId, exception);
        return false;
    }
}

    private String activeWorkerKey(Long workerId) {
        return activeWorkerKey(workerId.toString());
    }

    private String activeWorkerKey(String workerId) {
        return ACTIVE_WORKER_KEY_PREFIX + workerId;
    }
}
