package com.jnotifier.services.impl;

import com.jnotifier.entity.Notice;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.pojo.NoticeListingsPojo;
import com.jnotifier.payload.request.AddNewNoticeRequest;
import com.jnotifier.payload.request.UpdateNoticeRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.NoticeRepository;
import com.jnotifier.services.INoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class NoticeService implements INoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    @Override
    public ServiceReply addNewNotice(AddNewNoticeRequest request, MultipartFile noticeAdvertisement) throws IOException {
        Notice notice;

        if (noticeAdvertisement != null && !noticeAdvertisement.isEmpty()) {
            String advFilename = noticeAdvertisement.getOriginalFilename();
            String advContentType = noticeAdvertisement.getContentType();
            boolean hasMdExtension = advFilename != null && advFilename.toLowerCase().endsWith(".md");
            boolean hasMdMimeType = "text/markdown".equalsIgnoreCase(advContentType);

            if (!hasMdExtension && !hasMdMimeType)
                throw new GenericException(ApiResponse.error("INVALID_FILE_EXT", "Please upload a markdown file."));

            long fileSize = noticeAdvertisement.getSize() / (1024 * 1024);

            if (fileSize >= 5)
                throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "Your file size is too large"));

            byte[] fileBytes = noticeAdvertisement.getBytes();
            String markdownContent = new String(fileBytes, StandardCharsets.UTF_8);

            request.setNoticeAdvertisement(markdownContent);
            notice = new Notice(request.getNoticeTitle(), request.getNoticeDesc(), request.getNoticeTags(), request.getNoticeAdvertisement());
        } else {
            notice = new Notice(request.getNoticeTitle(), request.getNoticeDesc(), request.getNoticeTags());
        }

        Map<String, Object> map = new HashMap<>();
        noticeRepository.save(notice);
        map.put("message", "Notice added");
        map.put("notice", notice);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply updateNotice(UpdateNoticeRequest request) {
        Notice notice = noticeRepository.findById(request.getNoticeId())
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_NOTICE", "This notice does not exist")));

        notice.setTitle(Optional.ofNullable(request.getNoticeTitle()).orElse(notice.getTitle()));
        notice.setNoticeDescription(Optional.ofNullable(request.getNoticeDesc()).orElse(notice.getNoticeDescription()));
        notice.setTags(Optional.ofNullable(request.getNoticeTags()).orElse(notice.getTags()));

        noticeRepository.save(notice);
        return new ServiceReply().build(HttpStatusCode.valueOf(204));
    }

    @Override
    public ServiceReply deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_NOTICE", "This notice does not exist")));

        notice.setIsDeleted(true);
        noticeRepository.save(notice);

        return new ServiceReply().build(HttpStatusCode.valueOf(204));
    }

    @Override
    public ServiceReply markAsArchived(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_NOTICE", "This notice does not exist")));
        Map<String, Object> map = new HashMap<>();

        notice.setIsActive(false);
        noticeRepository.save(notice);
        map.put("message", "Notice marked as archived.");
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply markAsActive(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_NOTICE", "This notice does not exist")));
        Map<String, Object> map = new HashMap<>();

        notice.setIsActive(true);
        noticeRepository.save(notice);
        map.put("message", "Notice marked as archived.");
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getAllActiveNotices(String createdBy, Pageable pageable) {
        Map<String, Object> map = new HashMap<>();
        Page<Notice> notices = noticeRepository.findByIsActive(true, createdBy, pageable);

        map.put("message", "All active notices are fetched successfully");
        map.put("list", notices);
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getAllActivePublicNotices(Pageable pageable) {
        Map<String, Object> map = new HashMap<>();
        Page<Notice> notices = noticeRepository.findByIsActivePublic(true, pageable);

        map.put("message", "All active notices are fetched successfully");
        map.put("list", notices);
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getAllDeletedNotices(String createdBy, Pageable pageable) {
        Map<String, Object> map = new HashMap<>();
        Page<Notice> notices = noticeRepository.findByIsDeleted(true, createdBy, pageable);

        map.put("message", "All deleted notices are fetched successfully");
        map.put("list", notices);
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getNotice(Long id) {
        Map<String, Object> map = new HashMap<>();
        Notice notices = noticeRepository.findById(id).orElseThrow(() -> new GenericException(ApiResponse.error("INVALID_NOTICE", "This notice does not exists")));

        map.put("message", "Notice found");
        map.put("notice", notices);
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getAllArchivedNotices(String createdBy, Pageable pageable) {
        Map<String, Object> map = new HashMap<>();
        Page<Notice> notices = noticeRepository.findByIsActive(false, createdBy, pageable);

        map.put("message", "All archived notices are fetched successfully");
        map.put("list", notices);
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getAllUserNotices(String createdBy, Pageable pageable) {
        Map<String, Object> map = new HashMap<>();
        Page<Notice> notices = noticeRepository.findAllUserNotices(createdBy, pageable);

        map.put("message", "All user's notices are fetched successfully");
        map.put("list", notices);
        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getAllArchivedPublicNotices(Pageable pageable) {
        Map<String, Object> map = new HashMap<>();
        Page<Notice> notices = noticeRepository.findByIsActivePublic(false, pageable);

        map.put("message", "All archived notices are fetched successfully");
        map.put("list", notices);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getAllActiveNoticeListingsPublic(Pageable pageable) {
        Map<String, Object> map = new HashMap<>();
        Page<NoticeListingsPojo> notices = noticeRepository.findAllNoticeListingsByCreatedByPublic(true, pageable);

        map.put("message", "All active notices are fetched successfully");
        map.put("list", notices);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getAllArchiveNoticeListingsPublic(Pageable pageable) {
        Map<String, Object> map = new HashMap<>();
        Page<NoticeListingsPojo> notices = noticeRepository.findAllNoticeListingsByCreatedByPublic(false, pageable);

        map.put("message", "All archive notices are fetched successfully");
        map.put("list", notices);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }

    @Override
    public ServiceReply getAllNoticeListingsBySearchCriteriaPublic(Boolean isActive, String tags, Pageable pageable) {
        Map<String, Object> map = new HashMap<>();
        Page<NoticeListingsPojo> notices = noticeRepository.findAllNoticeListingsBySearchCriteriaPublic(isActive, tags, pageable);

        map.put("message", "All notices are fetched successfully");
        map.put("list", notices);

        return new ServiceReply().build(HttpStatusCode.valueOf(200), map);
    }
}
