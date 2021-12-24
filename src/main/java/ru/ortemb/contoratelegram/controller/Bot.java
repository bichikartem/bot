package ru.ortemb.contoratelegram.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ortemb.contoratelegram.data.TextType;
import ru.ortemb.contoratelegram.data.entity.Phrases;
import ru.ortemb.contoratelegram.data.entity.SystemUser;
import ru.ortemb.contoratelegram.data.mapper.UsersMapper;
import ru.ortemb.contoratelegram.data.repository.PhrasesRepository;
import ru.ortemb.contoratelegram.data.repository.UserRepository;

@Slf4j
@Controller
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

  private final PhrasesRepository phrasesRepository;
  private final UserRepository userRepository;
  private final UsersMapper usersMapper;
  @Value("${telegram.bot.credentials.token}")
  private String TOKEN;
  @Value("${telegram.bot.credentials.bot-username}")
  private String BOT_USERNAME;

  @Override
  @Transactional
  public void onUpdateReceived(Update update) {
    if (Objects.nonNull(update.getMessage()) && update.getMessage().hasText()) {
      if (update.getMessage().getText().equals("/start")) {
        Optional<SystemUser> usersOptional = userRepository.findById(update.getMessage().getFrom().getId().toString());
        if (usersOptional.isPresent()) {
          SystemUser user = usersOptional.get();
          user.setBlocked(false);
          userRepository.save(user);
          log.info("User id: {} already exists", update.getMessage().getFrom().getId());
          return;
        }
        SystemUser newUser = userRepository.save(usersMapper.telegramUserToEntity(update.getMessage().getFrom()));
        log.info("New User {}, id: {} has add", newUser.getFirstName(), newUser.getId());
        try {
          execute(new SendMessage(newUser.getId(), String.format("Hi %s", newUser.getFirstName())));
        } catch (TelegramApiException e) {
          e.printStackTrace();
        }
      }
    } else if (Objects.nonNull(update.getMyChatMember()) && update.getMyChatMember().getNewChatMember().getStatus().equals("kicked")) {
//      Optional<Users> optionalUser = userRepository.findById(update.getMyChatMember().getChat().getId().toString());

      userRepository.findById(update.getMyChatMember().getChat().getId().toString())
          .ifPresent(user -> user.setBlocked(true));

//      if (optionalUser.isPresent()) {
//        Users user = optionalUser.get();
//        user.setBlocked(true);
//        userRepository.save(user);
//        log.info("USER ID {} BLOCK YOU", user.getId());
//      }
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

  @Scheduled(cron = "0 0 11 * * *", zone = "Europe/Moscow")
//  @Scheduled(cron = "*/50 * * * * *")
  public void game() {

    Random random = new Random();
    List<Phrases> listFooterPhrases = phrasesRepository.findAllByTextType(TextType.FOOTER_ROLL);
    List<Phrases> listAuthorityPhrases = phrasesRepository.findAllByTextType(TextType.AUTHORITY_ROLL);
    List<SystemUser> users = userRepository.findAll();

    SystemUser authority = users.get(random.nextInt(users.size()));
    SystemUser footer = users.get(random.nextInt(users.size()));

    users.forEach(user -> {
      try {
        execute(new SendMessage(user.getId(), phrasesRepository.findAllByTextType(TextType.FOOTER_ROLL_START).get(0).getText()));
      } catch (TelegramApiException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), user.getUserName(), user.getId());
      }
    });

    sendPhrases(listFooterPhrases, random, users);
    users.forEach(user -> {
      try {
        execute(new SendMessage(user.getId(),
            String.format(phrasesRepository.findAllByTextType(TextType.FOOTER_ROLL_END).get(0).getText() + "%s", footer.getFirstName())));
        Thread.sleep(10000);
      } catch (TelegramApiException | InterruptedException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), user.getUserName(), user.getId());
      }
    });

    users.forEach(user -> {
      try {
        execute(new SendMessage(user.getId(), phrasesRepository.findAllByTextType(TextType.AUTHORITY_ROLL_START).get(0).getText()));
      } catch (TelegramApiException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), user.getUserName(), user.getId());
      }
    });

    sendPhrases(listAuthorityPhrases, random, users);
    users.forEach(user -> {
      try {
        execute(new SendMessage(user.getId(),
            String.format(authority.getFirstName() + "%s", phrasesRepository.findAllByTextType(TextType.AUTHORITY_ROLL_END).get(0).getText())));

      } catch (TelegramApiException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), user.getUserName(), user.getId());
      }
    });

  }

  private void sendPhrases(List<Phrases> listPhrases,Random random, List<SystemUser> users) {
    try {
      sendPhrase(listPhrases, random.nextInt(listPhrases.size()), users);
      Thread.sleep(1000);
      sendPhrase(listPhrases, random.nextInt(listPhrases.size()), users);
      Thread.sleep(1000);
      sendPhrase(listPhrases, random.nextInt(listPhrases.size()), users);
      Thread.sleep(1000);
      sendPhrase(listPhrases, random.nextInt(listPhrases.size()), users);
      Thread.sleep(1000);
      sendPhrase(listPhrases, random.nextInt(listPhrases.size()), users);
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void sendPhrase(List<Phrases> listPhrases, int number, List<SystemUser> users) {
    users.stream().forEach(user -> {
      try {
          String text = listPhrases.get(number).getText();
          execute(new SendMessage(user.getId(), text));
      } catch (TelegramApiException e) {
        log.info("{}. USER {}, ID: {}", e.getMessage(), user.getUserName(), user.getId());
      }
    });
  }

}
