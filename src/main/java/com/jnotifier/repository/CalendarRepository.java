package com.jnotifier.repository;

import com.jnotifier.entity.Calendar;
import com.jnotifier.helpers.query.CalendarQueries;
import com.jnotifier.payload.pojo.DateWiseActivityCountPojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    @Query(CalendarQueries.GET_ALL_ACTIVE_ACTIVITIES)
    public List<Calendar> findAllActiveActivitiesByUser(@Param("createdBy") String createdBy);

    @Query(CalendarQueries.GET_ACTIVITY_DETAILS_BY_ID_AND_USER)
    public Optional<Calendar> getActivityDetails(@Param("id") Long id, @Param("createdBy") String createdBy);

    @Query(CalendarQueries.GET_DATE_WISE_ACTIVITY_COUNT)
    public List<DateWiseActivityCountPojo> getDateWiseActivityCount(@Param("createdBy") String createdBy);

    @Query(CalendarQueries.GET_DATE_WISE_ACTIVITY_DETAILS)
    public List<Calendar> getDateWiseActivityDetails(@Param("activityDate") String activityDate, @Param("createdBy") String createdBy);
}
