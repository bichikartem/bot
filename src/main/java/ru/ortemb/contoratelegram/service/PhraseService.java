package ru.ortemb.contoratelegram.service;

import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ortemb.contoratelegram.data.TextType;
import ru.ortemb.contoratelegram.data.entity.Phrase;
import ru.ortemb.contoratelegram.data.repository.PhraseRepository;

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

}
