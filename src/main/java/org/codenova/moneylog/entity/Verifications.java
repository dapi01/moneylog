package org.codenova.moneylog.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Verifications {
    private int id;
    private String token;
    private String userEmail;
    private LocalDateTime expiresAt;
}
