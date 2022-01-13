package ru.ortemb.contoratelegram.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ortemb.contoratelegram.data.entity.SystemUser;
import ru.ortemb.contoratelegram.data.mapper.UserMapper;
import ru.ortemb.contoratelegram.data.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private  final MessageService messageService;

  @Transactional
  public void newUser(User telegramUser) {
    userRepository.findById(telegramUser.getId().toString())
        .ifPresentOrElse(user -> {
          user.setBlocked(false);
          log.info("User id: {} already exists", user.getId());
          messageService.sendMessage(List.of(user), String.format("Hi %s", user.getFirstName()),false);
        }, () -> {
          SystemUser newUser = userRepository.save(userMapper.telegramUserToEntity(telegramUser));
          log.info("New User {}, id: {} was added", newUser.getFirstName(), newUser.getId());
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

//  public SystemUser

}
