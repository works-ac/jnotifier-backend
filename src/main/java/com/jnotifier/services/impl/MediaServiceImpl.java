package com.jnotifier.services.impl;

import com.jnotifier.entity.Media;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.MediaRepository;
import com.jnotifier.services.IMediaService;
import com.jnotifier.services.core.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MediaServiceImpl implements IMediaService {
    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public ServiceReply addMedia(MultipartFile file) {
        if (file == null)
            throw new GenericException(ApiResponse.error("FILE_ERR", "File is required"));

        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();

        if (contentType == null)
            throw new GenericException(ApiResponse.error("FILE_ERR", "File content type is required"));

        final String contentTypeRegex = "^(application/pdf|image/.*)$";
        boolean hasInvalidExtension = !contentType.matches(contentTypeRegex);

        if (hasInvalidExtension)
            throw new GenericException(ApiResponse.error("FILE_TYPE_ERR", "Invalid file extension, unsupported file detected"));

        long fileSize = file.getSize() / (1024 * 1024);

        if (fileSize >= 10)
            throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "Your file size is too large"));

        String fileUri = fileStorageService.saveFile(file);

        Media media = new Media(fileName, contentType.toLowerCase(), file.getSize(), fileUri);
        mediaRepository.save(media);

        Map<String, Object> map = new HashMap<>();

        map.put("message", "File successfully uploaded.");
        map.put("fileUri", "/downloads/" + fileUri);

        return new ServiceReply().build(HttpStatusCode.valueOf(201), map);
    }

    @Override
    public ServiceReply listActiveMedia(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Media> mediaList = mediaRepository.findAllActiveMedia(pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("mediaList", mediaList);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply listAllMedia(String createdBy, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Media> mediaList = mediaRepository.findAllMediaByCreatedBy(createdBy, pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("list", mediaList);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply changeMediaVisibility(Long mediaId, Boolean visibility) {
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new GenericException(ApiResponse.error("MEDIA_ERR", "Invalid media")));

        media.setIsPublic(visibility);
        mediaRepository.save(media);

        return new ServiceReply().build(HttpStatusCode.valueOf(204));
    }

    @Override
    public ServiceReply deleteMedia(Long mediaId) {
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new GenericException(ApiResponse.error("MEDIA_ERR", "Invalid media")));

        media.setIsDeleted(true);
        mediaRepository.save(media);

        return new ServiceReply().build(HttpStatusCode.valueOf(200));
    }

    @Override
    public ResponseEntity<Resource> downloadMedia(String fileName, Long mediaId, HttpServletRequest request) throws IOException {
        Media media = mediaRepository.findById(mediaId).orElseThrow(()-> new GenericException(ApiResponse.error("MEDIA_ERR", "Invalid media")));

        if (!media.getIsPublic() || media.getIsDeleted()) throw new GenericException(ApiResponse.error("MEDIA_TYPE_ERR", "Media has been deleted"));

        Resource resource = fileStorageService.loadFileAsResource(fileName);
        String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
