package ploton.TelegramBot.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ploton.TelegramBot.config.BotConfig;
import ploton.TelegramBot.model.User;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LogManager.getLogger(TelegramBot.class);

    @Value("${document.path}")
    private String DOC_PATH;
    private final BotConfig botConfig;
    private boolean isStart = false;
    private static int verificiationStage = 0;
    private User tempUser;

    public TelegramBot(BotConfig botConfig) {
        super(botConfig.getBotToken());
        this.botConfig = botConfig;
        menuInit();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && !update.getMessage().getText().isEmpty()) {
            long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();
            String nickName = update.getMessage().getChat().getUserName();

            if (!isStart) {
                switch (message) {
                    case "/start":
                        isStart = true;
                        sendMessage(chatId, "Hi " + nickName + " ! In order to get the " +
                                "document you need to make a verification. \n");
                        tempUser = new User();
                        break;
                    case "/help":
                        sendMessage(chatId, "This bot helps you get a " +
                                "document with useful information! \n" +
                                "\n" +
                                "To get your document type /start or select the " +
                                "corresponding item in the bot menu. \n" +
                                "\n" +
                                "Thank you!");
                        break;
                    default:
                        sendMessage(chatId, "There is no such command");
                }
            }
            if (!userVerification(update)) {
                return;
            }
            sendChatAction(chatId, ActionType.UPLOADDOCUMENT);
            sendDocument(chatId, Path.of(DOC_PATH));
            UserToJsonFile.save(tempUser);
            tempUser = new User();
            isStart = false;
        }
    }

    private boolean userVerification(Update update) {
        long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();

        switch (verificiationStage) {
            case 0:
                tempUser.setNickName(update.getMessage().getChat().getUserName());
                sendMessage(chatId, "Enter your name (example - Alex Smith):");
                verificiationStage++;
                break;
            case 1:
                if (message.matches("^[a-zA-Z]+ [a-zA-Z]+$")) {
                    tempUser.setFirstName(message.split(" ")[0]);
                    tempUser.setLastName(message.split(" ")[1]);
                    sendMessage(chatId, "Your age (example - 21):");
                    verificiationStage++;
                } else {
                    sendMessage(chatId, "Incorrect. Please enter your name (example - Alex Smith):");
                    break;
                }
                break;
            case 2:
                if (message.matches("^\\d{1,2}$")) {
                    tempUser.setAge(Integer.valueOf(message));
                    sendMessage(chatId, "Email (example - mail@gmail.com):");
                    verificiationStage++;
                } else {
                    sendMessage(chatId, "Incorrect. Please enter your age (example - 21):");
                    break;
                }
                break;
            case 3:
                if (message.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}$")) {
                    tempUser.setEmail(message);
                    sendMessage(chatId, "Phone number (example 89991231212 or +79991231212):");
                    verificiationStage++;
                } else {
                    sendMessage(chatId, "Incorrect. Please enter your email (example - mail@gmail.com):");
                    break;
                }
                break;
            case 4:
                if (message.matches("^(\\+)?\\d{11}$")) {
                    tempUser.setPhoneNumber(message);
                    sendMessage(chatId, "Thanks for your time!");
                } else {
                    sendMessage(chatId, "Incorrect. Please enter your phone (example - 89991231212 or +79991231212):");
                    break;
                }
            default:
                sendMessage(chatId, "The file download has started, please wait for it");
                tempUser.setLastRequest(new Date());
                return true;
        }
        return false;
    }


    public void sendMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error("Error (TelegramBot) - SEND MESSAGE: " + e.getMessage());
        }
    }

    public void sendDocument(long chatId, Path documentPath) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(new InputFile(new File(documentPath.toString())));
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            LOGGER.error("Error (TelegramBot) - SEND DOCUMENT: " + e.getMessage());
        }
    }

    public void sendChatAction(long chatId, ActionType type) {
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setChatId(chatId);
        sendChatAction.setAction(type);
        try {
            execute(sendChatAction);
        } catch (TelegramApiException e) {
            LOGGER.error("Error (TelegramBot) - SEND CHAT ACTION: " + e.getMessage());
        }
    }

    public void menuInit() {
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "Start working with the bot"));
        commandList.add(new BotCommand("/help", "Get help on bot operation and its features"));
        try {
            execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            LOGGER.error("Error (TelegramBot) - MENU INIT: " + e.getMessage());
        }
    }


    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
}
