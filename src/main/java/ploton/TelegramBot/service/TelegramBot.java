package ploton.TelegramBot.service;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ploton.TelegramBot.config.BotConfig;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    public TelegramBot(BotConfig botConfig) {
        super(botConfig.getBotToken());
        this.botConfig = botConfig;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && !update.getMessage().getText().isEmpty()) {
            long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();
            try {
                execute(new SendMessage(String.valueOf(chatId), "Hello"));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
}
