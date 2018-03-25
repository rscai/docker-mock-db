package io.github.rscai.mockdb.dao;

import javax.sql.DataSource;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.operation.CompositeOperation;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class OracleDBTestBase extends DataSourceBasedDBTestCase {

  @Autowired
  private DataSource dataSource;

  @Override
  public DataSource getDataSource() {
    return dataSource;
  }

  @Override
  protected void setUpDatabaseConfig(DatabaseConfig config) {
    super.setUpDatabaseConfig(config);
    config.setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
  }

  @Override
  protected DatabaseOperation getSetUpOperation() throws Exception {
    return new CompositeOperation(DatabaseOperation.TRUNCATE_TABLE, DatabaseOperation.INSERT);
  }

  @Override
  protected DatabaseOperation getTearDownOperation() throws Exception {
    return DatabaseOperation.NONE;
  }
}
