package eu.cryptoeuro.bankgateway.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import liquibase.integration.spring.SpringLiquibase;

/**
 * Spring configuration: database layer
 *
 * @author Erko Hansar
 */
@Configuration
@EnableTransactionManagement
public class DataConfig {

    @Value("${database.driver:org.postgresql.Driver}")
    private String databaseDriver;
    @Value("${database.url}")
    private String databaseUrl;
    @Value("${database.username}")
    private String databaseUsername;
    @Value("${database.password}")
    private String databasePassword;
    @Value("${database.schema}")
    private String schema;

    @Bean(name = "dataSource", destroyMethod = "close")
    public DataSource dataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setDriverClassName(databaseDriver);
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);
        dataSource.setInitialSize(0);
        dataSource.setMinIdle(0);
        dataSource.setMaxActive(10);
        dataSource.setMaxIdle(10);
        dataSource.setMaxWait(5000);
        dataSource.setMaxAge(3600000);
        dataSource.setLogAbandoned(true);
        dataSource.setSuspectTimeout(60);
        dataSource.setLogValidationErrors(true);
        dataSource.setFairQueue(true);
        dataSource.setTimeBetweenEvictionRunsMillis(10000);
        dataSource.setMinEvictableIdleTimeMillis(600000);
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(600000);
        dataSource.setAbandonWhenPercentageFull(75);
        dataSource.setJmxEnabled(false);
        dataSource.setValidationInterval(5000);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setDefaultTransactionIsolation(2);
        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource());
        liquibase.setChangeLog("classpath:liquibase/changelog.xml");
        // this works only with XML type changesets
        liquibase.setDefaultSchema(schema);
        // this is needed for SQL type changesets
        Map<String, String> changeLogParameters = new HashMap<>();
        changeLogParameters.put("schemaName", schema);
        liquibase.setChangeLogParameters(changeLogParameters);
        return liquibase;
    }

}
