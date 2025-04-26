package com.alex.web.node.pdm.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 * This class is an object model for database table 'detail'.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "specification", callSuper = false)
@ToString(exclude = "specification")
@Entity
@Builder
@Table(name = "detail")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Detail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer amount;

    @ManyToOne(optional=false,fetch = FetchType.LAZY)
    @JoinColumn(name = "specification_id")
    private Specification specification;

}
