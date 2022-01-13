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
import ru.ortemb.contoratelegram.data.EventsType;
import ru.ortemb.contoratelegram.data.TextType;
import ru.ortemb.contoratelegram.data.entity.Event;
import ru.ortemb.contoratelegram.data.entity.SystemUser;
import ru.ortemb.contoratelegram.data.repository.EventRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyEventService {

  private final Random random = new Random();
  private final PhraseService quoteService;
  private final MessageService messageService;
  private final EventRepository eventRepository;

  public void sendPhrases(TextType textType, List<SystemUser> users) {
    try {
      messageService.sendMessage(users, String.format("4 - %s %s", quoteService.getRandomPhrase(textType), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      messageService.sendMessage(users, String.format("3 - %s %s", quoteService.getRandomPhrase(textType), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      messageService.sendMessage(users, String.format("2 - %s %s", quoteService.getRandomPhrase(textType), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      messageService.sendMessage(users, String.format("1 - %s %s", quoteService.getRandomPhrase(textType), getRandomEmoji(random.nextInt(3))), true);
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
  public SystemUser getRandomUserAndChangeStat(List<SystemUser> users, EventsType eventsType) {
    SystemUser systemUser = users.get(random.nextInt(users.size()));
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