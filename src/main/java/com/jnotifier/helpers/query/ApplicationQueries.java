package com.jnotifier.helpers.query;

public class ApplicationQueries {
    public static final String GET_ALL_ARCHIVED_PUBLIC_JOBS = "SELECT a FROM Application a WHERE a.status = false";
    public static final String GET_ALL_JOB_LISTING_DETAILS_PUBLIC = "SELECT NEW com.jnotifier.payload.pojo.JobsListingsPojo(\n" +
            "    j.id, \n" +
            "    j.applicationStartDate, \n" +
            "    j.applicationEndDate, \n" +
            "    j.title, \n" +
            "    j.shortDescription, \n" +
            "    u.fullname, \n" +
            "    j.createdAt, \n" +
            "    j.tags, \n" +
            "    j.status, \n" +
            "    j.advertisementNo\n" +
            ")\n" +
            "FROM \n" +
            "    Application j \n" +
            "    INNER JOIN User u ON j.createdBy = u.username \n" +
            "WHERE \n" +
            "    j.status = :status";

    public static final String GET_ALL_JOB_LISTINGS_BY_SEARCH_CRITERIA = "SELECT NEW com.jnotifier.payload.pojo.JobsListingsPojo(\n" +
            "    j.id, \n" +
            "    j.applicationStartDate, \n" +
            "    j.applicationEndDate, \n" +
            "    j.title, \n" +
            "    j.shortDescription, \n" +
            "    u.fullname, \n" +
            "    j.createdAt, \n" +
            "    j.tags, \n" +
            "    j.status, \n" +
            "    j.advertisementNo\n" +
            ")\n" +
            "FROM \n" +
            "    Application j \n" +
            "    INNER JOIN User u ON j.createdBy = u.username \n" +
            "WHERE \n" +
            "    j.status = :status AND \n"+
            "    j.tags LIKE %:tags%";
}
