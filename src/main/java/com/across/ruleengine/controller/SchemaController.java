package com.across.ruleengine.controller;

import com.across.ruleengine.dto.SchemaDTO;
import com.across.ruleengine.service.DynamicTypeService;
import com.across.ruleengine.service.RuleCompilerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/schemas")
@RequiredArgsConstructor
public class SchemaController {
    private final DynamicTypeService types;

    private final RuleCompilerService compiler;

    @PostMapping
    public ResponseEntity<String> upload(@RequestBody SchemaDTO dto) {
        types.save(dto);
        compiler.invalidate(dto.getEntityName());
        return ResponseEntity.ok("Schema stored");
    }
}
