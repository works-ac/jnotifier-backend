package com.jnotifier.repository;

import com.jnotifier.entity.Notice;
import com.jnotifier.helpers.query.NoticeQueries;
import com.jnotifier.payload.pojo.NoticeListingsPojo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Query(NoticeQueries.GET_NOTICE_BY_ACTIVE_STATUS)
    Page<Notice> findByIsActive(@Param("isActive") Boolean isActive, @Param("createdBy") String createdBy, Pageable pageable);

    @Query(NoticeQueries.GET_NOTICE_BY_ACTIVE_STATUS_PUBLIC)
    Page<Notice> findByIsActivePublic(@Param("isActive") Boolean isActive, Pageable pageable);

    @Query(NoticeQueries.GET_NOTICE_BY_DELETION_STATUS)
    Page<Notice> findByIsDeleted(@Param("isDeleted") Boolean isDeleted, @Param("createdBy") String createdBy, Pageable pageable);

    @Query(NoticeQueries.GET_ALL_USER_NOTICES)
    Page<Notice> findAllUserNotices(@Param("createdBy") String createdBy, Pageable pageable);

    @Query(NoticeQueries.GET_NOTICE_LISTINGS_DETAILS_PUBLIC)
    Page<NoticeListingsPojo> findAllNoticeListingsByCreatedByPublic(@Param("isActive") Boolean isActive, Pageable pageable);

    @Query(NoticeQueries.GET_NOTICE_LISTINGS_DETAILS_BY_SEARCH_CRITERIA_PUBLIC)
    Page<NoticeListingsPojo> findAllNoticeListingsBySearchCriteriaPublic(@Param("isActive") Boolean isActive, @Param("tags") String tags, Pageable pageable);
}
