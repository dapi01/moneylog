package org.codenova.moneylog.query;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyExpense {
    private LocalDate expenseDate;
    private long total;


}
