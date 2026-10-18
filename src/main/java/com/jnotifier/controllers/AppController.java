package com.jnotifier.controllers;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.payload.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/app")
public class AppController {
    @GetMapping("ping")
    public ResponseEntity<ApiResponse<Object>> ping() {
        Map<String,String> pingReply = new HashMap<>();
        pingReply.put("message", "pong");

        return ResponseEntity.ok(ApiResponse.success(pingReply));
    }
}
