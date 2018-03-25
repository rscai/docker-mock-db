package io.github.rscai.mockdb.dao;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class DaoConfig {

  @Autowired
  private DataSource dataSource;

  @Bean
  public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
    return new NamedParameterJdbcTemplate(dataSource);
  }

  @Bean
  public CategoryDao categoryDao() {
    CategoryDao dao = new CategoryDao();
    dao.setNamedParameterJdbcTemplate(namedParameterJdbcTemplate());

    return dao;
  }
}
