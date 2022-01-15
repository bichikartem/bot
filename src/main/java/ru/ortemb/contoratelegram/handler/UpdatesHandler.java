package ru.ortemb.contoratelegram.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ortemb.contoratelegram.data.EventsType;
import ru.ortemb.contoratelegram.data.TextType;
import ru.ortemb.contoratelegram.data.entity.Event;
import ru.ortemb.contoratelegram.data.repository.EventRepository;
import ru.ortemb.contoratelegram.service.MessageService;
import ru.ortemb.contoratelegram.service.PhraseService;
import ru.ortemb.contoratelegram.service.UserService;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdatesHandler extends TelegramLongPollingBot {

  private final UserService userService;
  private final EventRepository eventRepository;
  private final PhraseService phraseService;
  private final MessageService messageService;

  @Value("${telegram.bot.credentials.token}")
  private String TOKEN;
  @Value("${telegram.bot.credentials.bot-username}")
  private String BOT_USERNAME;

  @Override
  public void onUpdateReceived(Update update) {
    if (Objects.nonNull(update.getMessage()) && update.getMessage().hasText()) {

      if (update.getMessage().getText().equals("/start")) {
        userService.newUser(update.getMessage().getFrom());
      }

      if (update.getMessage().getText().equals("/fufel")) {
        getStat(update, EventsType.FOOTER, phraseService.getRandomPhrase(TextType.FOOTER_ROLL_RESULTS));
      }

      if (update.getMessage().getText().equals("/authority")) {
        getStat(update, EventsType.AUTHORITY, phraseService.getRandomPhrase(TextType.AUTHORITY_ROLL_RESULTS));
      }

      if (update.getMessage().getText().equals("/quote")) {
        String userId = update.getMessage().getFrom().getId().toString();
        messageService.sendMessage(userId, phraseService.getRandomPhrase(TextType.QUOTE), false);
      }

    } else if (Objects.nonNull(update.getMyChatMember()) && update.getMyChatMember().getNewChatMember().getStatus().equals("kicked")) {
      userService.userBlocked(update);
    } else if (Objects.nonNull(update.getMyChatMember()) && update.getMyChatMember().getNewChatMember().getStatus().equals("member")) {
      //todo логика когда добавили в чат (id, type, title)
    } else if (Objects.nonNull(update.getMyChatMember()) && update.getMyChatMember().getNewChatMember().getStatus().equals("left")) {
      //todo логика когда удалили из чата
    }

  }

  @Override
  public String getBotUsername() {
    return BOT_USERNAME;
  }

  @Override
  public String getBotToken() {
    return TOKEN;
  }

  private void getStat(Update update, EventsType eventsType, String firstRow) {

    User telegramUser = update.getMessage().getFrom();
    List<Event> events = eventRepository.findFirst10ByEventsTypeOrderByCountDesc(eventsType);
    StringBuilder result = new StringBuilder();
    result.append(firstRow);

    for (int i = 0; i < events.size(); i++) {
      Event ev = events.get(i);
      result.append(String.format("%s)  %s (@%s) - %s раз(а) \n", i + 1, ev.getUser().getFirstName(), ev.getUser().getUserName(), ev.getCount()));
    }
    try {
      execute(new SendMessage(telegramUser.getId().toString(), result.toString()));
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }
}
