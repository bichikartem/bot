package ru.ortemb.contoratelegram.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ortemb.contoratelegram.data.EventType;
import ru.ortemb.contoratelegram.data.entity.EventHistory;

public interface EventHistoryRepository extends JpaRepository<EventHistory, UUID> {

  Optional<EventHistory> findByUserIdAndEventType(String userId, EventType eventType);

  List<EventHistory> findFirst10ByEventTypeOrderByCountDesc(EventType eventType);
}
