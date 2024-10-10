package com.capstone.usa.job.service;

import com.capstone.usa.job.dto.JobDto;
import com.capstone.usa.job.model.Job;
import com.capstone.usa.job.repository.JobRepository;
import com.capstone.usa.auth.model.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class JobService {
    private JobRepository jobRepository;

    public List<Job> getJobs() {
        return jobRepository.findAll();
    }

    @Transactional
    public ResponseEntity<Job> getJob(Long id) {
        Optional<Job> oJob = jobRepository.findById(id);

        return oJob.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public void createJob(User user, JobDto dto) {
        Job job = new Job(
                null,
                dto.getTitle(),
                dto.getContent(),
                user,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        jobRepository.save(job);
    }

    private ResponseEntity<?> checkJobOwnership(Long id, User user) {
        Optional<Job> oJob = jobRepository.findById(id);

        if (oJob.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Job job = oJob.get();
        if (!job.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("내 글만 수정할 수 있습니다");
        }

        return ResponseEntity.ok(job);
    }

    public ResponseEntity<?> modifyJob(Long id, User user, JobDto dto) {
        ResponseEntity<?> response = checkJobOwnership(id, user);
        if (response.getStatusCode() != HttpStatus.OK) {
            return response;
        }

        Job job = (Job) response.getBody();
        job.setTitle(dto.getTitle());
        job.setContent(dto.getContent());
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> deleteJob(Long id, User user) {
        ResponseEntity<?> response = checkJobOwnership(id, user);
        if (response.getStatusCode() != HttpStatus.OK) {
            return response;
        }

        Job job = (Job) response.getBody();
        jobRepository.delete(job);

        return ResponseEntity.ok().build();
    }
}
