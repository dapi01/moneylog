package org.codenova.moneylog.entity;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Verifications {
    private int id;
    private String token;
    private String userEmail;
    private LocalDateTime expiresAt;
}
