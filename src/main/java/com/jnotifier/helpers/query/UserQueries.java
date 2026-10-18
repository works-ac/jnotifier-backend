package com.jnotifier.helpers.query;

public class UserQueries {
    public final static String GET_ALL_USER_DETAILS = "SELECT u FROM User u WHERE u.role.id NOT IN (3,4,5)";
}
