package com.email.verification.email.services;

import org.springframework.stereotype.Service;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Type;
import org.xbill.DNS.Record;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class DNSMXCheckerServes {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";


    public List<String> getMXRecords(String domain) {
        List<String> mxRecords = new ArrayList<>();
        try {
            Record[] records = new Lookup(domain, Type.MX).run();
            if (records != null) {
                for (Record record : records) {
                    if (record instanceof MXRecord) {
                        MXRecord mx = (MXRecord) record;
                        mxRecords.add(mx.getTarget().toString());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in search MX-records: " + e.getMessage());
        }

        if (!mxRecords.isEmpty()) {
            System.out.println("MX-records for domain " + domain + ":");
            mxRecords.forEach(System.out::println);
        } else {
            System.out.println("MX-records missing for domain: " + domain);
        }
        return mxRecords;
    }

    public  String checkEmailOnExists(String email) {
        String domain = email.substring(email.indexOf("@") + 1);
        List<String> mxRecords = getMXRecords(domain);
        for (String mxRecord : mxRecords) {
            try (Socket socket = new Socket(mxRecord, 25); // Подключение к порту 25
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 OutputStream writer = socket.getOutputStream()) {

                // Чтение приветственного сообщения сервера
                System.out.println("Server: " + reader.readLine());

                // Отправка команд HELO, MAIL FROM и RCPT TO
                sendCommand(writer, reader, "HELO " + domain);
                sendCommand(writer, reader, "MAIL FROM:<hunter11@gmail.com>"); // Используйте любой тестовый email
                String response = sendCommand(writer, reader, "RCPT TO:<" + email + ">");

                // Если сервер ответил "250", email существует
                if (response.startsWith("250")) {
                    return "Email " + email + " exist.";
                }
            } catch (Exception e) {
                System.err.println("Error when checking through " + mxRecord + ": " + e.getMessage());
            }
        }

        return "Email " + email + " not found.";
    }

    private String sendCommand(OutputStream writer, BufferedReader reader, String command) throws Exception {
        writer.write((command + "\r\n").getBytes());
        writer.flush();
        String response = reader.readLine();
        System.out.println("Command: " + command + " -> Answer: " + response);
        return response;
    }

    public boolean validateEmail(String email) {
        email = email.toLowerCase().trim();
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }
}
