package ploton.TelegramBot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ploton.TelegramBot.model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserToJsonFile {
    private static String jsonPath = "src/main/jsonRepository/users.json";

    public static void save(User user) {
        int id;
        String json;
        Path path = Path.of(jsonPath);
        //get id
        try {
            id = Files.readAllLines(path).size();
            user.setId(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //get json string from object
        try {
            json = new ObjectMapper().writeValueAsString(user) + "\n";
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //write json string to file
        try {
            Files.write(
                    path,
                    json.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveAll(List<User> list) {
        list.forEach(UserToJsonFile::save);
    }

    public static User get(int id) {
        StringBuilder allUsersJson = new StringBuilder();
        String jsonUser = null;
        User user;
        try {
            Files.readAllLines(Path.of(jsonPath))
                    .forEach(s -> {
                        allUsersJson.append(s).append("\n");
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            for (String s : allUsersJson.toString().split("\n")) {
                if (s.contains("\"id\":" + id)) {
                    jsonUser = s;
                }
            }
            user = jsonUser != null ? new ObjectMapper().readValue(jsonUser, User.class) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public static List<User> getAll() {
        List<String> jsonStrings = new ArrayList<>();
        List<User> usersList = new ArrayList<>();
        try {
            jsonStrings = Files.readAllLines(Path.of(jsonPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String s : jsonStrings) {
            try {
                usersList.add(new ObjectMapper().readValue(s, User.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return usersList;
    }

}
