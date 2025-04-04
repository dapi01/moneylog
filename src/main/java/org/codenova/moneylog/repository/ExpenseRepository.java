package org.codenova.moneylog.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.codenova.moneylog.entity.Expense;
import org.codenova.moneylog.query.CategoryExpense;
import org.codenova.moneylog.vo.ExpenseJoin;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ExpenseRepository {
    public int save(Expense expense);

    public List<Expense> findByUserId(int userId);

    public List<Expense> findByUserIdAndDuration(@Param("userId") int userId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    public List<ExpenseJoin> expenseJoin();

    public int selectWeeklyExpense(@Param("userId") int userId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
    public List<Expense> selectWeeklyExpenseTop3(@Param("userId") int userId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    public List<CategoryExpense> topSpendingCategory(@Param("userId") int userId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

}
