/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.vutura.p21.configs;

import io.vutura.p21.util.DataSourceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.TimeZone;

/**
 * @author Ahmad R. Djarkasih
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfigs {

    private static final String H2_MEM_URL = "jdbc:h2:mem:biblio.db;DB_CLOSE_ON_EXIT=FALSE";
    private static final String POSTGRES_URL_TEMPLATE = "jdbc:postgresql://%s:%s/%s?stringtype=unspecified";
    private final String JDBC_URL_VAR = "JDBC_URL";
    private final String JDBC_HOST_VAR = "JDBC_HOST";
    private final String JDBC_PORT_VAR = "JDBC_PORT";
    private final String JDBC_DATABASE_VAR = "JDBC_DATABASE";
    private final String JDBC_USER_VAR = "JDBC_USER";
    private final String JDBC_PASSWORD_VAR = "JDBC_PASSWORD";
    Logger logger = LoggerFactory.getLogger(AppConfigs.class);
    private String apiName;
    private String apiVersion;

    @Autowired
    private Environment env;

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getRunMode() {

        return env.getProperty("app.run.mode", "ERROR MODE");

    }

//    @Bean
//    public DataSource getDefaultDataSource() {
//
//        DataSource ds = null;
//
//        String jdbcUrl = H2_MEM_URL;
//        String jdbcUser = "biblioAdm";
//        String jdbcPassword = "b1bl10K3y";
//
//        try {
//
//            logger.info(String.format("Getting DataSource from %s with user for %s", jdbcUrl, jdbcUser));
//            ds = DataSourceHelper.build(jdbcUrl, jdbcUser, jdbcPassword);
//
//        } catch (SQLException ex) {
//
//        }
//
//        return ds;
//
//    }

    @Bean
    @Profile("dev")
    public DataSource getDevDataSource() {

        DataSource ds = null;
        String jdbcUser = env.getProperty(JDBC_USER_VAR);
        if (jdbcUser == null) {
            jdbcUser = env.getProperty("jdbc.user");
        }
        String jdbcPassword = env.getProperty(JDBC_PASSWORD_VAR);
        if (jdbcPassword == null) {
            jdbcPassword = env.getProperty("jdbc.password");
        }

        String jdbcUrl = env.getProperty(JDBC_URL_VAR);
        if (jdbcUrl == null) {
            jdbcUrl = env.getProperty("jdbc.url");
        }

        try {

            logger.info(String.format("Getting DataSource from %s with user for %s", jdbcUrl, jdbcUser));
            ds = DataSourceHelper.build(jdbcUrl, jdbcUser, jdbcPassword);

        } catch (SQLException ex) {
            System.out.println(ex);

        }

        return ds;

    }

    @Bean
    @Profile("prod")
    public DataSource getProdDataSource() {

        DataSource ds = null;

        String jdbcHost = env.getProperty(JDBC_HOST_VAR);
        if (jdbcHost == null) {
            jdbcHost = env.getProperty("jdbc.host");
        }
        String jdbcPort = env.getProperty(JDBC_PORT_VAR);
        if (jdbcPort == null) {
            jdbcPort = env.getProperty("jdbc.port");
        }
        String jdbcDatabase = env.getProperty(JDBC_DATABASE_VAR);
        if (jdbcDatabase == null) {
            jdbcDatabase = env.getProperty("jdbc.database");
        }

        String jdbcUrl = String.format(POSTGRES_URL_TEMPLATE, jdbcHost, jdbcPort, jdbcDatabase);

        String jdbcUser = env.getProperty(JDBC_USER_VAR);
        if (jdbcUser == null) {
            jdbcUser = env.getProperty("jdbc.user");
        }
        String jdbcPassword = env.getProperty(JDBC_PASSWORD_VAR);
        if (jdbcPassword == null) {
            jdbcPassword = env.getProperty("jdbc.password");
        }

        try {

            logger.info(String.format("Getting DataSource from %s with credential for %s", jdbcUrl, jdbcUser));
            ds = DataSourceHelper.build(jdbcUrl, jdbcUser, jdbcPassword);

        } catch (SQLException ex) {

            logger.info(String.format("getDataSource error = %s", ex.getClass().getCanonicalName()));

        }

        return ds;

    }
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
    }
}
