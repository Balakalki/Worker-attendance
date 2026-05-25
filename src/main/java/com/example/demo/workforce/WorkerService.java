package com.example.demo.workforce;

import com.example.demo.workforce.dto.CreateWorkerRequest;
import com.example.demo.workforce.exception.WorkforceApiException;
import com.example.demo.workforce.redis.ActiveAttendanceCacheService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkerService {
    private final WorkerRepository workerRepository;
    private final ActiveAttendanceCacheService activeAttendanceCacheService;

    public WorkerService(WorkerRepository workerRepository, ActiveAttendanceCacheService activeAttendanceCacheService) {
        this.workerRepository = workerRepository;
        this.activeAttendanceCacheService = activeAttendanceCacheService;
    }

    @Transactional(readOnly = true)
    public List<Worker> findAllWorkers() {
        return workerRepository.findAll();
    }

    @Transactional
    public Worker createWorker(CreateWorkerRequest request) {
        Worker worker = new Worker();
        worker.setName(request.getName().trim());
        worker.setPhone(request.getPhone().trim());
        worker.setDesignation(request.getDesignation());
        worker.setDailyWageRate(request.getDailyWageRate());
        worker.setActive(request.getActive() == null || request.getActive());

        try {
            return workerRepository.save(worker);
        } catch (DataIntegrityViolationException exception) {
            throw new WorkforceApiException("WORKER_ALREADY_EXISTS", "A worker with this phone already exists", HttpStatus.CONFLICT);
        }
    }

    @Transactional
    public Worker updateWorker(Long workerId, CreateWorkerRequest request) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new WorkforceApiException("WORKER_NOT_FOUND", "Worker not found", HttpStatus.NOT_FOUND));

        worker.setName(request.getName().trim());
        worker.setPhone(request.getPhone().trim());
        worker.setDesignation(request.getDesignation());
        worker.setDailyWageRate(request.getDailyWageRate());
        worker.setActive(request.getActive() == null || request.getActive());

        try {
            Worker savedWorker = workerRepository.save(worker);
            activeAttendanceCacheService.invalidateWorker(workerId);
            return savedWorker;
        } catch (DataIntegrityViolationException exception) {
            throw new WorkforceApiException("WORKER_ALREADY_EXISTS", "A worker with this phone already exists", HttpStatus.CONFLICT);
        }
    }
}
