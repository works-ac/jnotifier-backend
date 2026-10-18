package com.jnotifier.services;

import com.jnotifier.payload.request.AddNewJobCategoryRequest;
import com.jnotifier.payload.request.UpdateJobCategoryRequest;
import com.jnotifier.payload.response.ServiceReply;

public interface IJobCategoriesServices {
    public ServiceReply addNewJobCategory(AddNewJobCategoryRequest request);

    public ServiceReply updateJobCategory(UpdateJobCategoryRequest request);

    public ServiceReply listAllJobCategories();

    public ServiceReply listAllJobCategories(int page, int size, String createdBy);

    public ServiceReply listAllJobCategoriesPublic();

    public ServiceReply markJobCategoryAsArchived(Long jobCategoryId);

    public ServiceReply markJobCategoryAsDeleted(Long jobCategoryId);

    public ServiceReply markJobCategoryAsActive(Long jobCategoryId);
}
