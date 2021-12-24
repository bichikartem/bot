package ru.ortemb.contoratelegram.data.mapper;

import org.mapstruct.Mapper;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ortemb.contoratelegram.data.entity.SystemUser;

@Mapper(componentModel = "spring")
public interface UsersMapper {

  SystemUser telegramUserToEntity(User from);
}