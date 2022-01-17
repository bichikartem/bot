package ru.ortemb.contoratelegram.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ortemb.contoratelegram.data.EventsType;
import ru.ortemb.contoratelegram.data.entity.EventHistory;

public interface EventHistoryRepository extends JpaRepository<EventHistory, UUID> {

  Optional<EventHistory> findByUserIdAndEventsType(String userId, EventsType eventsType);

  List<EventHistory> findFirst10ByEventsTypeOrderByCountDesc(EventsType eventsType);
}
