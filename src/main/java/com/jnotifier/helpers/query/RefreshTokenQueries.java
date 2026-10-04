package com.jnotifier.helpers.query;

public class RefreshTokenQueries {
    public static final String GET_USER_BY_REFRESH_TOKEN = "SELECT r FROM refreshtoken r WHERE r.user.id = :userId";
}
