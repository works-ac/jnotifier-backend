package com.jnotifier.services.impl;

import com.jnotifier.entity.Views;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.pojo.DailyActiveUsersPojo;
import com.jnotifier.payload.pojo.PageViewsPojo;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.ViewsRepository;
import com.jnotifier.services.IViewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ViewsServiceImpl implements IViewsService {
    @Autowired
    private ViewsRepository viewsRepository;

    @Override
    public ServiceReply addView(Map<String, String> clientDetails) {
        Map<String, Object> map = new HashMap<>();

        String ipAddress = clientDetails.get("ipAddress");
        String visitedPage = clientDetails.get("visitedPage");
        String browserName = clientDetails.get("browserName");
        String browserVersion = clientDetails.get("browserVersion");
        String osName = clientDetails.get("osName");
        String deviceType = clientDetails.get("deviceType");
        String deviceVendor = clientDetails.get("deviceVendor");
        String visitedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        viewsRepository.save(new Views(ipAddress, visitedDate, visitedPage, browserName, browserVersion, osName,
                deviceType, deviceVendor));

        map.put("message", "Thanks for visiting JNotifier.");
        return new ServiceReply()
                .build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    @Cacheable(value = "calcNGet", key = "#visitedPage")
    public ServiceReply getPageViews(String visitedPage) {
        Map<String, Object> map = new HashMap<>();
        List<PageViewsPojo> pageViews = viewsRepository.findPageViews(visitedPage);
        Long totalViews = pageViews.stream().map(PageViewsPojo::getPageViews).reduce(0L, Long::sum);

        map.put("message", "Page views fetched successfully.");
        map.put("views", totalViews);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getDailyActiveViews(String visitedDate) {
        List<DailyActiveUsersPojo> dau = viewsRepository.findDailyActiveViews(visitedDate);
        Long totalViews = dau.stream().map(DailyActiveUsersPojo::getDau).reduce(0L, Long::sum);
        Map<String, Object> map = new HashMap<>();

        map.put("message", "Daily active views fetched successfully.");
        map.put("views", totalViews);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getTotalViews() {
        Map<String, Object> map = new HashMap<>();
        List<DailyActiveUsersPojo> viewDetails = viewsRepository.findTotalViews();
        Long totalViews = viewDetails.stream().map(DailyActiveUsersPojo::getDau).reduce(0L, Long::sum);

        map.put("message", "Total views fetched successfully.");
        map.put("views", totalViews);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }
}
