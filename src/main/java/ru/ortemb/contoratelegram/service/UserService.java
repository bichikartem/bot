package ru.ortemb.contoratelegram.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ortemb.contoratelegram.data.entity.SystemUser;
import ru.ortemb.contoratelegram.data.mapper.UserMapper;
import ru.ortemb.contoratelegram.data.repository.SystemUserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserMapper userMapper;
  private  final MessageService messageService;
  private final SystemUserRepository systemUserRepository;

  @Transactional
  public void createUser(User telegramUser) {
    systemUserRepository.findById(telegramUser.getId().toString())
        .ifPresentOrElse(user -> {
          user.setBlocked(false);
          log.info("User id: {} already exists", user.getId());
          messageService.sendMessage(user.getId(), String.format("Hi %s", user.getFirstName()),false);
        }, () -> {
          SystemUser newUser = systemUserRepository.save(userMapper.telegramUserToEntity(telegramUser));
          log.info("New User {}, id: {} was added", newUser.getFirstName(), newUser.getId());
        });
  }

  @Transactional
  public void userBlocked(String userId) {
    systemUserRepository.findById(userId)
        .ifPresent(user -> {
          user.setBlocked(true);
          log.info("USER ID {} BLOCK YOU", user.getId());
        });
  }

  public LinkedList<SystemUser> getSomeRandomUsers(long amount) {
    List<SystemUser> users = systemUserRepository.findAllByIsBlocked(false);
    if (users.size() < amount){
      throw new RuntimeException("The amount of users is less than necessary");
    } else {
      Collections.shuffle(users);
      return users.stream()
              .limit(amount)
              .collect(Collectors.toCollection(LinkedList::new));
    }
  }

  public List<SystemUser> getAllActiveUsers() {
    return systemUserRepository.findAllByIsBlocked(false);
  }

}
