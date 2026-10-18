package com.jnotifier.services.impl;

import com.jnotifier.entity.Calendar;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.pojo.DateWiseActivityCountPojo;
import com.jnotifier.payload.request.AddNewActivityRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.CalendarRepository;
import com.jnotifier.services.ICalendarServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalendarServicesImpl implements ICalendarServices {
    @Autowired
    private CalendarRepository calendarRepository;

    @Override
    public ServiceReply createNewActivity(AddNewActivityRequest request) {
        Calendar calendar = new Calendar(request.getDescription(), request.getDate());
        calendarRepository.save(calendar);

        Map<String, Object> map = new HashMap<>();

        map.put("message", "New activity created");
        map.put("calendar", calendar);

        return new ServiceReply().build(HttpStatusCode.valueOf(201), map);
    }

    @Override
    public ServiceReply getActivityDetails(Long id, String createdBy) {
        Calendar calendar = calendarRepository.getActivityDetails(id, createdBy)
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_ID", "The activity details could not be found.")));

        Map<String, Object> map = new HashMap<>();

        map.put("message", "Activity details");
        map.put("calendar", calendar);
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getDateWiseActivityCountDetails(String createdBy) {
        List<DateWiseActivityCountPojo> dateWiseActivityCount = calendarRepository.getDateWiseActivityCount(createdBy);

        Map<String, Object> map = new HashMap<>();

        map.put("message", "Date wise activity count");
        map.put("activities", dateWiseActivityCount);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply markAsArchive(Long id, String createdBy) {
        Calendar calendar = calendarRepository.getActivityDetails(id, createdBy)
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_ID", "The activity details could not be found.")));

        calendar.setIsActive(false);
        calendarRepository.save(calendar);
        return new ServiceReply().build(HttpStatusCode.valueOf(204));
    }

    @Override
    public ServiceReply activateActivity(Long id, String createdBy){
        Calendar calendar = calendarRepository.getActivityDetails(id, createdBy)
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_ID", "The activity details could not be found.")));

        calendar.setIsActive(true);
        calendarRepository.save(calendar);
        return new ServiceReply().build(HttpStatusCode.valueOf(204));
    }

    @Override
    public ServiceReply deleteActivity(Long id, String createdBy) {
        Calendar calendar = calendarRepository.getActivityDetails(id, createdBy)
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_ID", "The activity details could not be found.")));

        calendar.setIsDeleted(true);
        calendarRepository.save(calendar);
        return new ServiceReply().build(HttpStatusCode.valueOf(204));
    }

    @Override
    public ServiceReply getDateWiseActivityDetails(String date, String createdBy){
        List<Calendar> details = calendarRepository.getDateWiseActivityDetails(date,createdBy);

        Map<String, Object> map = new HashMap<>();

        map.put("message", "Date wise activity details");
        map.put("activities", details);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }
}
