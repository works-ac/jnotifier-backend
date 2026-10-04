package com.jnotifier.payload.response;

import org.springframework.http.HttpStatusCode;
import java.io.Serializable;
import java.util.Map;

// 1. Still add Serializable
public class ServiceReply implements Serializable {

    private static final long serialVersionUID = 1L;

    // 2. Change the INTERNAL field to an int
    private int statusCodeValue;

    private Map<String, Object> reply;

    // 3. Keep the exact same public getter
    public HttpStatusCode getHttpStatusCode() {
        // Reconstruct the HttpStatusCode on the fly for the rest of your app
        return statusCodeValue == 0 ? null : HttpStatusCode.valueOf(this.statusCodeValue);
    }

    // 4. Keep the exact same public setter
    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        // Extract the int to store it safely for Redis
        this.statusCodeValue = (httpStatusCode != null) ? httpStatusCode.value() : 0;
    }

    public Map<String, Object> getReply() {
        return reply;
    }

    public void setReply(Map<String, Object> reply) {
        this.reply = reply;
    }

    // 5. Keep the exact same builders
    public ServiceReply build(HttpStatusCode httpStatusCode, Map<String, Object> reply) {
        this.setHttpStatusCode(httpStatusCode);
        this.reply = reply;
        return this;
    }

    public ServiceReply build(HttpStatusCode httpStatusCode) {
        this.setHttpStatusCode(httpStatusCode);
        return this;
    }
}