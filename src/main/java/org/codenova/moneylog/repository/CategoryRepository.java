package org.codenova.moneylog.repository;

import org.apache.ibatis.annotations.Mapper;
import org.codenova.moneylog.entity.Category;

import java.util.List;

@Mapper
public interface CategoryRepository {
     List<Category> findAll();
}
