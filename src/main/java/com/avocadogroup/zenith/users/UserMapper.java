package com.avocadogroup.zenith.users;

import com.avocadogroup.zenith.users.dtos.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Dto to expose the user object
    UserDto toDto(User user);
}
