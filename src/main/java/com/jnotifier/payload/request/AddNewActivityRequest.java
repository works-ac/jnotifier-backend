package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddNewActivityRequest {
    @NotNull(message = "Description is required")
    @Size(max = 512, message = "Too large description")
    private String description;

    @NotNull(message = "Date is required")
    @Size(max = 12, message = "Too large date")
    private String date;

    public AddNewActivityRequest() {
    }

    public AddNewActivityRequest(String description, String date) {
        this.description = description;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
