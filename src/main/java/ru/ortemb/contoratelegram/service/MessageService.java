package ru.ortemb.contoratelegram.service;

import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ortemb.contoratelegram.data.entity.SystemUser;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class MessageService {

  private final TelegramLongPollingBot bot;
  private final List<String> dice = List.of("🎲", "🎯", "🏀", "⚽", "🎰", "🎳");

  public void sendMessage(List<SystemUser> users, String text, boolean disableNotification) {
    users.forEach(user -> {
      try {
        bot.execute(SendMessage.builder()
            .chatId(user.getId())
            .text(text)
            .disableNotification(disableNotification)
            .build());
      } catch (TelegramApiException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), user.getUserName(), user.getId());
      }
    });
  }

  public void sendDice(List<SystemUser> users) {
    users.forEach(user -> {
      try {
        bot.execute(SendDice.builder()
            .disableNotification(true)
            .chatId(user.getId())
            .emoji(dice.get(new Random().nextInt(dice.size())))
            .build());
      } catch (TelegramApiException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), user.getUserName(), user.getId());
      }
    });
  }

}
