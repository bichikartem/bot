package ru.ortemb.contoratelegram.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ortemb.contoratelegram.data.EventType;
import ru.ortemb.contoratelegram.data.TextType;
import ru.ortemb.contoratelegram.service.EventHistoryService;
import ru.ortemb.contoratelegram.service.MessageService;
import ru.ortemb.contoratelegram.service.PhraseService;
import ru.ortemb.contoratelegram.service.UserService;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdatesHandler extends TelegramLongPollingBot {

  private final UserService userService;
  private final EventHistoryService eventHistoryService;
  private final PhraseService phraseService;
  private final MessageService messageService;

  @Value("${telegram.bot.credentials.token}")
  private String TOKEN;
  @Value("${telegram.bot.credentials.bot-username}")
  private String BOT_USERNAME;

  @Override
  public void onUpdateReceived(Update update) {

    if (Objects.nonNull(update.getMessage()) && update.getMessage().hasText()) {
      String userId = update.getMessage().getFrom().getId().toString();

      switch (update.getMessage().getText()) {
        case "/start" -> userService.createUser(update.getMessage().getFrom());
        case "/footer" -> eventHistoryService.getStatistics(userId, EventType.FOOTER);
        case "/authority" -> eventHistoryService.getStatistics(userId, EventType.AUTHORITY);
        case "/quote" -> messageService.sendMessage(userId, phraseService.getRandomPhrase(TextType.QUOTE), false);
      }
    }

    if (Objects.nonNull(update.getMyChatMember())) {
      switch (update.getMyChatMember().getNewChatMember().getStatus()) {
        case "kicked" -> userService.userBlocked(update.getMyChatMember().getChat().getId().toString());
//        case "member" -> //todo логика когда добавили в чат (id, type, title)
//        case "left" -> //todo логика когда удалили из чата
      }
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

}
