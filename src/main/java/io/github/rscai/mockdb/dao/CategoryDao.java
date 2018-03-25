package io.github.rscai.mockdb.dao;

import io.github.rscai.mockdb.model.Category;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class CategoryDao extends BaseDao implements Dao<Category, Long> {

  private static final String SQL_FIND_BY_ID = "SELECT * FROM DEV.CATEGORY START WITH ID = :id CONNECT BY PRIOR ID=PARENT_ID";

  @Override
  public Optional<Category> findById(Long id) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("id", id);
    List<ImmutableTriple<Long, String, Long>> list = namedParameterJdbcTemplate
        .query(SQL_FIND_BY_ID, params, new RowMapper<ImmutableTriple<Long, String, Long>>() {
          @Override
          public ImmutableTriple mapRow(ResultSet resultSet, int i) throws SQLException {
            Long id = resultSet.getLong("ID");
            String name = resultSet.getString("NAME");
            Long parentId = resultSet.getLong("PARENT_ID");
            if (resultSet.wasNull()) {
              parentId = 0L;
            }
            return new ImmutableTriple(id, name, parentId);
          }
        });
    if (list.isEmpty()) {
      return Optional.empty();
    }
    // find root
    Optional<Category> optionalRoot = findRoot(id, list);
    if (!optionalRoot.isPresent()) {
      return Optional.empty();
    }
    Category root = optionalRoot.get();
    root.setSubCategories(findSubCategories(root.getId(), list));

    return Optional.of(root);
  }

  private Optional<Category> findRoot(final Long id,
      final List<ImmutableTriple<Long, String, Long>> list) {
    for (ImmutableTriple<Long, String, Long> element : list) {
      if (element.left == id) {
        Category category = new Category();
        category.setId(element.left);
        category.setName(element.middle);
        return Optional.of(category);
      }
    }
    return Optional.empty();
  }

  private List<Category> findSubCategories(final long id,
      final List<ImmutableTriple<Long, String, Long>> list) {
    List<Category> subs = new ArrayList<>();
    for (ImmutableTriple<Long, String, Long> element : list) {
      if (element.right == id) {
        Category sub = new Category();
        sub.setId(element.left);
        sub.setName(element.middle);
        sub.setSubCategories(findSubCategories(sub.getId(), list));
        subs.add(sub);
      }
    }
    return subs;
  }
}
