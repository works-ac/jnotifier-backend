package com.jnotifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;

import com.jnotifier.controllers.DashboardController;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.repository.ApplicationRepository;
import com.jnotifier.repository.MediaRepository;
import com.jnotifier.repository.NoticeRepository;
import com.jnotifier.repository.UserRepository;
import com.jnotifier.services.impl.ViewsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class DashboardControllerTest {

    @Mock
    private ViewsServiceImpl viewsService;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private DashboardController dashboardController;

    @Test
    void testGetDashboardStats() {
        when(applicationRepository.count()).thenReturn(15L);
        when(noticeRepository.count()).thenReturn(7L);
        when(userRepository.count()).thenReturn(25L);
        when(mediaRepository.countActiveMedia()).thenReturn(10L);
        when(applicationRepository.countByStatusFalse()).thenReturn(4L);
        when(noticeRepository.countArchivedNotices()).thenReturn(2L);

        ResponseEntity<ApiResponse<Map<String, Object>>> response = dashboardController.getDashboardStats();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).containsEntry("totalJobs", 15L);
        assertThat(response.getBody().getData()).containsEntry("totalNotices", 7L);
        assertThat(response.getBody().getData()).containsEntry("totalUsers", 25L);
        assertThat(response.getBody().getData()).containsEntry("totalMedias", 10L);
        assertThat(response.getBody().getData()).containsEntry("archivedJobs", 4L);
        assertThat(response.getBody().getData()).containsEntry("archivedNotices", 2L);
    }

    @Test
    void testGetArchivedJobs() {
        when(applicationRepository.countByStatusFalse()).thenReturn(8L);

        ResponseEntity<ApiResponse<Map<String, Object>>> response = dashboardController.getArchivedJobs();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).containsEntry("archivedJobs", 8L);
    }

    @Test
    void testGetArchivedNotices() {
        when(noticeRepository.countArchivedNotices()).thenReturn(3L);

        ResponseEntity<ApiResponse<Map<String, Object>>> response = dashboardController.getArchivedNotices();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).containsEntry("archivedNotices", 3L);
    }
}
