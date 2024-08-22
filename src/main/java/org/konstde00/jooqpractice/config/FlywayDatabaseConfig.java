package org.konstde00.jooqpractice.config;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FlywayProperties.class)
public class FlywayDatabaseConfig {

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public FlywayDatabaseConfig(@Value("${spring.datasource.url}") String jdbcUrl,
                                @Value("${spring.datasource.username}") String username,
                                @Value("${spring.datasource.password}") String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(FlywayProperties flywayProperties) {
        return Flyway.configure()
                .dataSource(
                        DataSourceBuilder.create()
                                .url(jdbcUrl)
                                .username(username)
                                .password(password)
                                .driverClassName("com.mysql.cj.jdbc.Driver")
                                .build()
                )
                .locations(flywayProperties.getLocations().toArray(String[]::new))
                .baselineOnMigrate(true)
                .load();
    }

}
