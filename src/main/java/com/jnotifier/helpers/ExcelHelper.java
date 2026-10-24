package com.jnotifier.helpers;

import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.request.ApplicationRequest;
import com.jnotifier.payload.response.ApiResponse;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelHelper {

    private static final int HEADER_ROW_INDEX = 5; // 0-based index for Row 6
    private static final int DATA_START_ROW_INDEX = 6; // 0-based index for Row 7
    private static final int MAX_JOBS = 25;

    private static final DateTimeFormatter[] DATE_FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };

    public List<ApplicationRequest> parseJobApplicationsFromExcel(InputStream inputStream) {
        List<ApplicationRequest> applications = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new GenericException(ApiResponse.error("INVALID_EXCEL", "Excel file has no sheets."));
            }

            int lastRowNum = sheet.getLastRowNum();
            for (int r = DATA_START_ROW_INDEX; r <= lastRowNum; r++) {
                Row row = sheet.getRow(r);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                if (applications.size() >= MAX_JOBS) {
                    throw new GenericException(ApiResponse.error("EXCEEDS_MAX_LIMIT",
                            "You can create at most 25 jobs at a time from this template."));
                }

                int displayRowNumber = r + 1;
                ApplicationRequest req = parseRow(row, displayRowNumber);
                applications.add(req);
            }

            if (applications.isEmpty()) {
                throw new GenericException(ApiResponse.error("NO_DATA_FOUND",
                        "No job application records found in the uploaded excel file."));
            }

            return applications;
        } catch (GenericException ge) {
            throw ge;
        } catch (Exception e) {
            throw new GenericException(ApiResponse.error("EXCEL_PARSING_ERROR",
                    "Failed to parse excel file: " + e.getMessage()));
        }
    }

    private ApplicationRequest parseRow(Row row, int rowNum) {
        ApplicationRequest req = new ApplicationRequest();

        // Col 1: Title (Col index 1, Col B)
        String title = getCellStringValue(row.getCell(1));
        req.setTitle(title);

        // Col 2: Job Description (Col index 2, Col C)
        String description = getCellStringValue(row.getCell(2));
        req.setShortDescription(description);

        // Col 3: Tags (Col index 3, Col D)
        String tags = getCellStringValue(row.getCell(3));
        req.setTags(tags);

        // Col 4: Start Date (Col index 4, Col E)
        LocalDate startDate = getCellLocalDateValue(row.getCell(4), "Application Start Date", rowNum);
        req.setApplicationStartDate(startDate);

        // Col 5: End Date (Col index 5, Col F)
        LocalDate endDate = getCellLocalDateValue(row.getCell(5), "Application End Date", rowNum);
        req.setApplicationEndDate(endDate);

        // Col 6: Adv No (Col index 6, Col G)
        String advNo = getCellStringValue(row.getCell(6));
        req.setAdvNo(advNo);

        // Col 7: Apply Link (Col index 7, Col H)
        String applyLink = getCellStringValue(row.getCell(7));
        req.setApplyLink(applyLink);

        req.setStatus(true);

        return req;
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String val = getCellStringValue(cell);
                if (val != null && !val.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        DataFormatter formatter = new DataFormatter();
        String val = formatter.formatCellValue(cell);
        return (val != null && !val.trim().isEmpty()) ? val.trim() : null;
    }

    private LocalDate getCellLocalDateValue(Cell cell, String fieldName, int rowNum) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else {
                double num = cell.getNumericCellValue();
                if (DateUtil.isValidExcelDate(num)) {
                    return DateUtil.getJavaDate(num).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                }
            }
        }

        String strVal = getCellStringValue(cell);
        if (strVal == null) {
            return null;
        }

        for (DateTimeFormatter dtf : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(strVal, dtf);
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new GenericException(ApiResponse.error("INVALID_DATE_FORMAT",
                "Row " + rowNum + ": Invalid date format for '" + fieldName + "': '" + strVal + "'. Use DD-MM-YYYY or YYYY-MM-DD."));
    }
}
