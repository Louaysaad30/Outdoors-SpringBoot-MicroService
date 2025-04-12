package tn.esprit.spring.userservice.Enum;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account"),
    RESET_PASSWORD("RESET_PASSWORD");

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
