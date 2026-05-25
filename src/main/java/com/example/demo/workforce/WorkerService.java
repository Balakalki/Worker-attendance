package com.example.demo.workforce;

import com.example.demo.workforce.dto.CreateWorkerRequest;
import com.example.demo.workforce.exception.WorkforceApiException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkerService {
    private final WorkerRepository workerRepository;

    public WorkerService(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
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
}
