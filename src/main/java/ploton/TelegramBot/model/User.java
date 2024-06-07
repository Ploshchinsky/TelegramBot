package ploton.TelegramBot.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
public class User {
    private int id;
    private String nickName;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private String phoneNumber;
    private Date lastRequest;
}
