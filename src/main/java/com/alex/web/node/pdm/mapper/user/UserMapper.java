package com.alex.web.node.pdm.mapper.user;

import com.alex.web.node.pdm.dto.NewUserDto;
import com.alex.web.node.pdm.dto.UpdateUserDto;
import com.alex.web.node.pdm.dto.UserDto;
import com.alex.web.node.pdm.model.User;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {UserMapperUtil.class},
        builder = @Builder(disableBuilder = true)
)
@Component
public interface UserMapper {
    @Mapping(target = "roles",qualifiedByName = {"UserMapperUtil","roleToString"}, source = "roles")
    @Mapping(target = "provider",qualifiedByName = {"UserMapperUtil","providerToString"},source = "provider")
    UserDto toUserDto(User user);

    List<UserDto> toUserDtoList(List<User> users);

    @Mapping(target = "password",qualifiedByName = {"encryptPassword"}, source = "password")
   /* @Mapping(target = "roles",qualifiedByName = "", source = "roles")*/
    User toUserFromNewUserDto(NewUserDto newUserDto);


    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateUser(@MappingTarget User user,  UpdateUserDto updateUserDto);



}
