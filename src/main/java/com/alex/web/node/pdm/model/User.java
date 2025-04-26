package com.alex.web.node.pdm.model;

import com.alex.web.node.pdm.model.enums.Provider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is an object model for database table 'users'.
 *
 * @see Provider provider
 * @see Role role
 * @see Specification specification
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "username")
@ToString(of = "username")
@Builder
@Entity
@Table(name = "users")
//@NamedEntityGraph(name = "roles",attributeNodes = {@NamedAttributeNode("roles")})
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String lastname;

    @Column(name = "birth_day")
    private LocalDate birthday;

    @Column(unique = true,nullable = false)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @ElementCollection
    @CollectionTable(name = "users_roles",joinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    @AttributeOverride(name="roleName",column = @Column(name = "name"))
    @NotAudited
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @Builder.Default
    @NotAudited
    List<Specification> specifications = new ArrayList<>();

}
