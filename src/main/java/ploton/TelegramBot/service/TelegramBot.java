package ploton.TelegramBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ploton.TelegramBot.config.BotConfig;

public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    public TelegramBot(BotConfig botConfig) {
        super(botConfig.getBotToken());
        this.botConfig = botConfig;
    }

    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }
}
