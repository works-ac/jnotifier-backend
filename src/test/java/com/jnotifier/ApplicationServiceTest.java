package com.jnotifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import com.jnotifier.entity.Application;
import com.jnotifier.payload.request.ApplicationRequest;
import com.jnotifier.repository.ApplicationRepository;
import com.jnotifier.services.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private Application existingApplication;

    @BeforeEach
    void setUp() {
        existingApplication = new Application();
        existingApplication.setId(1L);
        existingApplication.setTitle("Old Title");
        existingApplication.setTags("java,spring");
        existingApplication.setApplicationStartDate(LocalDate.now());
        existingApplication.setApplicationEndDate(LocalDate.now().plusDays(10));
        existingApplication.setShortDescription("Old short desc");
        existingApplication.setViewPageDescription("Old markdown content");
        existingApplication.setAdvFileName("/uploads/old-file.pdf");
        existingApplication.setApplyLink("https://old.link");
        existingApplication.setStatus(true);
    }

    @Test
    void testUpdateApplication_withNewFiles() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(existingApplication));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApplicationRequest request = new ApplicationRequest();
        request.setTitle("New Title");
        request.setTags("react,node");
        request.setApplicationStartDate(LocalDate.now());
        request.setApplicationEndDate(LocalDate.now().plusDays(15));
        request.setShortDescription("New short desc");
        request.setAdvNo("ADV-002");
        request.setApplyLink("https://new.link");
        request.setViewPageDescription("New markdown content");
        request.setAdvFileName("/uploads/new-file.pdf");

        Application updated = applicationService.update(1L, request);

        assertThat(updated.getTitle()).isEqualTo("New Title");
        assertThat(updated.getViewPageDescription()).isEqualTo("New markdown content");
        assertThat(updated.getAdvFileName()).isEqualTo("/uploads/new-file.pdf");
        assertThat(updated.getApplicationEndDate()).isEqualTo(LocalDate.now().plusDays(15));
    }

    @Test
    void testUpdateApplication_preservesExistingFilesWhenNull() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(existingApplication));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApplicationRequest request = new ApplicationRequest();
        request.setTitle("Updated Title Only");
        request.setTags("java,spring");
        request.setApplicationStartDate(LocalDate.now());
        request.setApplicationEndDate(LocalDate.now().plusDays(10));
        request.setShortDescription("Old short desc");
        request.setAdvNo("ADV-001");
        request.setApplyLink("https://old.link");
        request.setViewPageDescription(null);
        request.setAdvFileName(null);

        Application updated = applicationService.update(1L, request);

        assertThat(updated.getTitle()).isEqualTo("Updated Title Only");
        assertThat(updated.getViewPageDescription()).isEqualTo("Old markdown content");
        assertThat(updated.getAdvFileName()).isEqualTo("/uploads/old-file.pdf");
    }
}
