package ru.ortemb.contoratelegram.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ortemb.contoratelegram.data.EventType;
import ru.ortemb.contoratelegram.data.TextType;
import ru.ortemb.contoratelegram.data.entity.EventHistory;
import ru.ortemb.contoratelegram.data.entity.SystemUser;
import ru.ortemb.contoratelegram.data.repository.EventHistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventHistoryService {

  private final PhraseService phraseService;
  private final MessageService messageService;
  private final EventHistoryRepository eventHistoryRepository;

  @Transactional
  public SystemUser changeStat(SystemUser systemUser, EventType eventType) {
    eventHistoryRepository.findByUserIdAndEventType(systemUser.getId(), eventType)
        .ifPresentOrElse(
            eventHistory -> eventHistory.setCount(eventHistory.getCount() + 1),
            () -> eventHistoryRepository.save(EventHistory.builder()
                .user(systemUser)
                .eventType(eventType)
                .count(1)
                .build()));
    return systemUser;
  }

  public void getStatistics(String userId, EventType eventType) {
    StringBuilder result = new StringBuilder();
    result.append(phraseService.getPhrase(
        eventType.equals(EventType.AUTHORITY)
            ? TextType.AUTHORITY_ROLL_RESULTS
            : TextType.FOOTER_ROLL_RESULTS));

    AtomicInteger counter = new AtomicInteger(1);
    eventHistoryRepository.findFirst10ByEventTypeOrderByCountDesc(eventType)
        .forEach(eventHistory -> result.append(
            String.format("%s)  %s (@%s) - %s раз(а) \n",
                counter.getAndIncrement(),
                eventHistory.getUser().getFirstName(),
                eventHistory.getUser().getUserName(),
                eventHistory.getCount())));
    messageService.sendMessage(userId, result.toString(), true);
  }

}
