package com.jnotifier.helpers.query;

public class CalendarQueries {
    public static final String GET_ALL_ACTIVE_ACTIVITIES = "SELECT c FROM Calendar c WHERE c.isActive = true AND c.createdBy = :createdBy";
    public static final String GET_ACTIVITY_DETAILS_BY_ID_AND_USER = "SELECT c FROM Calendar c WHERE c.id = :id AND c.createdBy = :createdBy";
    public static final String GET_DATE_WISE_ACTIVITY_COUNT =
            "SELECT new com.jnotifier.payload.pojo.DateWiseActivityCountPojo(c.activityDate, COUNT(c.activityDate))\n" +
                    "FROM Calendar c \n" +
                    "WHERE c.isActive = true \n" +
                    "  AND c.isDeleted = false \n" +
                    "  AND c.createdBy = :createdBy \n" +
                    "GROUP BY c.activityDate, c.isActive, c.isDeleted";
    public static final String GET_DATE_WISE_ACTIVITY_DETAILS = "SELECT c FROM Calendar c WHERE c.createdBy = :createdBy AND c.activityDate = :activityDate";
}
