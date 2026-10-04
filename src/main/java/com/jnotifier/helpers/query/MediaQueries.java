package com.jnotifier.helpers.query;

public class MediaQueries {
    public static final String GET_ALL_ACTIVE_MEDIA = "SELECT m FROM Media m WHERE m.isPublic = true";
    public static final String GET_ALL_MEDIA_BY_CREATED_BY = "SELECT m FROM Media m WHERE m.createdBy = :createdBy";
}
