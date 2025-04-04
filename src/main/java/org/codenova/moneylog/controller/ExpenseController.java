package org.codenova.moneylog.controller;

import lombok.AllArgsConstructor;
import org.codenova.moneylog.entity.Category;
import org.codenova.moneylog.entity.Expense;
import org.codenova.moneylog.entity.User;
import org.codenova.moneylog.repository.CategoryRepository;
import org.codenova.moneylog.repository.ExpenseRepository;
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
    public String historyHandle(@SessionAttribute("user") User user, Model model) {
        model.addAttribute("sort", categoryRepository.findAll());
        model.addAttribute("now", LocalDate.now());

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
