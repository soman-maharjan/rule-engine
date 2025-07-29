package com.across.ruleengine.dto;

import lombok.Data;

import java.util.Map;

@Data
public class RuleDTO {
    private String ruleSetId;

    private String entityName;

    private String drl;

    private Map<String, Object> params;
}
