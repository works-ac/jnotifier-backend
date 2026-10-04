package com.jnotifier.helpers.query.migrations;

public class CreateSchemaQuery {
    public static final String CREATE_MASTER_SCHEMA_QUERY = "CREATE SCHEMA IF NOT EXISTS masters AUTHORIZATION postgres;";
    public static final String UPDATE_ROLES_CHECK_CONSTRAINT = "ALTER TABLE IF EXISTS roles DROP CONSTRAINT IF EXISTS roles_name_check;";
    public static final String UPDATE_USERS_DOB_NULLABLE = "ALTER TABLE IF EXISTS users ALTER COLUMN dob DROP NOT NULL;";
}


