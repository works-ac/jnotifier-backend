package com.jnotifier.helpers.query;

public class NoticeQueries {
    public static final String GET_NOTICE_BY_DELETION_STATUS = "SELECT n FROM Notice n WHERE n.isDeleted = :isDeleted AND n.createdBy = :createdBy";
    public static final String GET_NOTICE_BY_ACTIVE_STATUS = "SELECT n FROM Notice n WHERE n.isActive = :isActive AND n.createdBy = :createdBy";
    public static final String GET_ALL_USER_NOTICES = "SELECT n from Notice n WHERE n.createdBy = :createdBy";
    public static final String GET_NOTICE_BY_ACTIVE_STATUS_PUBLIC = "SELECT n FROM Notice n WHERE n.isActive = :isActive AND n.isDeleted = false";
    public static final String GET_NOTICE_LISTINGS_DETAILS_PUBLIC = "SELECT NEW com.jnotifier.payload.pojo.NoticeListingsPojo(\n" +
            "    n.id, \n" +
            "    n.title, \n" +
            "    n.tags, \n" +
            "    n.noticeDescription, \n" +
            "    u.fullname, \n" +
            "    n.createdAt, \n" +
            "    n.isActive)\n" +
            "FROM \n" +
            "    Notice n \n" +
            "    INNER JOIN User u ON n.createdBy = u.username \n" +
            "WHERE \n" +
            "    n.isActive = :isActive \n" +
            "    AND n.isDeleted = false";
    public static final String GET_NOTICE_LISTINGS_DETAILS_BY_SEARCH_CRITERIA_PUBLIC = "SELECT NEW com.jnotifier.payload.pojo.NoticeListingsPojo(\n" +
            "    n.id, \n" +
            "    n.title, \n" +
            "    n.tags, \n" +
            "    n.noticeDescription, \n" +
            "    u.fullname, \n" +
            "    n.createdAt\n" +
            ")\n" +
            "FROM \n" +
            "    Notice n \n" +
            "    INNER JOIN User u ON n.createdBy = u.username \n" +
            "WHERE \n" +
            "    n.isActive = :isActive \n" +
            "    AND n.isDeleted = false\n" +
            "   AND n.tags LIKE %:tags%";
}
