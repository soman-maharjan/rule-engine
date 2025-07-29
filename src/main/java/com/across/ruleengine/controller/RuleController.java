package com.across.ruleengine.controller;

import com.across.ruleengine.dto.RuleDTO;
import com.across.ruleengine.model.Rule;
import com.across.ruleengine.repository.RuleRepository;
import com.across.ruleengine.service.RuleCompilerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleRepository repo;

    private final RuleCompilerService compiler;

    @PostMapping
    public ResponseEntity<String> upload(@RequestBody RuleDTO dto) {
        repo.save(new Rule(null, dto.getRuleSetId(), dto.getDrl(), dto.getEntityName(), dto.getParams()));
        compiler.invalidate(dto.getRuleSetId());
        return ResponseEntity.ok("Rule stored");
    }

    @GetMapping("/entity/{entity}")
    public List<RuleDTO> rulesForEntity(@PathVariable String entity) {
        return repo.findByEntityName(entity)
                .stream()
                .map(r -> { RuleDTO d=new RuleDTO();
                    d.setRuleSetId(r.getRuleSetId());
                    d.setEntityName(r.getEntityName());
                    d.setDrl(r.getDrl()); return d; })
                .toList();
    }
}
