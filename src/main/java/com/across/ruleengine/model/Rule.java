package com.across.ruleengine.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rule {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "rule_set_id", nullable = false)
    private String ruleSetId;

    @Column(columnDefinition = "text")
    private String drl;

    @Column(name = "entity_name", nullable = false)
    private String entityName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> params = Map.of();
}
