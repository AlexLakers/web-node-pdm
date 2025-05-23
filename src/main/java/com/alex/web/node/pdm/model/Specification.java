package com.alex.web.node.pdm.model;

import com.alex.web.node.pdm.model.enums.Provider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an object model for database table 'users'.
 * @see Detail detail
 * @see User specification
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = "code")
@EqualsAndHashCode(of = "code")
@Entity
@Builder
@Table(name = "specification")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
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
    @NotAudited
    private List<Detail> details = new ArrayList<>();

}
