package com.jnotifier.services;

import com.jnotifier.payload.request.AddNewActivityRequest;
import com.jnotifier.payload.response.ServiceReply;

public interface ICalendarServices {
    public ServiceReply createNewActivity(AddNewActivityRequest request);

    public ServiceReply getDateWiseActivityCountDetails(String createdBy);

    public ServiceReply getDateWiseActivityDetails(String date, String createdBy);

    public ServiceReply getActivityDetails(Long id, String createdBy);

    public ServiceReply markAsArchive(Long id, String createdBy);

    public ServiceReply activateActivity(Long id, String createdBy);

    public ServiceReply deleteActivity(Long id, String createdBy);
}
