package com.jnotifier.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
public class RootController {

    @Value("classpath:banner.txt")
    private Resource bannerResource;

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getBanner() throws IOException {
        String bannerContent = StreamUtils.copyToString(bannerResource.getInputStream(), StandardCharsets.UTF_8);
        return ResponseEntity.ok(bannerContent);
    }
}
