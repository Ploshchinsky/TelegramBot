package ploton.TelegramBot.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ploton.TelegramBot.model.User;

import java.util.Date;
import java.util.List;

public class UserToJsonFileTest {

    User user1, user2;

    @Test
    @Disabled
    public void createAndReadTest() {
        user1 = new User();
        user1.setId(0);
        user1.setNickName("Avatar");
        user1.setFirstName("Ivan");
        user1.setLastName("Petrov");
        user1.setAge(21);
        user1.setEmail("mail@gmail.com");
        user1.setPhoneNumber("+79110897645");
        user1.setLastRequest(new Date());

        user2 = new User();
        user2.setId(1);
        user2.setNickName("Pony");
        user2.setFirstName("Ploshchinskii");
        user2.setAge(28);
        user2.setLastName("Antont");
        user2.setEmail("made_of_fire@mail.ru");
        user2.setPhoneNumber("89220755231");
        user2.setLastRequest(new Date());

        UserToJsonFile.save(user1);
        UserToJsonFile.save(user2);

        User actual = UserToJsonFile.get(user2.getId());
        Assertions.assertEquals(user2, actual);
    }

    @Test
    @Disabled
    public void readAllTest() {
        user1 = new User();
        user1.setId(0);
        user1.setNickName("Avatar");
        user1.setFirstName("Ivan");
        user1.setLastName("Petrov");
        user1.setAge(21);
        user1.setEmail("mail@gmail.com");
        user1.setPhoneNumber("+79110897645");
        user1.setLastRequest(new Date());

        user2 = new User();
        user2.setId(1);
        user2.setNickName("Pony");
        user2.setFirstName("Ploshchinskii");
        user2.setAge(28);
        user2.setLastName("Antont");
        user2.setEmail("made_of_fire@mail.ru");
        user2.setPhoneNumber("89220755231");
        user2.setLastRequest(new Date());

        UserToJsonFile.save(user1);
        UserToJsonFile.save(user2);

        List<User> expected = List.of(user1, user2);
        List<User> actual = UserToJsonFile.getAll();
        Assertions.assertEquals(expected,actual);
    }
}
