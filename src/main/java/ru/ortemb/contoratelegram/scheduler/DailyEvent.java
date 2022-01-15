package ru.ortemb.contoratelegram.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ortemb.contoratelegram.data.EventsType;
import ru.ortemb.contoratelegram.data.TextType;
import ru.ortemb.contoratelegram.data.entity.SystemUser;
import ru.ortemb.contoratelegram.data.repository.UserRepository;
import ru.ortemb.contoratelegram.service.DailyEventService;
import ru.ortemb.contoratelegram.service.MessageService;
import ru.ortemb.contoratelegram.service.PhraseService;
import ru.ortemb.contoratelegram.service.UserService;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyEvent {

  private final UserService userService;
  private final PhraseService phraseService;
  private final UserRepository userRepository;
  private final MessageService messageService;
  private final DailyEventService dailyEventService;

  @Scheduled(cron = "0 0 11 * * *", zone = "Europe/Moscow")
//  @Scheduled(cron = "*/50 * * * * * ")
  public void rollEvent() throws InterruptedException {

    List<SystemUser> users = userRepository.findAll().stream().filter(user -> !user.isBlocked()).toList();
    LinkedList<SystemUser> selectedUsers = userService.getSomeRandomUsers(2);

    SystemUser authority = dailyEventService.changeStat(selectedUsers.poll(), EventsType.AUTHORITY);
    SystemUser footer = dailyEventService.changeStat(selectedUsers.poll(), EventsType.FOOTER);

    messageService.sendMessage(users, phraseService.getPhrase(TextType.FOOTER_ROLL_START), false);
    Thread.sleep(2000);
    dailyEventService.sendPhrases(users, TextType.FOOTER_ROLL);
    messageService.sendMessage(users,
        String.format("%s %s (@%s)", phraseService.getPhrase(TextType.FOOTER_ROLL_END), footer.getFirstName(), footer.getUserName()),
        true);
    Thread.sleep(5000);
    messageService.sendMessage(users, phraseService.getPhrase(TextType.AUTHORITY_ROLL_START), false);
    Thread.sleep(2000);
    dailyEventService.sendPhrases(users, TextType.AUTHORITY_ROLL);
    messageService.sendMessage(users,
        String.format("%s (@%s) %s", authority.getFirstName(), authority.getUserName(),
            phraseService.getPhrase(TextType.AUTHORITY_ROLL_END)),
        true);
  }

  @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Moscow")
//  @Scheduled(cron = "*/30 * * * * * ")
  public void quoteOfDay() {
    List<SystemUser> users = userRepository.findAll().stream().filter(user -> !user.isBlocked()).toList();

    messageService.sendMessage(users,
        String.format("%s\n%s", phraseService.getPhrase(TextType.QUOTE_OF_DAY),
            phraseService.getRandomPhrase(TextType.QUOTE)),
        false);
  }

}
