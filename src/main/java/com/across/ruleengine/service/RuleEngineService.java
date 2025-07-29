package com.across.ruleengine.service;

import com.across.ruleengine.model.Rule;
import com.across.ruleengine.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RuleEngineService {

    private final RuleCompilerService compiler;

    private final RuleRepository ruleRepository;
    private final KieServices ks = KieServices.Factory.get();

    public List<Object> evaluate(String ruleSetId, List<Object> facts) {
        KieContainer kc = compiler.compile(ruleSetId);
        KieBase kb = kc.getKieBase();

        Rule rule = ruleRepository.findByRuleSetId(ruleSetId)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("No rule metadata"));
        String entityName = rule.getEntityName();

        Map<String, Object> params = rule.getParams();
        if (params == null) {
            params = new HashMap<>();
        }

        FactType factType = kb.getKiePackages().stream()
                .flatMap(pkg -> pkg.getFactTypes().stream())
                .filter(ft -> {
                    String simple = ft.getName()
                            .substring(ft.getName().lastIndexOf('.') + 1);
                    return ft.getName().equals(entityName) ||
                            simple.equals(entityName);
                })
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("No declare type " + entityName));

        List<Object> typedFacts = new ArrayList<>(facts.size());
        for (Object raw : facts) {
            if (!(raw instanceof Map<?, ?> map)) {
                typedFacts.add(raw);
                continue;
            }
            try {
                Object bean = factType.newInstance();
                for (var e : map.entrySet()) {
                    FactField field = factType.getField(e.getKey().toString());
                    if (field == null) continue;               // skip unknown keys
                    Object v = coerce(field.getType(), e.getValue());
                    factType.set(bean, field.getName(), v);
                }
                typedFacts.add(bean);
            } catch (Exception ex) {
                throw new RuntimeException("Unable to hydrate fact", ex);
            }
        }

        StatelessKieSession ss = kb.newStatelessKieSession();
        ss.setGlobal("params", params);

        ss.execute(KieServices.Factory.get()
                .getCommands().newInsertElements(typedFacts));

        List<Object> result = new ArrayList<>(typedFacts.size());
        for (Object o : typedFacts) {
            if (!factType.getFactClass().isInstance(o)) {
                result.add(o);
                continue;
            }

            Map<String, Object> m = new LinkedHashMap<>();
            for (FactField f : factType.getFields()) {
                m.put(f.getName(), factType.get(o, f.getName()));
            }
            result.add(m);
        }

        return result;
    }

    private static Object coerce(Class<?> target, Object val) {
        if (val == null || target.isInstance(val)) return val;
        if (target == java.math.BigDecimal.class && val instanceof Number n)
            return new java.math.BigDecimal(n.toString());
        if ((target == Boolean.class || target == boolean.class) && val instanceof String s)
            return Boolean.valueOf(s);
        return val;
    }
}
