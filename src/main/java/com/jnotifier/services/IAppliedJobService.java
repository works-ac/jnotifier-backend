package com.jnotifier.services;

import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.response.ServiceReply;
import org.springframework.data.domain.Pageable;

public interface IAppliedJobService {
    ServiceReply markJobAsApplied(String username, Long applicationId) throws GenericException;
    ServiceReply getUserAppliedJobs(String username, Pageable pageable) throws GenericException;
}
