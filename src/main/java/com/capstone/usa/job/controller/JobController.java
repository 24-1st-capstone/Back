package com.capstone.usa.job.controller;

import com.capstone.usa.job.dto.JobDto;
import com.capstone.usa.job.model.Job;
import com.capstone.usa.job.service.JobService;
import com.capstone.usa.auth.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/jobs")
@AllArgsConstructor
public class JobController {
    private JobService jobService;

    @GetMapping
    public List<Job> getJob() {
        return jobService.getJobs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJob(
            @PathVariable Long id
    ) {
        return jobService.getJob(id);
    }

    @PostMapping
    public void createJob(
            @AuthenticationPrincipal User user,
            @RequestBody JobDto dto
    ) {
        jobService.createJob(user, dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifyJob(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody JobDto dto
    ) {
        return jobService.modifyJob(id, user, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return jobService.deleteJob(id, user);
    }
}
