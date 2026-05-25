package com.example.demo.workforce;

import com.example.demo.workforce.dto.CreateWorkerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {
    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping
    public List<Worker> findAllWorkers() {
        return workerService.findAllWorkers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Worker createWorker(@Valid @RequestBody CreateWorkerRequest request) {
        return workerService.createWorker(request);
    }

    @PutMapping("/{workerId}")
    public Worker updateWorker(@PathVariable Long workerId, @Valid @RequestBody CreateWorkerRequest request) {
        return workerService.updateWorker(workerId, request);
    }
}
