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
import ru.ortemb.contoratelegram.data.entity.EventHistory;
import ru.ortemb.contoratelegram.data.repository.EventHistoryRepository;
import ru.ortemb.contoratelegram.service.EventHistoryService;
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

      if (update.getMessage().getText().equals("/start")) {
        userService.createUser(update.getMessage().getFrom());
      }

      if (update.getMessage().getText().equals("/footer")) {
        String userId = update.getMessage().getFrom().getId().toString();
        eventHistoryService.getStat(userId, EventsType.FOOTER, phraseService.getRandomPhrase(TextType.FOOTER_ROLL_RESULTS));
      }

      if (update.getMessage().getText().equals("/authority")) {
        String userId = update.getMessage().getFrom().getId().toString();
        eventHistoryService.getStat(userId, EventsType.AUTHORITY, phraseService.getRandomPhrase(TextType.AUTHORITY_ROLL_RESULTS));
      }

      if (update.getMessage().getText().equals("/quote")) {
        String userId = update.getMessage().getFrom().getId().toString();
        messageService.sendMessage(userId, phraseService.getRandomPhrase(TextType.QUOTE), false);
      }

    } else if (Objects.nonNull(update.getMyChatMember()) && update.getMyChatMember().getNewChatMember().getStatus().equals("kicked")) {
      userService.userBlocked(update.getMyChatMember().getChat().getId().toString());
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


}
