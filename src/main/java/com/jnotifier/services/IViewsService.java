package com.jnotifier.services;

import com.jnotifier.payload.response.ServiceReply;

import java.util.Map;

public interface IViewsService {
    public ServiceReply addView(Map<String,String> clientDetails);
    public ServiceReply getPageViews(String visitedPage);
    public ServiceReply getDailyActiveViews(String visitedDate);
    public ServiceReply getTotalViews();
}
