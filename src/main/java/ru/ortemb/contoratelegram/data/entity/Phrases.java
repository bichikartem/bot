package ru.ortemb.contoratelegram.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import ru.ortemb.contoratelegram.data.TextType;

@Entity(name = "Phrases")
@Table(schema = "public", name = "Phrases")
@DynamicUpdate
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Phrases {

  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private TextType textType;

  @Column(name = "text")
  private String text;

}
