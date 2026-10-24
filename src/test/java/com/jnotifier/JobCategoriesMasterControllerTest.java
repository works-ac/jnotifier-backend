package com.jnotifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.jnotifier.controllers.JobCategoriesMasterController;
import com.jnotifier.helpers.CaptchaHelper;
import com.jnotifier.payload.request.AddNewJobCategoryRequest;
import com.jnotifier.payload.request.UpdateJobCategoryRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.services.impl.JobCategoriesServicesImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class JobCategoriesMasterControllerTest {

    @Mock
    private JobCategoriesServicesImpl jobCategoriesServices;

    @Mock
    private CaptchaHelper captchaHelper;

    @InjectMocks
    private JobCategoriesMasterController jobCategoriesMasterController;

    @Test
    void testAddNewJobCategoryWithValidCaptcha() {
        AddNewJobCategoryRequest request = new AddNewJobCategoryRequest("IT", "c-123", "ABC123");
        when(captchaHelper.validateCaptcha("c-123", "ABC123")).thenReturn(true);
        when(jobCategoriesServices.addNewJobCategory(any())).thenReturn(
                new ServiceReply().build(HttpStatusCode.valueOf(201))
        );

        ResponseEntity<ApiResponse<?>> response = jobCategoriesMasterController.addNewJobCategory(request);
        assertThat(response.getStatusCode().value()).isEqualTo(201);
    }

    @Test
    void testAddNewJobCategoryWithInvalidCaptcha() {
        AddNewJobCategoryRequest request = new AddNewJobCategoryRequest("IT", "c-123", "WRONG");
        when(captchaHelper.validateCaptcha("c-123", "WRONG")).thenReturn(false);

        ResponseEntity<ApiResponse<?>> response = jobCategoriesMasterController.addNewJobCategory(request);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getError().getCode()).isEqualTo("INVALID_CAPTCHA");
    }

    @Test
    void testUpdateJobCategoryWithValidCaptcha() {
        UpdateJobCategoryRequest request = new UpdateJobCategoryRequest(1L, "IT Updated", true, false, "c-123", "ABC123");
        when(captchaHelper.validateCaptcha("c-123", "ABC123")).thenReturn(true);
        when(jobCategoriesServices.updateJobCategory(any())).thenReturn(
                new ServiceReply().build(HttpStatusCode.valueOf(204))
        );

        ResponseEntity<ApiResponse<?>> response = jobCategoriesMasterController.updateJobCategory(request);
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void testUpdateJobCategoryWithInvalidCaptcha() {
        UpdateJobCategoryRequest request = new UpdateJobCategoryRequest(1L, "IT Updated", true, false, "c-123", "WRONG");
        when(captchaHelper.validateCaptcha("c-123", "WRONG")).thenReturn(false);

        ResponseEntity<ApiResponse<?>> response = jobCategoriesMasterController.updateJobCategory(request);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getError().getCode()).isEqualTo("INVALID_CAPTCHA");
    }
}
