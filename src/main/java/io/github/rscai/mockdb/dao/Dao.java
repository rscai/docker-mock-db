package io.github.rscai.mockdb.dao;

import java.io.Serializable;
import java.util.Optional;

public interface Dao<T, ID extends Serializable> {

  Optional<T> findById(ID id);
}
