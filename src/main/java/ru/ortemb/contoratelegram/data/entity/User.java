package ru.ortemb.contoratelegram.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity(name = "users")
@Table(name = "users")
@Data
public class User {

  @Id
  String id;
  @Column
  String username;
  @Column
  String firstName;
  @Column
  String lastName;
}
