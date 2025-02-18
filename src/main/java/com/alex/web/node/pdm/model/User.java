package com.alex.web.node.pdm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "username")
@ToString(of = "username")
@Builder
@Entity
@Table(name = "users")
//@NamedEntityGraph(name = "roles",attributeNodes = {@NamedAttributeNode("roles")})
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


    @ElementCollection
    @CollectionTable(name = "users_roles",joinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    @AttributeOverride(name="roleName",column = @Column(name = "name"))
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @Builder.Default
    List<Specification> specifications = new ArrayList<>();

}
