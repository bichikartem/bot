package ru.ortemb.contoratelegram.service;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ortemb.contoratelegram.data.EventsType;
import ru.ortemb.contoratelegram.data.TextType;
import ru.ortemb.contoratelegram.data.entity.SystemUser;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyEventService {

  private final UserService userService;
  private final PhraseService phraseService;
  private final Random random = new Random();
  private final MessageService messageService;
  private final EventHistoryService eventHistoryService;

  public void startRollEvent() throws InterruptedException {
    List<SystemUser> users = userService.getAllActiveUsers();
    LinkedList<SystemUser> selectedUsers = userService.getSomeRandomUsers(2);

    SystemUser authority = eventHistoryService.changeStat(selectedUsers.poll(), EventsType.AUTHORITY);
    SystemUser footer = eventHistoryService.changeStat(selectedUsers.poll(), EventsType.FOOTER);

    messageService.sendMessage(users, phraseService.getPhrase(TextType.FOOTER_ROLL_START), false);
    Thread.sleep(2000);
    sendPhrases(users, TextType.FOOTER_ROLL);
    messageService.sendMessage(users,
        String.format("%s %s (@%s)", phraseService.getPhrase(TextType.FOOTER_ROLL_END), footer.getFirstName(), footer.getUserName()),
        true);
    Thread.sleep(5000);
    messageService.sendMessage(users, phraseService.getPhrase(TextType.AUTHORITY_ROLL_START), false);
    Thread.sleep(2000);
    sendPhrases(users, TextType.AUTHORITY_ROLL);
    messageService.sendMessage(users,
        String.format("%s (@%s) %s", authority.getFirstName(), authority.getUserName(),
            phraseService.getPhrase(TextType.AUTHORITY_ROLL_END)),
        true);
  }

  public void startQuoteOfDay() {
    List<SystemUser> users = userService.getAllActiveUsers();
    messageService.sendMessage(users,
        String.format("%s\n%s", phraseService.getPhrase(TextType.QUOTE_OF_DAY),
            phraseService.getRandomPhrase(TextType.QUOTE)),
        false);
  }

  private void sendPhrases(List<SystemUser> users, TextType textType) {
    LinkedList<String> phrases = phraseService.getSomeRandomPhrases(textType, 4);
    try {
      messageService.sendMessage(users, String.format("4 - %s %s", phrases.poll(), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      messageService.sendMessage(users, String.format("3 - %s %s", phrases.poll(), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      messageService.sendMessage(users, String.format("2 - %s %s", phrases.poll(), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      messageService.sendMessage(users, String.format("1 - %s %s", phrases.poll(), getRandomEmoji(random.nextInt(3))), true);
      Thread.sleep(1200);
      messageService.sendMessage(users, "Испытываем фортуну " + getRandomEmoji(random.nextInt(5)), true);
      Thread.sleep(600);
      messageService.sendDice(users);
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private String getRandomEmoji(int amt) {
    int amount = amt > 0 ? amt : 1;
    ArrayList<Emoji> all = (ArrayList<Emoji>) EmojiManager.getAll();
    return Stream.generate(() -> (int) (Math.random() * all.size())).limit(amount)
        .map(i -> all.get(i).getUnicode())
        .reduce("", (a, b) -> a + b);
  }
}
