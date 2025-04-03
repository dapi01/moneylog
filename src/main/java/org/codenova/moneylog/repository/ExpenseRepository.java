package org.codenova.moneylog.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.codenova.moneylog.entity.Expense;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ExpenseRepository {
    public int save(Expense expense);

    public List<Expense> findByUserId(int userId);

    public List<Expense> findByUserAndDuration(@Param("userId") String userId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

}
