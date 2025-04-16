package tn.esprit.spring.userservice.dto.Request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordRequest {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}
