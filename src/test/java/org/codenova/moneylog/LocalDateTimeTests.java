package org.codenova.moneylog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDate;

@SpringBootTest
public class LocalDateTimeTests {

    @Test
    public void test01(){
        LocalDate today = LocalDate.now();

        DayOfWeek dow = today.getDayOfWeek();
        System.out.println(dow);
        int value = today.getDayOfWeek().getValue();
        System.out.println(value);
        System.out.println(today.plusDays(1).getDayOfWeek().getValue());

        System.out.println(today.minusDays(5).getDayOfWeek().getValue());

        LocalDate d = LocalDate.of(2025,3,18);
        LocalDate firstDayOfWeek = d.minusDays(d.getDayOfYear());


    }
}
