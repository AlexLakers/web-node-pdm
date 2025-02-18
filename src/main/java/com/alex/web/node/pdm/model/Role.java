package com.alex.web.node.pdm.model;

import com.alex.web.node.pdm.model.enums.RoleName;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Role{
    @Enumerated(EnumType.STRING)
    private RoleName roleName;

}
