package com.email.verification.email;

import com.email.verification.email.bot.EmailCheckerBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class EmailCheckerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailCheckerApplication.class, args);
	}
}
