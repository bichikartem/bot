package ru.ortemb.contoratelegram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ortemb.contoratelegram.data.entity.SystemUser;
import ru.ortemb.contoratelegram.data.mapper.UsersMapper;
import ru.ortemb.contoratelegram.data.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UsersMapper usersMapper;
  private final UserRepository userRepository;

  @Transactional
  public void newUser(User telegramUser) {
    userRepository.findById(telegramUser.getId().toString())
        .ifPresentOrElse(user -> {
          user.setBlocked(false);
          log.info("User id: {} already exists", user.getId());
        }, () -> {
          SystemUser newUser = userRepository.save(usersMapper.telegramUserToEntity(telegramUser));
          log.info("New User {}, id: {} added", newUser.getFirstName(), newUser.getId());
        });
  }

  @Transactional
  public void userBlocked(Update update) {
    userRepository.findById(update.getMyChatMember().getChat().getId().toString())
        .ifPresent(user -> {
          user.setBlocked(true);
          log.info("USER ID {} BLOCK YOU", user.getId());
        });
  }

}
