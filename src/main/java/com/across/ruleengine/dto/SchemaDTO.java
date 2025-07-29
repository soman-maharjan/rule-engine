package com.across.ruleengine.dto;

import lombok.Data;
import java.util.List;

@Data
public class SchemaDTO {
    private String entityName;

    private List<Field> fields;

    @Data
    public static class Field {
        private String name;

        private String type;
    }
}
