package io.github.rscai.mockdb.dao;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;

public final class Schema {

  public static final ITableMetaData CATEGORY = new DefaultTableMetaData("TEST.CATEGORY",
      new Column[]{
          new Column("ID", DataType.NUMERIC),
          new Column("NAME", DataType.VARCHAR),
          new Column("PARENT_ID", DataType.NUMERIC)
      });
}
