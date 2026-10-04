package com.jnotifier.repository;

import java.util.List;

import com.jnotifier.helpers.query.ApplicationQueries;
import com.jnotifier.payload.pojo.JobsListingsPojo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.jnotifier.entity.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStatusTrue();

    Page<Application> findByStatusTrue(Pageable pageable);

    Page<Application> findByCreatedBy(String createdBy, Pageable pageable);

    @Query(ApplicationQueries.GET_ALL_ARCHIVED_PUBLIC_JOBS)
    Page<Application> findAllArchivedPublicApplications(Pageable pageable);

    @Query(ApplicationQueries.GET_ALL_JOB_LISTING_DETAILS_PUBLIC)
    Page<JobsListingsPojo> findAllJobListingsDetails(@Param("status") Boolean status, Pageable pageable);

    @Query(ApplicationQueries.GET_ALL_JOB_LISTINGS_BY_SEARCH_CRITERIA)
    Page<JobsListingsPojo> findAllJobListingsBySearchCriteria(@Param("status") Boolean status, @Param("tags") String tags, Pageable pageable);
}
