package ru.ortemb.contoratelegram.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Users {

  @Id
  private String id;
  private String firstName;
  private String lastName;
  @Column(name = "username")
  private String userName;
}
