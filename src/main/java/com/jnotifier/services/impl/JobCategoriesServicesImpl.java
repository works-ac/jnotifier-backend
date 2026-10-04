package com.jnotifier.services.impl;

import com.jnotifier.entity.JobCategories;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.pojo.PublicJobCategoriesPojo;
import com.jnotifier.payload.request.AddNewJobCategoryRequest;
import com.jnotifier.payload.request.UpdateJobCategoryRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.JobCategoriesRepository;
import com.jnotifier.services.IJobCategoriesServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JobCategoriesServicesImpl implements IJobCategoriesServices {
    @Autowired
    private JobCategoriesRepository jobCategoriesRepository;

    @Override
    public ServiceReply addNewJobCategory(AddNewJobCategoryRequest request) {
        JobCategories categories = new JobCategories(request.getJobCategoryName());
        Map<String, Object> map = new HashMap<>();

        categories = jobCategoriesRepository.save(categories);

        map.put("message", "Job category added successfully");
        map.put("category", categories);

        return new ServiceReply().build(HttpStatusCode.valueOf(201), map);
    }

    @Override
    public ServiceReply updateJobCategory(UpdateJobCategoryRequest request) {
        JobCategories category = jobCategoriesRepository.findById(request.getJobCategoryId())
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_ID", "The provided ID is corrupted")));

        category.setCategoryName(Optional.ofNullable(request.getJobCategoryName()).orElse(category.getCategoryName()));
        category.setIsActive(Optional.ofNullable(request.getIsActive()).orElse(category.getIsActive()));
        category.setIsDeleted(Optional.ofNullable(request.getIsDeleted()).orElse(category.getIsDeleted()));

        jobCategoriesRepository.save(category);
        return new ServiceReply().build(HttpStatusCode.valueOf(204));
    }

    @Override
    public ServiceReply listAllJobCategories() {
        Map<String, Object> map = new HashMap<>();
        List<JobCategories> jobCategories = jobCategoriesRepository.findAll();

        map.put("message", "Job categories listed successfully");
        map.put("categories", jobCategories);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply listAllJobCategories(int page, int size, String createdBy) {
        Map<String, Object> map = new HashMap<>();
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<JobCategories> jobCategories = jobCategoriesRepository.findAllJobCategoriesByAdmin(createdBy, pageable);

        map.put("message", "Job categories listed successfully");
        map.put("list", jobCategories);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply listAllJobCategoriesPublic() {
        Map<String, Object> map = new HashMap<>();
        List<PublicJobCategoriesPojo> jobCategories = jobCategoriesRepository.findAllJobCategoriesPublic().stream()
                .map(category -> new PublicJobCategoriesPojo(category.getCategoryName()))
                .toList();

        map.put("message", "Job categories listed successfully");
        map.put("categories", jobCategories);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply markJobCategoryAsArchived(Long jobCategoryId) {
        JobCategories category = jobCategoriesRepository.findById(jobCategoryId)
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_ID", "The provided ID is corrupted")));

        category.setIsActive(false);
        jobCategoriesRepository.save(category);

        return new ServiceReply().build(HttpStatusCode.valueOf(204));
    }

    @Override
    public ServiceReply markJobCategoryAsDeleted(Long jobCategoryId) {
        JobCategories category = jobCategoriesRepository.findById(jobCategoryId)
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_ID", "The provided ID is corrupted")));

        category.setIsDeleted(true);
        jobCategoriesRepository.save(category);

        return new ServiceReply().build(HttpStatusCode.valueOf(204));
    }

    @Override
    public ServiceReply markJobCategoryAsActive(Long jobCategoryId) {
        JobCategories category = jobCategoriesRepository.findById(jobCategoryId)
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_ID", "The provided ID is corrupted")));

        category.setIsActive(true);
        jobCategoriesRepository.save(category);

        return new ServiceReply().build(HttpStatusCode.valueOf(204));
    }
}
