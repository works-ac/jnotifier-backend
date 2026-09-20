package com.jnotifier.components;

import com.jnotifier.helpers.query.migrations.CreateSchemaQuery;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("jNotifierPostConstructComponent")
public class JNotifierPostConstructComponent {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(JNotifierPostConstructComponent.class);

    public JNotifierPostConstructComponent(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        try {
            jdbcTemplate.execute(CreateSchemaQuery.CREATE_MASTER_SCHEMA_QUERY);
            jdbcTemplate.execute(CreateSchemaQuery.UPDATE_ROLES_CHECK_CONSTRAINT);
            jdbcTemplate.execute(CreateSchemaQuery.UPDATE_USERS_DOB_NULLABLE);
            logger.info("ALL SCHEMA QUERIES HAVE BEEN EXECUTED SUCCESSFULLY.");
        } catch (Exception e) {
            logger.warn("Schema initialization warning: {}", e.getMessage());
        }
    }
}
