package ru.ortemb.contoratelegram.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ortemb.contoratelegram.data.EventsType;
import ru.ortemb.contoratelegram.data.entity.EventHistory;
import ru.ortemb.contoratelegram.data.entity.SystemUser;
import ru.ortemb.contoratelegram.data.repository.EventHistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventHistoryService {

  private final EventHistoryRepository eventHistoryRepository;
  private final MessageService messageService;

  @Transactional
  public SystemUser changeStat(SystemUser systemUser, EventsType eventsType) {
    eventHistoryRepository.findByUserIdAndEventsType(systemUser.getId(), eventsType)
        .ifPresentOrElse(
            eventHistory -> eventHistory.setCount(eventHistory.getCount() + 1),
            () -> eventHistoryRepository.save(EventHistory.builder()
                .user(systemUser)
                .eventsType(eventsType)
                .count(1)
                .build()));
    return systemUser;
  }

  public void getStat(String userId, EventsType eventsType, String firstRow) {
    List<EventHistory> eventHistories = eventHistoryRepository.findFirst10ByEventsTypeOrderByCountDesc(eventsType);
    StringBuilder result = new StringBuilder();
    result.append(firstRow);

    for (int i = 0; i < eventHistories.size(); i++) {
      EventHistory ev = eventHistories.get(i);
      result.append(String.format("%s)  %s (@%s) - %s раз(а) \n", i + 1, ev.getUser().getFirstName(), ev.getUser().getUserName(), ev.getCount()));
    }

    messageService.sendMessage(userId, result.toString(), true);
  }

}
