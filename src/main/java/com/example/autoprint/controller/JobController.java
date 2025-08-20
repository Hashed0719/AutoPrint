package com.example.autoprint.controller;

import com.example.autoprint.model.PrintOrder;
import com.example.autoprint.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping("/getPendingJobs")
    public ResponseEntity<List<PrintOrder>> getPendingJobs(@RequestParam String clientId) {
        List<PrintOrder> pendingJobs = jobService.getPendingJobsForClient(clientId);
        return ResponseEntity.ok(pendingJobs);
    }
}
