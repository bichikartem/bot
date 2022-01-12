package ru.ortemb.contoratelegram.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ortemb.contoratelegram.data.EventsType;
import ru.ortemb.contoratelegram.data.entity.Events;

public interface EventsRepository extends JpaRepository<Events, UUID> {

  Optional<Events> findByUserIdAndEventsType(String userId, EventsType eventsType);

  List<Events> findFirst10ByEventsTypeOrderByCountDesc(EventsType eventsType);
}
