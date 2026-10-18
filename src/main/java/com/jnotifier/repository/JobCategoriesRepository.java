package com.jnotifier.repository;

import com.jnotifier.entity.JobCategories;
import com.jnotifier.helpers.query.JobCategoriesQueries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobCategoriesRepository extends JpaRepository<JobCategories, Long> {
    @Query(JobCategoriesQueries.GET_ALL_JOB_CATEGORIES_BY_ADMIN)
    Page<JobCategories> findAllJobCategoriesByAdmin(@Param("createdBy") String createdBy, Pageable pageable);

    @Query(JobCategoriesQueries.GET_ALL_JOB_CATEGORIES_PUBLIC)
    List<JobCategories> findAllJobCategoriesPublic();
}
