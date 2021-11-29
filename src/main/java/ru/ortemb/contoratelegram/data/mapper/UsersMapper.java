package ru.ortemb.contoratelegram.data.mapper;

import org.mapstruct.Mapper;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ortemb.contoratelegram.data.entity.Users;

@Mapper(componentModel = "spring")
public interface UsersMapper {

  Users telegramUserToEntity(User from);
}