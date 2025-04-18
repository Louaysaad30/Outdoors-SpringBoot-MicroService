package tn.esprit.spring.userservice.dto.Request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@Builder
public class ChatMessageDTO {
    private Long id;
    private String content;
    private Long sender;
    private Long recipient;
    private Date timestamp;
}
