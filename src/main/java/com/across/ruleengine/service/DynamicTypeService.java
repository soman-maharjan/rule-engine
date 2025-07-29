package com.across.ruleengine.service;

import com.across.ruleengine.dto.SchemaDTO;
import com.across.ruleengine.model.Schema;
import com.across.ruleengine.repository.SchemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DynamicTypeService {

    private final SchemaRepository schemaRepo;

    public void save(SchemaDTO dto) {
        var declare = generateDeclare(dto);
        schemaRepo.save(new Schema(null, dto.getEntityName(), declare));
    }

    public String resolveDeclare(String entityName) {
        return schemaRepo.findByEntityName(entityName)
                .map(Schema::getDeclareBlock)
                .orElse("");
    }

    private String generateDeclare(SchemaDTO s) {
        var b = new StringBuilder();
        b.append("declare ").append(s.getEntityName()).append("\n");
        s.getFields().forEach(f ->
                b.append("  ").append(f.getName()).append(" : ").append(f.getType()).append("\n"));
        return b.append("end\n").toString();
    }
}