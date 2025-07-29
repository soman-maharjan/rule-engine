package com.across.ruleengine.repository;


import com.across.ruleengine.model.Schema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchemaRepository extends JpaRepository<Schema, UUID> {
    Optional<Schema> findByEntityName(String entityName);
}
