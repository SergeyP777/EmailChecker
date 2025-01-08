package com.email.verification.email.bot;

import com.email.verification.email.services.DNSMXCheckerServes;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class EmailCheckerBot implements LongPollingSingleThreadUpdateConsumer {
    @Value("${welcome.message}")
    String welcomeMessage;
    @Value("${bot.token}")
    String token;
    TelegramClient telegramClient;
    @Autowired
    DNSMXCheckerServes dnsmxCheckerServes;

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    sendMessage(chatId, welcomeMessage);
                    break;
                default: {
                    if (!dnsmxCheckerServes.validateEmail(messageText)) {
                        sendMessage(chatId, "Your email is not correct.Pleas try again.");
                        break;
                    }
                    String resultOfCheck = dnsmxCheckerServes.checkEmailOnExists(messageText);
                    sendMessage(chatId, resultOfCheck);
                }
            }
        }
    }

    private void sendMessage(long chatId, String textMessage) {
        SendMessage message = new SendMessage(String.valueOf(chatId), textMessage);

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    private void initClient() {
        telegramClient = new OkHttpTelegramClient(token);
    }
}
