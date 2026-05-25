package com.example.demo.workforce.redis;

import com.example.demo.workforce.AttendanceLog;
import com.example.demo.workforce.AttendanceRepository;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ActiveAttendanceExpirationListener implements MessageListener {
    private static final String ACTIVE_WORKER_KEY_PREFIX = "attendance:active-worker:";

    private final AttendanceRepository attendanceRepository;
    private final ActiveAttendanceCacheService activeAttendanceCacheService;

    public ActiveAttendanceExpirationListener(AttendanceRepository attendanceRepository,
                                              ActiveAttendanceCacheService activeAttendanceCacheService) {
        this.attendanceRepository = attendanceRepository;
        this.activeAttendanceCacheService = activeAttendanceCacheService;
    }

    @Override
    @Transactional
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if (!expiredKey.startsWith(ACTIVE_WORKER_KEY_PREFIX)) {
            return;
        }

        Long workerId = Long.valueOf(expiredKey.substring(ACTIVE_WORKER_KEY_PREFIX.length()));
        attendanceRepository.findByWorkerIdAndClockOutAtIsNull(workerId)
                .ifPresent(this::flagExpiredAttendance);
        activeAttendanceCacheService.removeActiveWorker(workerId);
    }

    private void flagExpiredAttendance(AttendanceLog attendanceLog) {
        attendanceLog.setFlagged(true);
        attendanceRepository.save(attendanceLog);
    }
}
