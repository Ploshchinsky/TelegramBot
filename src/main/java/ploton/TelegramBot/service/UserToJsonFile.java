package ploton.TelegramBot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger LOGGER = LogManager.getLogger(UserToJsonFile.class);
    private static String jsonPath = "src/main/jsonRepository/users.json";

    public static void save(User user) {
        int id;
        String json = "";
        Path path = Path.of(jsonPath);
        //get id
        try {
            id = Files.readAllLines(path).size();
            user.setId(id);
        } catch (IOException e) {
            LOGGER.error("Error (UserToJsonFile) - ID INIT: " + e.getMessage());
        }
        //get json string from object
        try {
            json = new ObjectMapper().writeValueAsString(user) + "\n";
        } catch (JsonProcessingException e) {
            LOGGER.error("Error (UserToJsonFile) - GET JSON STRING FROM USER OBJECT: " + e.getMessage());
        }
        //write json string to file
        try {
            Files.write(
                    path,
                    json.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("Error (UserJsonToFile) - WRITE JSON STRING TO FILE: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void saveAll(List<User> list) {
        list.forEach(UserToJsonFile::save);
    }

    public static User get(int id) {
        StringBuilder allUsersJson = new StringBuilder();
        String jsonUser = null;
        User user = null;
        //get all lines from file
        try {
            Files.readAllLines(Path.of(jsonPath))
                    .forEach(s -> {
                        allUsersJson.append(s).append("\n");
                    });
        } catch (IOException e) {
            LOGGER.error("Error (UserToJsonFile) - GET ALL LINES FROM FILE: " + e.getMessage());
        }
        //finding user by id
        try {
            for (String s : allUsersJson.toString().split("\n")) {
                if (s.contains("\"id\":" + id)) {
                    jsonUser = s;
                }
            }
            user = jsonUser != null ? new ObjectMapper().readValue(jsonUser, User.class) : null;
        } catch (JsonProcessingException e) {
            LOGGER.error("Error (UserToJsonFile) - FINDING USER BY ID: " + e.getMessage());
        }
        return user;
    }

    public static List<User> getAll() {
        List<String> jsonStrings = new ArrayList<>();
        List<User> usersList = new ArrayList<>();
        //get all lines from file
        try {
            jsonStrings = Files.readAllLines(Path.of(jsonPath));
        } catch (IOException e) {
            LOGGER.error("Error (UseToJsonFile) - GET ALL LINES FROM FILE: " + e.getMessage());
        }
        //deserialize json strings to User objects
        for (String s : jsonStrings) {
            try {
                usersList.add(new ObjectMapper().readValue(s, User.class));
            } catch (JsonProcessingException e) {
                LOGGER.error("Error (UseToJsonFile) - DESERIALIZATION JSON LINES TO USER OBJECTS: " + e.getMessage());
            }
        }
        return usersList;
    }

}
