package com.alex.web.node.pdm.model;

import com.alex.web.node.pdm.model.enums.RoleName;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * This class is an additional part object model  for database tables 'users' and 'users_roles'.
 *
 * @see User user
 * @see RoleName roleName
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Role implements GrantedAuthority {
    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    @Override
    public String getAuthority() {
        return roleName.name();
    }
}
