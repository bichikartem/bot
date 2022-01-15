package ru.ortemb.contoratelegram.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ortemb.contoratelegram.data.entity.SystemUser;

import java.util.List;

public interface UserRepository extends JpaRepository<SystemUser, String> {

  List<SystemUser> findAllByIsBlocked(boolean isBlocked);

}