package com.email.verification.email.bot;

import com.email.verification.email.dto.EmailInfoDto;
import com.email.verification.email.services.EmailService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.FileNotFoundException;
import java.util.List;

@Component
public class EmailCheckerTelegramBot implements LongPollingSingleThreadUpdateConsumer {
    @Value("${welcome.message}")
    String welcomeMessage;
    @Value("${bot.token}")
    String token;
    TelegramClient telegramClient;
    private final EmailService emailService;

    public EmailCheckerTelegramBot() {
        emailService = new EmailService();
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            List<String> possibleEmails;
            EmailInfoDto emailInfoDto;

            switch (messageText) {
                case "/start":
                    sendMessage(chatId, welcomeMessage);
                    break;
                default: {
                    if (!emailService.validateEmail(messageText)) {
                        sendMessage(chatId, "Your email is not correct.Pleas try again.");
                        break;
                    }

                    possibleEmails = emailService.getPossibleEmails(messageText);
                    if (!possibleEmails.isEmpty()) {
                        possibleEmails.forEach(email -> sendMessage(chatId, "Maybe are you assume? " + email));
                    }

                    emailInfoDto = emailService.getInfoAboutEmail(messageText);
                    sendMessage(chatId, emailInfoDto.getEmail() + " exists.");
                    if (emailInfoDto.isExists() && emailInfoDto.isDisposable()) {
                        sendMessage(chatId, "WARNING: email is disposable!");
                    }
                    break;

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
