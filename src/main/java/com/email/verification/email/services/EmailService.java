package com.email.verification.email.services;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Type;
import org.xbill.DNS.Record;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class EmailService {
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

    public String getInfoAboutEmail(String email) {
        String domain = email.substring(email.indexOf("@") + 1);
        List<String> mxRecords = getMXRecords(domain);
        for (String mxRecord : mxRecords) {
            try (Socket socket = new Socket(mxRecord, 25);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 OutputStream writer = socket.getOutputStream()) {

                System.out.println("Server: " + reader.readLine());

                sendCommand(writer, reader, "HELO " + domain);
                sendCommand(writer, reader, "MAIL FROM:<hunter11@gmail.com>");
                String response = sendCommand(writer, reader, "RCPT TO:<" + email + ">");

                if (response.startsWith("250")) {
                    return "Email " + email + " exists.";
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

    public int rateDifferentDomainNames(String domainOfEmailInput, String correctDomainOfEmail) {
        int len1 = domainOfEmailInput.length();
        int len2 = correctDomainOfEmail.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            for (int j = 0; j <= len2; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = getMin(domainOfEmailInput, correctDomainOfEmail, dp, i, j);
                }
            }
        }

        return dp[len1][len2];
    }

    public List<String> getPossibleEmails(String emailInput) throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:must_popular_email_domains");

        try (Stream<String> streamOfDomains = Files.lines(file.toPath())) {
            List<String> domains = streamOfDomains.toList();
            String extractDomain = extractDomain(emailInput);
            String extractName = extractName(emailInput);
            if (domains.contains(extractDomain)) {
                return List.of();
            }

            return domains.stream()
                    .filter(emailDomain -> rateDifferentDomainNames(extractDomain, emailDomain) == 1)
                    .map(emailDomain -> extractName + "@" + emailDomain)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isDisposable(String emailInput) throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:disposable_email.txt");

        try (Stream<String> streamOfDisposableDomains = Files.lines(file.toPath())) {
            List<String> disposableDomainsList = streamOfDisposableDomains.toList();
            String extractDomain = extractDomain(emailInput);

            return disposableDomainsList.stream().anyMatch(it -> it.equals(extractDomain));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String extractDomain(@NotNull String email) {
        return StringUtils.substringAfter(email, "@");
    }

    public String extractName(@NotNull String email) {
        return StringUtils.substringBefore(email, "@");
    }

    private static int getMin(String domainOfEmailInput, String correctDomainOfEmail, int[][] dp, int i, int j) {
        return Math.min(dp[i - 1][j - 1] +
                        (domainOfEmailInput.charAt(i - 1) == correctDomainOfEmail.charAt(j - 1) ? 0 : 1),
                Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
    }
}
