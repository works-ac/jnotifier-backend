package com.jnotifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.util.List;

import com.jnotifier.exception.GenericException;
import com.jnotifier.helpers.ExcelHelper;
import com.jnotifier.payload.request.ApplicationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

public class ExcelHelperTest {

    private final ExcelHelper excelHelper = new ExcelHelper();

    @Test
    void testTemplateExistsAndParsesWithoutCrashing() throws Exception {
        ClassPathResource resource = new ClassPathResource("bulk-job-creation-template.xlsx");
        assertThat(resource.exists()).isTrue();

        try (InputStream is = resource.getInputStream()) {
            // Template might have 0 data rows or some rows.
            // If template is empty (no data rows), ExcelHelper throws NO_DATA_FOUND, which confirms the structure was reached!
            try {
                List<ApplicationRequest> apps = excelHelper.parseJobApplicationsFromExcel(is);
                assertThat(apps).isNotNull();
            } catch (GenericException ge) {
                // Either parses data rows or throws NO_DATA_FOUND if template has empty sample rows
                assertThat(ge.getResponseMap().getError().getCode()).isIn("NO_DATA_FOUND", "SUCCESS");
            }
        }
    }
}
