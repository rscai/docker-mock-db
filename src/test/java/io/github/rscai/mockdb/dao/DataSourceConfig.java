package io.github.rscai.mockdb.dao;

import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class DataSourceConfig {


  @Value("${docker.host:http://ubuntu-vb:4243}")
  private String dockerHost;

  @Value("${docker.database.host:ubuntu-vb}")
  private String databaseHost;

  @Value("${docker.image.oracle:sath89/oracle-xe-11g}")
  private String image;

  @Value("${docker.database.url.template:jdbc:oracle:thin:@%s:%d:xe}")
  private String urlTemplate;

  @Value("${docker.database.username:system}")
  private String databaseUsername;

  @Value("${docker.database.password:oracle}")
  private String databasePassword;

  @Value("${docker.database.driverClass:oracle.jdbc.driver.OracleDriver}")
  private String driverClass;

  /*
  @Bean
  public DataSource dataSource() {
    DockerizedDataSource dataSource = new DockerizedDataSource();
    dataSource.setDockerHost(dockerHost);
    dataSource.setDatabaseHost(databaseHost);
    dataSource.setImage(image);
    dataSource.setUrlTemplate(urlTemplate);
    dataSource.setDatabaseUsername(databaseUsername);
    dataSource.setDatabasePassword(databasePassword);
    dataSource.setDriverClass(driverClass);

    return dataSource;
  }
  */

  @Bean
  public DataSource dataSource() {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setUrl(System.getProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL));
    dataSource.setUsername(System.getProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME));
    dataSource.setPassword(System.getProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD));
    dataSource.setDriverClassName(
        System.getProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS));

    return dataSource;
  }
}
