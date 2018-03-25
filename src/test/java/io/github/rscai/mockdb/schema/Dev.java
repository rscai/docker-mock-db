package io.github.rscai.mockdb.schema;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;

public class Dev {

  public static final String SCHEMA = "DEV";
  public static final ITableMetaData CATEGORY = new DefaultTableMetaData(
      String.format("%s.CATEGORY", SCHEMA), new Column[]{
      new Column("ID", DataType.NUMERIC),
      new Column("NAME", DataType.VARCHAR),
      new Column("PARENT_ID", DataType.NUMERIC)
  });
}
