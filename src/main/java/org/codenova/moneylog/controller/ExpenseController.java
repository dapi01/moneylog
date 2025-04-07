package org.codenova.moneylog.controller;

import lombok.AllArgsConstructor;
import org.codenova.moneylog.entity.Category;
import org.codenova.moneylog.entity.Expense;
import org.codenova.moneylog.entity.User;
import org.codenova.moneylog.repository.CategoryRepository;
import org.codenova.moneylog.repository.ExpenseRepository;
import org.codenova.moneylog.request.SearchPeriodRequest;
import org.codenova.moneylog.vo.ExpenseJoin;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.sax.SAXResult;
import java.time.LocalDate;
import java.util.List;


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


}
