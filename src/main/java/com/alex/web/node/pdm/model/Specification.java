package com.alex.web.node.pdm.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = "code")
@EqualsAndHashCode(of = "code")
@Entity
@Builder
@Table(name = "specification")
@DynamicUpdate
public class Specification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String code;

    private Integer amount;

    @Column(name = "description")
    private String desc;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "specification")
    @Builder.Default
    private List<Detail> details = new ArrayList<>();

}
