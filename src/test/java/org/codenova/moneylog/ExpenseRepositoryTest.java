package org.codenova.moneylog;

import org.codenova.moneylog.query.DailyExpense;
import org.codenova.moneylog.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ExpenseRepositoryTest {

    @Autowired
    ExpenseRepository expenseRepository;

    @Test
    public void dailyExpenseTest() {
        List<DailyExpense> list = expenseRepository.getDailyExpense(1,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30)
        );
        for (DailyExpense expense : list) {
            System.out.println(expense.getExpenseDate() + " / " + expense.getTotal());
        }
        System.out.println("==========================================================");
        list.add(4,
                DailyExpense.builder().expenseDate(LocalDate.of(2025, 4, 5)).total(0).build());
        for (DailyExpense expense : list) {
            System.out.println(expense.getExpenseDate() + " / " + expense.getTotal());
        }
    }
    @Test
    public void test2() {
        LocalDate from = LocalDate.of(2025, 4, 1);
        LocalDate to = LocalDate.of(2025, 4, 30);

        // 반복문을 이용해서 이 사이의 모든 날짜 (LocalDate)를 출력
        List<LocalDate> dates = new ArrayList<>();
        for(int i = 0; from.plusDays(i).isBefore(to) || from.plusDays(i).isEqual(to); i++ ) {
            dates.add(from.plusDays(i));
        }

        List<DailyExpense> expenses = new ArrayList<>();
        for(LocalDate date : dates) {
            expenses.add(
                    DailyExpense.builder().expenseDate(date).total(0).build()
            );
        }
        // ===================================================================================
        for(DailyExpense one : expenses) {
            System.out.println(one);
        }
    }

    @Test
    public void test3() {
        LocalDate from = LocalDate.of(2025, 4, 1);
        LocalDate to = LocalDate.of(2025, 4, 14);

        List<DailyExpense> list = expenseRepository.getDailyExpense(10, from, to);
        Map<LocalDate, DailyExpense> listMap = new HashMap<>();
        for(DailyExpense expense : list) {
            listMap.put(expense.getExpenseDate(), expense);
            System.out.println(expense);
        }
        System.out.println(listMap);

        List<DailyExpense> fullList = new ArrayList<>();
        for(int i = 0; from.plusDays(i).isBefore(to) || from.plusDays(i).isEqual(to); i++ ) {
            LocalDate d = from.plusDays(i);
            if(listMap.get(d) != null){
                fullList.add(listMap.get(d));
            }else {
                fullList.add(DailyExpense.builder().expenseDate(d).total(0).build());
            }

        }
        System.out.println("======================================================");
        for(DailyExpense expense : fullList) {
            System.out.println(expense);
        }



    }
}
