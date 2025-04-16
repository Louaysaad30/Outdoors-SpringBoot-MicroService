package tn.esprit.spring.userservice.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomError {
    private int status;
    private String error;


}
