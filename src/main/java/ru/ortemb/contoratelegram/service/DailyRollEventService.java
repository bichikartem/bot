package ru.ortemb.contoratelegram.service;


import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ortemb.contoratelegram.data.entity.Phrases;
import ru.ortemb.contoratelegram.data.entity.SystemUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyRollEventService {

  private final TelegramLongPollingBot bot;

  public void sendPhrases(List<Phrases> listPhrases, Random random, List<SystemUser> users) {
    try {
      sendMessage(users, listPhrases.get(random.nextInt(listPhrases.size())).getText());
      Thread.sleep(1000);
      sendMessage(users, listPhrases.get(random.nextInt(listPhrases.size())).getText());
      Thread.sleep(1000);
      sendMessage(users, listPhrases.get(random.nextInt(listPhrases.size())).getText());
      Thread.sleep(1000);
      sendMessage(users, listPhrases.get(random.nextInt(listPhrases.size())).getText());
      Thread.sleep(1000);
      sendMessage(users, listPhrases.get(random.nextInt(listPhrases.size())).getText());
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void sendMessage(List<SystemUser> users, String text) {
    users.forEach(user -> {
      try {
        bot.execute(new SendMessage(user.getId(), text));
      } catch (TelegramApiException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), user.getUserName(), user.getId());
      }
    });
  }

}
