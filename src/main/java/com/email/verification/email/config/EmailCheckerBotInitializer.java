package com.email.verification.email.config;

import com.email.verification.email.bot.EmailCheckerBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class EmailCheckerBotInitializer {
    @Value("${bot.token}")
    String token;
    @Autowired
    EmailCheckerBot bot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        try {
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(token, bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
