package ru.ortemb.contoratelegram.scheduler;

import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ortemb.contoratelegram.data.EventsType;
import ru.ortemb.contoratelegram.data.TextType;
import ru.ortemb.contoratelegram.data.entity.Phrases;
import ru.ortemb.contoratelegram.data.entity.SystemUser;
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
  private final Random random = new Random();


  @Scheduled(cron = "0 0 11 * * *", zone = "Europe/Moscow")
//  @Scheduled(cron = "*/30 * * * * * ")
  public void event() throws InterruptedException {

    List<Phrases> listFooterPhrases = phrasesRepository.findAllByTextType(TextType.FOOTER_ROLL);
    List<Phrases> listAuthorityPhrases = phrasesRepository.findAllByTextType(TextType.AUTHORITY_ROLL);
    List<SystemUser> users = userRepository.findAll().stream().filter(user -> !user.isBlocked()).toList();

    SystemUser authority = dailyRollEventService.getRandomUserAndChangeStat(users, EventsType.AUTHORITY);
    SystemUser footer = dailyRollEventService.getRandomUserAndChangeStat(users, EventsType.FOOTER);

    dailyRollEventService.sendMessage(users, phrasesRepository.findAllByTextType(TextType.FOOTER_ROLL_START).get(0).getText(), false);
    Thread.sleep(2000);
    dailyRollEventService.sendPhrases(listFooterPhrases, random, users);
    dailyRollEventService.sendMessage(users,
        String.format("%s %s (@%s)", phrasesRepository.findAllByTextType(TextType.FOOTER_ROLL_END).get(0).getText(), footer.getFirstName(),
            footer.getUserName()),
        true);
    Thread.sleep(5000);
    dailyRollEventService.sendMessage(users, phrasesRepository.findAllByTextType(TextType.AUTHORITY_ROLL_START).get(0).getText(), false);
    Thread.sleep(2000);
    dailyRollEventService.sendPhrases(listAuthorityPhrases, random, users);
    dailyRollEventService.sendMessage(users,
        String.format("%s (@%s) %s", authority.getFirstName(), authority.getUserName(),
            phrasesRepository.findAllByTextType(TextType.AUTHORITY_ROLL_END).get(0).getText()),
        true);
  }

  @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Moscow")
//  @Scheduled(cron = "*/30 * * * * * ")
  public void QuoteOfDay() {

    List<SystemUser> users = userRepository.findAll().stream().filter(user -> !user.isBlocked()).toList();
    List<Phrases> listQuotes = phrasesRepository.findAllByTextType(TextType.QUOTE);

    dailyRollEventService.sendMessage(users,
        String.format("%s\n%s", phrasesRepository.findAllByTextType(TextType.QUOTE_OF_DAY).get(0).getText(),
            listQuotes.get(random.nextInt(listQuotes.size()))),
        false);

  }

}
