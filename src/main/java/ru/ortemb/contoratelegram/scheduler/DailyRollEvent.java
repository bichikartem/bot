package ru.ortemb.contoratelegram.scheduler;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ortemb.contoratelegram.data.EventsType;
import ru.ortemb.contoratelegram.data.TextType;
import ru.ortemb.contoratelegram.data.entity.Events;
import ru.ortemb.contoratelegram.data.entity.Phrases;
import ru.ortemb.contoratelegram.data.entity.SystemUser;
import ru.ortemb.contoratelegram.data.repository.EventsRepository;
import ru.ortemb.contoratelegram.data.repository.PhrasesRepository;
import ru.ortemb.contoratelegram.data.repository.UserRepository;
import ru.ortemb.contoratelegram.service.DailyRollEventService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyRollEvent {

  private final UserRepository userRepository;
  private final PhrasesRepository phrasesRepository;
  private final DailyRollEventService dailyRollEventService;
  private final EventsRepository eventsRepository;
  private final Random random = new Random();


  @Scheduled(cron = "0 0 11 * * *", zone = "Europe/Moscow")
//  @Scheduled(cron = "*/50 * * * * * ")
  public void event() throws InterruptedException {

    List<Phrases> listFooterPhrases = phrasesRepository.findAllByTextType(TextType.FOOTER_ROLL);
    List<Phrases> listAuthorityPhrases = phrasesRepository.findAllByTextType(TextType.AUTHORITY_ROLL);
    List<SystemUser> users = userRepository.findAll().stream().filter(user -> !user.isBlocked()).toList();

    SystemUser authority = getRandomUserAndChangeStat(users, EventsType.AUTHORITY);
    SystemUser footer = getRandomUserAndChangeStat(users, EventsType.FOOTER);

    dailyRollEventService.sendMessage(users, phrasesRepository.findAllByTextType(TextType.FOOTER_ROLL_START).get(0).getText());
    dailyRollEventService.sendPhrases(listFooterPhrases, random, users);
    dailyRollEventService.sendMessage(users,
        String.format("%s %s (@%s)", phrasesRepository.findAllByTextType(TextType.FOOTER_ROLL_END).get(0).getText(), footer.getFirstName(),
            footer.getUserName()));
    Thread.sleep(5000);
    dailyRollEventService.sendMessage(users, phrasesRepository.findAllByTextType(TextType.AUTHORITY_ROLL_START).get(0).getText());
    dailyRollEventService.sendPhrases(listAuthorityPhrases, random, users);
    dailyRollEventService.sendMessage(users,
        String.format("%s (@%s) %s", authority.getFirstName(), authority.getUserName(),
            phrasesRepository.findAllByTextType(TextType.AUTHORITY_ROLL_END).get(0).getText()));
  }

  private SystemUser getRandomUserAndChangeStat(List<SystemUser> users, EventsType eventsType) {
    SystemUser systemUser = users.get(random.nextInt(users.size()));
    Optional<Events> authorityEvents = eventsRepository.findByUserIdAndEventsType(systemUser.getId(), eventsType.name());
    authorityEvents.ifPresentOrElse(events -> {
          events.setCount(events.getCount() + 1);
          eventsRepository.save(events);
        },
        () -> eventsRepository.save(Events.builder().user(systemUser).eventsType(eventsType.name()).count(1).build()));
    return systemUser;
  }
}
