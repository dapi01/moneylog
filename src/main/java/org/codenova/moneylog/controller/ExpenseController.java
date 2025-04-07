package org.codenova.moneylog.controller;

import lombok.AllArgsConstructor;
import org.codenova.moneylog.entity.Category;
import org.codenova.moneylog.entity.Expense;
import org.codenova.moneylog.entity.User;
import org.codenova.moneylog.query.DailyExpense;
import org.codenova.moneylog.repository.CategoryRepository;
import org.codenova.moneylog.repository.ExpenseRepository;
import org.codenova.moneylog.request.SearchPeriodRequest;
import org.codenova.moneylog.vo.ExpenseJoin;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.sax.SAXResult;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@AllArgsConstructor
@RequestMapping("/expense")
public class ExpenseController {

    private ExpenseRepository expenseRepository;
    private CategoryRepository categoryRepository;

    @GetMapping("/history")
    public String historyHandle(@SessionAttribute("user") User user,
                                @ModelAttribute SearchPeriodRequest searchPeriodRequest,
                                Model model) {

            LocalDate startDate;
            LocalDate endDate;
            if(searchPeriodRequest.getStartDate() != null && searchPeriodRequest.getEndDate() != null) {
                startDate = searchPeriodRequest.getStartDate();
                endDate = searchPeriodRequest.getEndDate();
            } else {
                LocalDate today = LocalDate.now();
                startDate = today.minusDays(today.getDayOfMonth()-1);
                endDate= startDate.plusMonths(1).minusDays(1);
            }


        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        model.addAttribute("sort", categoryRepository.findAll());
        model.addAttribute("now", LocalDate.now());

        //model.addAttribute("expenses",expenseRepository.findByUserIdAndDuration(user.getId()));
        model.addAttribute("expenses", expenseRepository.findByUserIdAndDuration(user.getId(), startDate, endDate));
        List <ExpenseJoin> save = expenseRepository.expenseJoin();

        model.addAttribute("save", save);


        return "expense/history";
    }

    @PostMapping("/history")
    public String expenseHandle(@ModelAttribute Expense expense, @SessionAttribute("user") User user, Model model) {

        expense.setUserId(user.getId());
        int r = expenseRepository.save(expense);

        return "redirect:/expense/history";
    }

    @GetMapping("/report")
    public String reportHandle(@SessionAttribute("user") User user, Model model) {

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(today.getDayOfMonth()-1);
        LocalDate endDate= startDate.plusMonths(1).minusDays(1);

        model.addAttribute("categoryExpense",
                expenseRepository.getCategory(user.getId(), startDate, endDate)
        );

        List<DailyExpense> list =
                expenseRepository.getDailyExpense(user.getId(), startDate, endDate);
        Map<LocalDate, DailyExpense> dateMap = new HashMap<>();

        for(DailyExpense expense : list) {
            dateMap.put(expense.getExpenseDate(), expense);
        }
        List<DailyExpense> fullList = new ArrayList<>();
        for(int i = 0; startDate.plusDays(i).isBefore(endDate) || startDate.plusDays(i).isEqual(endDate); i++ ) {
            LocalDate d = startDate.plusDays(i);
            if(dateMap.get(d) != null) {
                fullList.add(dateMap.get(d));
            }else {
                fullList.add(DailyExpense.builder().expenseDate(d).total(0).build());
            }
        }
        model.addAttribute("dailyExpense", fullList);


        return "expense/report";
    }



}