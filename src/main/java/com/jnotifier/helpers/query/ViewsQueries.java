package com.jnotifier.helpers.query;

public class ViewsQueries {
    public static final String GET_PAGE_VIEWS =
            "SELECT new com.jnotifier.payload.pojo.PageViewsPojo(" +
                    "   v.ipAddress, " +
                    "   v.visitedDate, " +
                    "   COUNT(v.ipAddress), " +
                    "   v.visitedPage" +
                    ") " +
                    "FROM Views v " +
                    "WHERE v.visitedPage = :visitedPage " +
                    "  AND v.visitedDate IS NOT NULL " +
                    "  AND v.visitedPage IS NOT NULL " +
                    "GROUP BY v.ipAddress, v.visitedDate, v.visitedPage";

    public static final String GET_DAILY_ACTIVE_VIEWS = "SELECT new com.jnotifier.payload.pojo.DailyActiveUsersPojo(" +
            "   v.ipAddress, " +
            "   v.visitedDate, " +
            "   COUNT(v.ipAddress) " +
            ") " +
            "FROM Views v " +
            "WHERE v.visitedDate = :visitedDate " +
            "  AND v.visitedDate IS NOT NULL " +
            "GROUP BY v.ipAddress, v.visitedDate";

    public static final String GET_TOTAL_VIEWS = "SELECT new com.jnotifier.payload.pojo.DailyActiveUsersPojo(" +
            "   v.ipAddress, " +
            "   v.visitedDate, " +
            "   COUNT(v.ipAddress) " +
            ") " +
            "FROM Views v " +
            "WHERE v.visitedDate IS NOT NULL " +
            "GROUP BY v.ipAddress, v.visitedDate";
}