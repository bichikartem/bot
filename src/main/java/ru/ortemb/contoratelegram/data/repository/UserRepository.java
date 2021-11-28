package ru.ortemb.contoratelegram.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ortemb.contoratelegram.data.entity.Users;

public interface UserRepository extends JpaRepository<Users, String> {

}