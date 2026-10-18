package com.jnotifier.controllers;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.services.impl.MediaServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/downloads")
public class DownloadController {
    @Autowired
    private MediaServiceImpl mediaService;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> download(@PathVariable String filename, @RequestParam Long mediaId, HttpServletRequest request)
            throws IOException{
        return mediaService.downloadMedia(filename, mediaId, request);
    }
}
