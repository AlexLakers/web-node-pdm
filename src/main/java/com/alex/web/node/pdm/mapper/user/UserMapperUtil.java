package com.alex.web.node.pdm.mapper.user;


import com.alex.web.node.pdm.model.Role;
import com.alex.web.node.pdm.model.enums.Provider;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This is util-class for {@link UserMapper mapper}.
 */

@Named("UserMapperUtil")
@Component
@RequiredArgsConstructor
public class UserMapperUtil {

    private final PasswordEncoder passwordEncoder;

    @Named("roleToString")
    public String roleToString(Role role){
        return role.getRoleName().name();
    }

    @Named("encryptPassword")
    public String encryptPassword(String password){
        return passwordEncoder.encode(password);
    }
    @Named("providerToString")
    public String providerToString(Provider provider){
        return provider.name();
    }

 /*   @AfterMapping
    public void setDefaultRoleAndProvider(@MappingTarget User user, NewUserDto newUserDto){
        user.setRoles(Collections.singletonList(Role.builder().roleName(RoleName.USER).build()));
        user.setProvider(Provider.DAO_LOCAL);
    }*/


}
