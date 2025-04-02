package org.codenova.moneylog.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Getter
public class FindPasswordRequest {
    @Email
    private String email;
}
