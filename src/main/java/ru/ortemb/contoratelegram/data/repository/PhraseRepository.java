package ru.ortemb.contoratelegram.data.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ortemb.contoratelegram.data.TextType;
import ru.ortemb.contoratelegram.data.entity.Phrase;

public interface PhraseRepository extends JpaRepository<Phrase, String> {

  List<Phrase> findAllByTextType(TextType textType);

  Phrase findByTextType(TextType textType);

}
