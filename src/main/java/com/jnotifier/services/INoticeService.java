package com.jnotifier.services;

import com.jnotifier.payload.request.AddNewNoticeRequest;
import com.jnotifier.payload.request.UpdateNoticeRequest;
import com.jnotifier.payload.response.ServiceReply;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface INoticeService {
    /* DML METHODS */
    public ServiceReply addNewNotice(AddNewNoticeRequest newNoticeRequest, MultipartFile noticeAdvertisement) throws IOException;

    public ServiceReply updateNotice(UpdateNoticeRequest updateNoticeRequest);

    public ServiceReply deleteNotice(Long id);

    public ServiceReply markAsArchived(Long id);

    public ServiceReply markAsActive(Long id);

    /* GET METHODS */
    public ServiceReply getAllActiveNotices(String createdBy, Pageable pageable);

    public ServiceReply getAllArchivedPublicNotices(Pageable pageable);

    public ServiceReply getAllActivePublicNotices(Pageable pageable);

    public ServiceReply getAllDeletedNotices(String createdBy, Pageable pageable);

    public ServiceReply getNotice(Long id);

    public ServiceReply getAllArchivedNotices(String createdBy, Pageable pageable);

    public ServiceReply getAllUserNotices(String createdBy, Pageable pageable);

    public ServiceReply getAllActiveNoticeListingsPublic(Pageable pageable);

    public ServiceReply getAllArchiveNoticeListingsPublic(Pageable pageable);

    public ServiceReply getAllNoticeListingsBySearchCriteriaPublic(Boolean isActive, String tags, Pageable pageable);
}
