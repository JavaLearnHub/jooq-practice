package org.konstde00.jooqpractice.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.*;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@Slf4j
public class MySQLConfig {

  public static final String QUALIFIER_MYSQL_CONTEXT = "mysqlContext";
  public static final String QUALIFIER_MYSQL_TRANSACTION_MANAGER = "mysqlTransactionManager";
  public static final String QUALIFIER_MYSQL_DATA_SOURCE = "mysqlDataSource";
  public static final String QUALIFIER_MYSQL_CONFIGURATION = "mysqlConfiguration";

  @Bean(QUALIFIER_MYSQL_CONFIGURATION)
  public DefaultConfiguration jooqConfigurations(
      @Qualifier(QUALIFIER_MYSQL_DATA_SOURCE) DataSource dataSource) throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      MappedSchema mappedSchema =
          new MappedSchema().withInput("op_dev").withOutput(connection.getCatalog());

      var configuration = jooqConfiguration();
      configuration.settings().withRenderMapping(new RenderMapping().withSchemata(mappedSchema));
      configuration.setDataSource(dataSource);
      return configuration;
    }
  }

  public static DefaultConfiguration jooqConfiguration() {
    DefaultConfiguration configuration = new DefaultConfiguration();
    Settings settings =
        new Settings()
            .withRenderSchema(false)
            .withRenderQuotedNames(RenderQuotedNames.NEVER)
            .withRenderNameCase(RenderNameCase.AS_IS);
    configuration.set(settings);
    configuration.setSQLDialect(SQLDialect.MYSQL);
    return configuration;
  }

  @Bean(QUALIFIER_MYSQL_CONTEXT)
  public DSLContext mySqlContext(
      @Qualifier(QUALIFIER_MYSQL_CONFIGURATION) DefaultConfiguration configuration) {
    return new DefaultDSLContext(configuration);
  }

  @Primary
  @Bean(QUALIFIER_MYSQL_DATA_SOURCE)
  @ConfigurationProperties("spring.datasource")
  public DataSource mysqlDataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @DependsOnDatabaseInitialization
  @Bean(QUALIFIER_MYSQL_TRANSACTION_MANAGER)
  @Primary
  public DataSourceTransactionManager transactionManager(
      @Qualifier(QUALIFIER_MYSQL_DATA_SOURCE) DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }
}
