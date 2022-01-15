package ru.ortemb.contoratelegram.service;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ortemb.contoratelegram.data.EventsType;
import ru.ortemb.contoratelegram.data.TextType;
import ru.ortemb.contoratelegram.data.entity.Event;
import ru.ortemb.contoratelegram.data.entity.SystemUser;
import ru.ortemb.contoratelegram.data.repository.EventRepository;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyEventService {

  private final Random random = new Random();
  private final PhraseService phraseService;
  private final MessageService messageService;
  private final EventRepository eventRepository;

  public void sendPhrases(List<SystemUser> users, TextType textType) {
    LinkedList<String> phrases = phraseService.getSomeRandomPhrases(textType, 4);
    try {
      messageService.sendMessage(users, String.format("4 - %s %s", phrases.poll(), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      messageService.sendMessage(users, String.format("3 - %s %s", phrases.poll(), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      messageService.sendMessage(users, String.format("2 - %s %s", phrases.poll(), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      messageService.sendMessage(users, String.format("1 - %s %s", phrases.poll(), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      messageService.sendMessage(users, "Испытываем фортуну " + getRandomEmoji(random.nextInt(5)), true);
      Thread.sleep(600);
      messageService.sendDice(users);
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Transactional
  public SystemUser changeStat(SystemUser systemUser, EventsType eventsType) {
    eventRepository.findByUserIdAndEventsType(systemUser.getId(), eventsType)
        .ifPresentOrElse(
          event -> event.setCount(event.getCount() + 1),
          () -> eventRepository.save(Event.builder()
              .user(systemUser)
              .eventsType(eventsType)
              .count(1)
              .build()));
    return systemUser;
  }

  public String getRandomEmoji(int amt) {
    int amount = amt > 0 ? amt : 1;
    ArrayList<Emoji> all = (ArrayList<Emoji>) EmojiManager.getAll();
    return Stream.generate(() -> (int) (Math.random() * all.size())).limit(amount)
        .map(i -> all.get(i).getUnicode())
        .reduce("", (a, b) -> a + b);
  }
}
