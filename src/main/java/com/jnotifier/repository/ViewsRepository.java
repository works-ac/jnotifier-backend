package com.jnotifier.repository;

import com.jnotifier.entity.Views;
import com.jnotifier.helpers.query.ViewsQueries;
import com.jnotifier.payload.pojo.DailyActiveUsersPojo;
import com.jnotifier.payload.pojo.PageViewsPojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViewsRepository extends JpaRepository<Views, Integer> {
    Optional<Views> findByIpAddress(String ipAddress);

    @Query(value = ViewsQueries.GET_PAGE_VIEWS)
    List<PageViewsPojo> findPageViews(@Param("visitedPage") String visitedPage);

    @Query(value = ViewsQueries.GET_DAILY_ACTIVE_VIEWS)
    List<DailyActiveUsersPojo> findDailyActiveViews(@Param("visitedDate") String visitedDate);

    @Query(value = ViewsQueries.GET_TOTAL_VIEWS)
    List<DailyActiveUsersPojo> findTotalViews();
}
