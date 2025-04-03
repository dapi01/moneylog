package org.codenova.moneylog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class MoneylogApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void compareLocalDateTime(){
        LocalDateTime t1 = LocalDateTime.of(2025, 1, 21, 1,30);
        LocalDateTime t2 = LocalDateTime.of(2025, 1, 20, 13,30);
        LocalDateTime t3 = LocalDateTime.of(2025, 1, 20, 1,30);
    }
}
