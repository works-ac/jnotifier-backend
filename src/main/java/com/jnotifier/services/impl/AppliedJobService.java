package com.jnotifier.services.impl;

import com.jnotifier.entity.Application;
import com.jnotifier.entity.AppliedJob;
import com.jnotifier.entity.User;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.PaginatedResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.ApplicationRepository;
import com.jnotifier.repository.AppliedJobRepository;
import com.jnotifier.repository.UserRepository;
import com.jnotifier.services.IAppliedJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AppliedJobService implements IAppliedJobService {

    @Autowired
    private AppliedJobRepository appliedJobRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public ServiceReply markJobAsApplied(String username, Long applicationId) throws GenericException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new GenericException(ApiResponse.error("NOT_FOUND", "User not found!")));
            
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new GenericException(ApiResponse.error("NOT_FOUND", "Job not found!")));
            
        if (appliedJobRepository.existsByUserAndApplication(user, application)) {
            throw new GenericException(ApiResponse.error("CONFLICT", "You have already marked this job as applied."));
        }
        
        AppliedJob appliedJob = new AppliedJob(user, application, LocalDateTime.now());
        appliedJobRepository.save(appliedJob);
        
        Map<String, Object> reply = new HashMap<>();
        reply.put("message", "Job marked as applied successfully!");
        return new ServiceReply().build(HttpStatusCode.valueOf(201), reply);
    }

    @Override
    public ServiceReply getUserAppliedJobs(String username, Pageable pageable) throws GenericException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new GenericException(ApiResponse.error("NOT_FOUND", "User not found!")));
            
        Page<AppliedJob> appliedJobs = appliedJobRepository.findByUser(user, pageable);
        
        Page<Map<String, Object>> jobsPage = appliedJobs.map(aj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", aj.getId());
            map.put("appliedOn", aj.getAppliedOn());
            map.put("applicationId", aj.getApplication().getId());
            map.put("title", aj.getApplication().getTitle());
            map.put("advertisementNo", aj.getApplication().getAdvertisementNo());
            map.put("tags", aj.getApplication().getTags());
            map.put("applyLink", aj.getApplication().getApplyLink());
            return map;
        });

        Map<String, Object> reply = new HashMap<>();
        reply.put("message", "Applied jobs fetched successfully!");
        reply.put("list", new PaginatedResponse<>(jobsPage));

        return new ServiceReply().build(HttpStatusCode.valueOf(200), reply);
    }
}
