package com.alex.web.node.pdm.mapper.user;


import com.alex.web.node.pdm.model.Role;
import com.alex.web.node.pdm.model.enums.RoleName;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.stream.Stream;


class UserMapperUtilTest {
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final UserMapperUtil userMapperUtil = new UserMapperUtil(passwordEncoder);

    @ParameterizedTest
    @MethodSource("getValidArgs")
    void givenRole_whenMap_theReturnRoleAsString(RoleName roleName, String expected) {
        Role giveRole = Role.builder().roleName(roleName).build();

        String actual = userMapperUtil.roleToString(giveRole);

        Assertions.assertThat(actual).isEqualTo(expected);

    }

    static Stream<Arguments> getValidArgs() {
        return Stream.of(
                Arguments.of(RoleName.USER, RoleName.USER.name()),
                Arguments.of(RoleName.ADMIN, RoleName.ADMIN.name())
        );
    }

}