package com.jnotifier.config;

import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaDependencyConfig extends EntityManagerFactoryDependsOnPostProcessor {

    public JpaDependencyConfig() {
        super("jNotifierPostConstructComponent");
    }
}