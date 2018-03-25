package io.github.rscai.mockdb.dao;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class BaseDao {

  protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public void setNamedParameterJdbcTemplate(
      NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }
}
