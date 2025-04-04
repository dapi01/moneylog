package org.codenova.moneylog.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ExpenseJoin {
    private long id;
    private int userId;
    private LocalDate expenseDate;
    private String description;
    private long amount;
    private int categoryId;
    private String name;
}
