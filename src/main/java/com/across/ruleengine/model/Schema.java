package com.across.ruleengine.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "rule_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schema {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "entity_name", unique = true, nullable = false)
    private String entityName;

    @Column(columnDefinition = "text", name = "declare_drl", nullable = false)
    private String declareBlock;
}

