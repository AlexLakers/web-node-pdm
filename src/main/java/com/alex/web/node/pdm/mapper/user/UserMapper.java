package com.alex.web.node.pdm.mapper.user;

import com.alex.web.node.pdm.config.CustomUserDetails;
import com.alex.web.node.pdm.dto.user.NewUserDto;
import com.alex.web.node.pdm.dto.user.UpdateUserDto;
import com.alex.web.node.pdm.dto.user.UserDto;
import com.alex.web.node.pdm.model.Role;
import com.alex.web.node.pdm.model.User;
import com.alex.web.node.pdm.model.enums.Provider;
import com.alex.web.node.pdm.model.enums.RoleName;
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
    /* @Mapping(target = "authorities",source = "roles")
     CustomUserSecurity toUserSecurity(User user);*/
    @Mapping(target = "authorities",source = "roles")
    CustomUserDetails toCustomUserDetails(User user);

    /*@Mapping(target = "authorities",source = "roles")
     CustomOidcUser toCustomOidcUser(User user, OidcUserRequest userRequest);*/

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateUser(@MappingTarget User user,  UpdateUserDto updateUserDto);

    @AfterMapping
    default void setDefaultRole(@MappingTarget User user,NewUserDto newUserDto){
        user.setRoles(Collections.singletonList(Role.builder().roleName(RoleName.USER).build()));
        user.setProvider(Provider.DAO_LOCAL);

    }

}
