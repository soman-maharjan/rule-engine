package com.across.ruleengine.repository;

import com.across.ruleengine.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RuleRepository extends JpaRepository<Rule, UUID> {
    List<Rule> findByRuleSetId(String ruleSetId);

    List<Rule> findByEntityName(String entityName);
}
