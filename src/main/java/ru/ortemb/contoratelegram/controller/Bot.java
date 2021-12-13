package ru.ortemb.contoratelegram.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ortemb.contoratelegram.data.entity.Citations;
import ru.ortemb.contoratelegram.data.entity.Users;
import ru.ortemb.contoratelegram.data.mapper.UsersMapper;
import ru.ortemb.contoratelegram.data.repository.CitationsRepository;
import ru.ortemb.contoratelegram.data.repository.UserRepository;

@Slf4j
@Controller
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

  private final CitationsRepository citationsRepository;
  private final UserRepository userRepository;
  private final UsersMapper usersMapper;
  @Value("${telegram.bot.credentials.token}")
  private String TOKEN;
  @Value("${telegram.bot.credentials.bot-username}")
  private String BOT_USERNAME;

  @Override
  public void onUpdateReceived(Update update) {
    if (Objects.nonNull(update.getMessage()) && update.getMessage().hasText()) {
      if (update.getMessage().getText().equals("/start")) {
        Optional<Users> usersOptional = userRepository.findById(update.getMessage().getFrom().getId().toString());
        if (usersOptional.isPresent()) {
          Users user = usersOptional.get();
          user.setBlocked(false);
          userRepository.save(user);
          log.info("User id: {} already exists", update.getMessage().getFrom().getId());
          return;
        }
        Users newUser = userRepository.save(usersMapper.telegramUserToEntity(update.getMessage().getFrom()));
        log.info("New User {}, id: {} has add", newUser.getFirstName(), newUser.getId());
        try {
          execute(new SendMessage(newUser.getId(), String.format("Hi %s", newUser.getFirstName())));
        } catch (TelegramApiException e) {
          e.printStackTrace();
        }
      }
    } else if (Objects.nonNull(update.getMyChatMember()) && update.getMyChatMember().getNewChatMember().getStatus().equals("kicked")) {
      Optional<Users> optionalUser = userRepository.findById(update.getMyChatMember().getChat().getId().toString());
      if (optionalUser.isPresent()) {
        Users user = optionalUser.get();
        user.setBlocked(true);
        userRepository.save(user);
        log.info("USER ID {} BLOCK YOU", user.getId());
      }
    }
    else if (Objects.nonNull(update.getMyChatMember()) && update.getMyChatMember().getNewChatMember().getStatus().equals("member")) {
      //todo логика когда добавили в чат (id, type, title)
    }
    else if (Objects.nonNull(update.getMyChatMember()) && update.getMyChatMember().getNewChatMember().getStatus().equals("left")) {
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

  @Scheduled(cron = "0 0 10 * * *", zone = "Europe/Moscow")
//  @Scheduled(cron = "*/10 * * * * *")
  private void send() {

    Random random = new Random();
    List<Citations> list = citationsRepository.findAll();

    userRepository.findAll().forEach(user -> {
      try {
        String text = list.get(random.nextInt(list.size())).getBody();
        execute(new SendMessage(user.getId(),
            String.format("Первая пуля вошла в руку, больно сука...\nВторая в щёку угодила, косой мудила!\n\nТРИ ДНЯ ДО КОРПОРОТИВА!\n\n%s", text)));
      } catch (TelegramApiException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), user.getUserName(), user.getId());
      }
    });
  }

  @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Moscow")
  private void send2() {

    Random random = new Random();
    List<Citations> list = citationsRepository.findAll();

    userRepository.findAll().forEach(user -> {
      try {
        String text = list.get(random.nextInt(list.size())).getBody();
        execute(new SendMessage(user.getId(),
            String.format("В Сб. чебурки наобед, а пока го в MarketPlace.\n\n%s", text)));
      } catch (TelegramApiException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), user.getUserName(), user.getId());
      }
    });
  }

//  @Transactional
//  @Scheduled(cron = "*/10 * * * * *")
//  void testSend() {
////        userRepository.findAll().stream().map(Users::getId).forEach(System.out::println);
//    SendMessage message = new SendMessage("85248441", "Hi!");
//    try {
//      execute(message);
//    } catch (TelegramApiException e) {
//      //todo get username
//      log.info("{}. USER ID: {}", e.getMessage(), message.getChatId());
//    }
//  }
}
