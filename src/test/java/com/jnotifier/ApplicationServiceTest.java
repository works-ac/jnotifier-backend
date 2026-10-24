package com.jnotifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jnotifier.entity.Application;
import com.jnotifier.exception.GenericException;
import com.jnotifier.helpers.FileHelper;
import com.jnotifier.payload.request.ApplicationRequest;
import com.jnotifier.repository.ApplicationRepository;
import com.jnotifier.services.core.FileStorageService;
import com.jnotifier.services.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private FileHelper fileHelper;

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

    @Test
    void testSaveBulk_success() {
        when(applicationRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<ApplicationRequest> requests = new ArrayList<>();
        ApplicationRequest req1 = new ApplicationRequest();
        req1.setTitle("Backend Engineer");
        req1.setTags("java, spring");
        req1.setApplicationStartDate(LocalDate.now());
        req1.setApplicationEndDate(LocalDate.now().plusDays(30));
        req1.setShortDescription("Job description for backend engineer");
        req1.setAdvNo("ADV-101");
        req1.setApplyLink("https://example.com/apply1");

        ApplicationRequest req2 = new ApplicationRequest();
        req2.setTitle("Frontend Engineer");
        req2.setTags("react, vite");
        req2.setApplicationStartDate(LocalDate.now());
        req2.setApplicationEndDate(LocalDate.now().plusDays(20));
        req2.setShortDescription("Job description for frontend engineer");
        req2.setAdvNo("ADV-102");
        req2.setApplyLink("https://example.com/apply2");

        requests.add(req1);
        requests.add(req2);

        List<Application> saved = applicationService.saveBulk(requests);

        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).getTitle()).isEqualTo("Backend Engineer");
        assertThat(saved.get(1).getTitle()).isEqualTo("Frontend Engineer");
    }

    @Test
    void testSaveBulk_invalidEndDate() {
        List<ApplicationRequest> requests = new ArrayList<>();
        ApplicationRequest req1 = new ApplicationRequest();
        req1.setTitle("Invalid Dates");
        req1.setTags("test");
        req1.setApplicationStartDate(LocalDate.now());
        req1.setApplicationEndDate(LocalDate.now().minusDays(1)); // end before start
        req1.setShortDescription("Description");
        req1.setApplyLink("https://example.com");
        requests.add(req1);

        assertThatThrownBy(() -> applicationService.saveBulk(requests))
                .isInstanceOf(GenericException.class)
                .hasMessageContaining("Application end date cannot be before application start date");
    }

    @Test
    void testUploadFiles_success() throws IOException {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(existingApplication));
        when(fileHelper.isValidPdf(any())).thenReturn(true);
        when(fileStorageService.saveFile(any())).thenReturn("saved-file.pdf");
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile mdFile = new MockMultipartFile("file", "details.md", "text/markdown", "# Details".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile pdfFile = new MockMultipartFile("advFile", "adv.pdf", "application/pdf", "%PDF-1.4 sample content".getBytes(StandardCharsets.UTF_8));

        Application updated = applicationService.uploadFiles(1L, mdFile, pdfFile);

        assertThat(updated.getViewPageDescription()).isEqualTo("# Details");
        assertThat(updated.getAdvFileName()).isEqualTo("/uploads/saved-file.pdf");
    }
}
