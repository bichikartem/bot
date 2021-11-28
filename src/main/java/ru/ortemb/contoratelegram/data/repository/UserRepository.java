package ru.ortemb.contoratelegram.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ortemb.contoratelegram.data.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

}