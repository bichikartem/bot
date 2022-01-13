package ru.ortemb.contoratelegram.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ortemb.contoratelegram.data.EventsType;
import ru.ortemb.contoratelegram.data.entity.Event;

public interface EventRepository extends JpaRepository<Event, UUID> {

  Optional<Event> findByUserIdAndEventsType(String userId, EventsType eventsType);

  List<Event> findFirst10ByEventsTypeOrderByCountDesc(EventsType eventsType);
}
