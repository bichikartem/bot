package ru.ortemb.contoratelegram.data.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ortemb.contoratelegram.data.TextType;
import ru.ortemb.contoratelegram.data.entity.Phrases;

public interface PhrasesRepository extends JpaRepository<Phrases, String> {

  List<Phrases> findAllByTextType(TextType textType);

}
