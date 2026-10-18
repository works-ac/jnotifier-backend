package com.jnotifier.repository;

import com.jnotifier.entity.AppliedJob;
import com.jnotifier.entity.User;
import com.jnotifier.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppliedJobRepository extends JpaRepository<AppliedJob, Long> {
    boolean existsByUserAndApplication(User user, Application application);
    Page<AppliedJob> findByUser(User user, Pageable pageable);
}
