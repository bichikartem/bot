package ru.ortemb.contoratelegram.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ortemb.contoratelegram.service.DailyEventService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyEvent {

  private final DailyEventService dailyEventService;

  @Scheduled(cron = "0 0 11 * * *", zone = "Europe/Moscow")
//  @Scheduled(cron = "*/50 * * * * * ")
  public void rollEvent() throws InterruptedException {
    dailyEventService.startRollEvent();
  }

  @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Moscow")
//  @Scheduled(cron = "*/30 * * * * * ")
  public void quoteOfDay() {
    dailyEventService.startQuoteOfDay();
  }

}
