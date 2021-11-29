package ru.ortemb.contoratelegram.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ortemb.contoratelegram.data.entity.Users;
import ru.ortemb.contoratelegram.data.mapper.UsersMapper;
import ru.ortemb.contoratelegram.data.repository.UserRepository;

@Slf4j
@Controller
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

  private final UserRepository userRepository;
  private final UsersMapper usersMapper;
  @Value("${telegram.bot.credentials.token}")
  private String TOKEN;
  @Value("${telegram.bot.credentials.bot-username}")
  private String BOT_USERNAME;

  @SneakyThrows
  @Override
  public void onUpdateReceived(Update update) {

    if (update.getMessage().hasText()) {
      if (update.getMessage().getText().equals("/start")) {
        Users newUser = userRepository.save(usersMapper.telegramUserToEntity(update.getMessage().getFrom()));
        execute(new SendMessage(newUser.getId(), String.format("Hi %s", newUser.getFirstName())));
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

  @SneakyThrows
  @Scheduled(cron = "0 0 11 * * *", zone = "Europe/Moscow")
  private void send() {
    for (Users users : userRepository.findAll()) {
      SendMessage message = new SendMessage(users.getId(), "Have a good day!");
      execute(message);
    }
  }

//    @Scheduled(cron = "*/10 * * * * *")
//    private void testSend() throws TelegramApiException {
//        userRepository.findAll().stream().map(Users::getId).forEach(System.out::println);
//        SendMessage message = new SendMessage("85248441", "Hi!");
//        execute(message);
//    }
}
