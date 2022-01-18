package ru.ortemb.contoratelegram.data.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import ru.ortemb.contoratelegram.data.EventType;

@Entity(name = "Events")
@Table(schema = "public", name = "Events")
@DynamicUpdate
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventHistory {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id", updatable = false, nullable = false)
  UUID id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  SystemUser user;

  @Column(name = "events_type")
  @Enumerated(EnumType.STRING)
  EventType eventType;

  @Column(name = "count")
  Integer count;
}
