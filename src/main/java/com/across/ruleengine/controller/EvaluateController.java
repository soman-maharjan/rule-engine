package com.across.ruleengine.controller;

import com.across.ruleengine.service.RuleEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/evaluate")
@RequiredArgsConstructor
public class EvaluateController {
    private final RuleEngineService engine;

    @PostMapping("/{ruleSetId}")
    public ResponseEntity<List<Object>> eval(@PathVariable String ruleSetId,
                                             @RequestBody List<Object> payload) {
        return ResponseEntity.ok(engine.evaluate(ruleSetId, payload));
    }
}
