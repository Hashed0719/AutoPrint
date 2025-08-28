package com.example.autoprint.controller;

import com.example.autoprint.model.PrintOrder;
import com.example.autoprint.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // @GetMapping("/user")
    // public ResponseEntity<List<PrintOrder>> getOrdersByUser(@RequestParam Long userId) {
    //     List<PrintOrder> userOrders = jobService.getOrdersByUserId(userId);
    //     return ResponseEntity.ok(userOrders);
    // }

    @GetMapping("/user")
    public ResponseEntity<List<PrintOrder>> getOrdersByUsername(@RequestParam String username) {
        List<PrintOrder> userOrders = jobService.getOrdersByUsername(username);
        return ResponseEntity.ok(userOrders);
    }

}
