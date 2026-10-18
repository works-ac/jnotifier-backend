package com.jnotifier.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applied_jobs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "application_id"})
})
public class AppliedJob extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "applied_on", nullable = false)
    private LocalDateTime appliedOn;

    public AppliedJob() {
    }

    public AppliedJob(User user, Application application, LocalDateTime appliedOn) {
        this.user = user;
        this.application = application;
        this.appliedOn = appliedOn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public LocalDateTime getAppliedOn() {
        return appliedOn;
    }

    public void setAppliedOn(LocalDateTime appliedOn) {
        this.appliedOn = appliedOn;
    }
}
