package com.email.verification.email.bot;

import com.email.verification.email.services.DNSMXCheckerService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

@Component
public class EmailCheckerBot implements LongPollingSingleThreadUpdateConsumer {
    @Value("${welcome.message}")
    String welcomeMessage;
    @Value("${bot.token}")
    String token;
    TelegramClient telegramClient;
    @Autowired
    DNSMXCheckerService dnsmxCheckerService;

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            List<String> possibleEmails;

            switch (messageText) {
                case "/start":
                    sendMessage(chatId, welcomeMessage);
                    break;
                default: {
                    if (!dnsmxCheckerService.validateEmail(messageText)) {
                        sendMessage(chatId, "Your email is not correct.Pleas try again.");
                        break;
                    }
                    try {
                        possibleEmails = getPossibleEmails(messageText);
                        if (!possibleEmails.isEmpty()) {
                            possibleEmails.forEach(email -> sendMessage(chatId, "Maybe are you assume? " + email));
                            if (update.hasMessage() && update.getMessage().hasText() && possibleEmails.contains(update.getMessage().getText())) {
                                String existsEmail = dnsmxCheckerService.checkEmailOnExists(messageText);
                                sendMessage(chatId, existsEmail);
                                break;
                            }
                        }


                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    String existsEmail = dnsmxCheckerService.checkEmailOnExists(messageText);
                    sendMessage(chatId, existsEmail);
                }
            }
        }
    }

    private List<String> getPossibleEmails(String emailInput) throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:must_popular_email_domains");

        try (Stream<String> streamOfDomains = Files.lines(file.toPath())) {
            List<String> domains = streamOfDomains.toList();
            String extractDomain = dnsmxCheckerService.extractDomain(emailInput);
            String extractName = dnsmxCheckerService.extractName(emailInput);
            if (domains.contains(extractDomain)) {
                return List.of();
            }

            return domains.stream()
                    .filter(emailDomain -> dnsmxCheckerService.rateDifferentDomainNames(extractDomain, emailDomain) == 1)
                    .map(emailDomain -> extractName + "@" + emailDomain)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
