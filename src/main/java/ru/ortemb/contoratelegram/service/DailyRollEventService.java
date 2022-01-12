package ru.ortemb.contoratelegram.service;


import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ortemb.contoratelegram.data.EventsType;
import ru.ortemb.contoratelegram.data.entity.Events;
import ru.ortemb.contoratelegram.data.entity.Phrases;
import ru.ortemb.contoratelegram.data.entity.SystemUser;
import ru.ortemb.contoratelegram.data.repository.EventsRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyRollEventService {

  private final TelegramLongPollingBot bot;
  private final Random random = new Random();
  private final EventsRepository eventsRepository;
  private final List<String> dice = List.of("🎲", "🎯", "🏀", "⚽", "🎰", "🎳");

  public void sendPhrases(List<Phrases> listPhrases, Random random, List<SystemUser> users) {
    try {
      sendMessage(users, String.format("4 - %s %s", listPhrases.get(0).getText(), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      sendMessage(users, String.format("3 - %s %s", listPhrases.get(1).getText(), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      sendMessage(users, String.format("2 - %s %s", listPhrases.get(2).getText(), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      sendMessage(users, String.format("1 - %s %s", listPhrases.get(3).getText(), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      sendMessage(users, "Испытываем фортуну " + getRandomEmoji(random.nextInt(5)), true);
      Thread.sleep(600);
      sendDice(users);
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Transactional
  public SystemUser getRandomUserAndChangeStat(List<SystemUser> users, EventsType eventsType) {
    SystemUser systemUser = users.get(random.nextInt(users.size()));
    eventsRepository.findByUserIdAndEventsType(systemUser.getId(), eventsType)
        .ifPresentOrElse(
            event -> event.setCount(event.getCount() + 1),
            () -> eventsRepository.save(Events.builder()
                .user(systemUser)
                .eventsType(eventsType)
                .count(1)
                .build())
        );
    return systemUser;
  }

  public void sendMessage(List<SystemUser> users, String text, boolean disableNotification) {
    users.forEach(user -> {
      try {
        bot.execute(SendMessage.builder()
            .chatId(user.getId())
            .text(text)
            .disableNotification(disableNotification)
            .build()
        );
      } catch (TelegramApiException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), user.getUserName(), user.getId());
      }
    });
  }

  public void sendDice(List<SystemUser> users) {
    users.forEach(user -> {
      try {
        bot.execute(SendDice.builder().disableNotification(true).chatId(user.getId()).emoji(dice.get(new Random().nextInt(dice.size()))).build());
      } catch (TelegramApiException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), user.getUserName(), user.getId());
      }
    });
  }

  public String getRandomEmoji(int amt) {
    int amount = amt > 0 ? amt : 1;
    ArrayList<Emoji> all = (ArrayList<Emoji>) EmojiManager.getAll();
    return Stream.generate(() -> (int) (Math.random() * all.size())).limit(amount).map(i -> all.get(i).getUnicode()).reduce((a, b) -> a + b).get();
  }
}
