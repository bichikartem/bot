package ru.ortemb.contoratelegram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ortemb.contoratelegram.data.TextType;
import ru.ortemb.contoratelegram.data.entity.Phrase;
import ru.ortemb.contoratelegram.data.repository.PhraseRepository;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhraseService {

  private final Random random = new Random();
  private final PhraseRepository phraseRepository;

  public String getPhrase(TextType textType) {
    return phraseRepository.findByTextType(textType).getText();
  }

  public String getRandomPhrase(TextType textType) {
    List<Phrase> listPhrases = phraseRepository.findAllByTextType(textType);
    return listPhrases.get(random.nextInt(listPhrases.size())).getText();
  }

  public LinkedList<String> getSomeRandomPhrases(TextType textType, long amount) {
    List<Phrase> phrases = phraseRepository.findAllByTextType(textType);
    if (phrases.size() < amount){
      throw new RuntimeException("The amount of phrases is less than necessary");
    } else {
      Collections.shuffle(phrases);
      return phrases.stream()
              .limit(amount)
              .map(Phrase::getText)
              .collect(Collectors.toCollection(LinkedList::new));
    }
  }

}