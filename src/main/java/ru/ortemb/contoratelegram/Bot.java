package ru.ortemb.contoratelegram;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ortemb.contoratelegram.data.repository.UserRepository;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;

    @Value("${telegram.bot.credentials.token}")
    private String TOKEN;
    @Value("${telegram.bot.credentials.bot-username}")
    private String BOT_USERNAME;

    private Map<String, String> mapUsers = new HashMap<>();
    {
        mapUsers.put("85248441", "Danya");
    }

    @Override
    public void onUpdateReceived(Update update) {

        System.out.println("---------------------------------------------------------");
        System.out.println(update.getMessage().getChatId().toString());
        System.out.println("---------------------------------------------------------");

        if (update.getMessage().hasText()) {
            if (update.getMessage().getText().equals("/start")) {
                try {
//                    log.debug(update.getMessage().getChatId().toString());
                    execute(new SendMessage(update.getMessage().getChatId().toString(), "Hi there! What is your name?"));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (update.getMessage().hasText()) {
                    mapUsers.put(update.getMessage().getChatId().toString(), update.getMessage().getText());
            }
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Scheduled(cron = "0 0 8 * * *")
    private void send() {
        for (String id : mapUsers.keySet()){
            try {
                SendMessage message = new SendMessage(id, "Hi " + mapUsers.get(id) + "! \nHave a good day!");
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

//    @Scheduled(cron = "*/10 * * * * *")
//    private void testSend() throws TelegramApiException {
//        System.out.println(userRepository.findAll());
//                SendMessage message = new SendMessage("85248441", "Hi!");
//                execute(message);
//    }

}
