package com.across.ruleengine.service;

import com.across.ruleengine.model.Rule;
import com.across.ruleengine.model.Schema;
import com.across.ruleengine.repository.RuleRepository;
import com.across.ruleengine.repository.SchemaRepository;
import lombok.RequiredArgsConstructor;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RuleCompilerService {
    private final RuleRepository  ruleRepo;

    private final SchemaRepository schemaRepo;

    private final KieServices ks = KieServices.Factory.get();

    private final Map<String, KieContainer> cache = new HashMap<>();

    public KieContainer compile(String ruleSetId) {

        // return cached container if we already built this ruleset
        if (cache.containsKey(ruleSetId)) return cache.get(ruleSetId);

        // fetch rules from DB
        List<Rule> rules = ruleRepo.findByRuleSetId(ruleSetId);
        if (rules.isEmpty())
            throw new IllegalStateException("No rules in set " + ruleSetId);

        String entity  = rules.get(0).getEntityName();
        String declare = schemaRepo.findByEntityName(entity)
                .map(Schema::getDeclareBlock)
                .orElse("");

        // decide which type the rule should pattern-match
        boolean mapMode    = declare.isBlank();
        String  patternType = mapMode ? "java.util.Map" : entity;

        // write everything into an isolated KieFileSystem
        KieFileSystem kfs = ks.newKieFileSystem();

        // give every rule-set its own coordinates so two sets don’t overwrite each other
        ReleaseId releaseId = ks.newReleaseId("com.dynamic", ruleSetId, "1.0.0");
        kfs.generateAndWritePomXML(releaseId);

        if (!declare.isBlank())
            kfs.write("src/main/resources/declare.drl", declare);

        for (int i = 0; i < rules.size(); i++) {
            String drl = rules.get(i).getDrl().replace("${entity}", patternType);
            kfs.write(String.format("src/main/resources/%s/r%d.drl", ruleSetId, i), drl);
        }

        // build & verify
        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        Results res   = kb.getResults();
        if (res.hasMessages(Message.Level.ERROR))
            throw new IllegalStateException("Rule errors: " + res.getMessages());

        // create container, cache, return
        KieContainer kc = ks.newKieContainer(releaseId);
        cache.put(ruleSetId, kc);
        return kc;
    }

    public void invalidate(String ruleSetId) { cache.remove(ruleSetId); }
}
