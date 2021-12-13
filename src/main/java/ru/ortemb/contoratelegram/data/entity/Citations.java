package ru.ortemb.contoratelegram.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity(name = "Citations")
@Table(schema = "public", name = "Citations")
@DynamicUpdate
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Citations {

  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "body")
  private String body;

}
