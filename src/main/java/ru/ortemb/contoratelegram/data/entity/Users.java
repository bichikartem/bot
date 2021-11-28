package ru.ortemb.contoratelegram.data.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Users {

  @Id
  private String id;
  private String username;
  private String firstName;
  private String lastName;
}
