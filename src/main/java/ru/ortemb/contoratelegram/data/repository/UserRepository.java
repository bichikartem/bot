package ru.ortemb.contoratelegram.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ortemb.contoratelegram.data.entity.SystemUser;

public interface UserRepository extends JpaRepository<SystemUser, String> {

}