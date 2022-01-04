package ru.ortemb.contoratelegram.scheduler;

import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
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

    @Scheduled(cron = "0 0 11 * * *", zone = "Europe/Moscow")
//  @Scheduled(cron = "*/50 * * * * * ")
  public void event() {

    Random random = new Random();
    List<Phrases> listFooterPhrases = phrasesRepository.findAllByTextType(TextType.FOOTER_ROLL);
    List<Phrases> listAuthorityPhrases = phrasesRepository.findAllByTextType(TextType.AUTHORITY_ROLL);
    List<SystemUser> users = userRepository.findAll().stream().filter(user -> !user.isBlocked()).toList();

    SystemUser authority = users.get(random.nextInt(users.size()));
    SystemUser footer = users.get(random.nextInt(users.size()));


    dailyRollEventService.sendMessage(users, phrasesRepository.findAllByTextType(TextType.FOOTER_ROLL_START).get(0).getText());
    dailyRollEventService.sendPhrases(listFooterPhrases, random, users);
    dailyRollEventService.sendMessage(users,
        String.format("%s%s", phrasesRepository.findAllByTextType(TextType.FOOTER_ROLL_END).get(0).getText(), footer.getFirstName()));

    dailyRollEventService.sendMessage(users, phrasesRepository.findAllByTextType(TextType.AUTHORITY_ROLL_START).get(0).getText());
    dailyRollEventService.sendPhrases(listAuthorityPhrases, random, users);
    dailyRollEventService.sendMessage(users,
        String.format("%s%s", authority.getFirstName(), phrasesRepository.findAllByTextType(TextType.AUTHORITY_ROLL_END).get(0).getText()));

  }

}
