package io.github.rscai.mockdb.model;

import java.util.Collections;
import java.util.List;

public class Category {

  private long id;
  private String name;
  private List<Category> subCategories = Collections.EMPTY_LIST;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Category> getSubCategories() {
    return subCategories;
  }

  public void setSubCategories(List<Category> subCategories) {
    this.subCategories = subCategories;
  }
}
