package tn.esprit.spring.userservice.dto.Response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
    private int sessions;
    private int total_sessions;
    private int n_days_after_onboarding;
    private int total_navigations_fav1;
    private int activity_days;
}
