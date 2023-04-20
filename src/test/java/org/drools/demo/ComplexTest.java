
package org.drools.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utils.shim.Map.of;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComplexTest {
    static final Logger LOG = LoggerFactory.getLogger(ComplexTest.class);

    private DMNRuntime dmnRuntime;
    private DMNModel dmnModelUT;
    
    @Before
    public void init() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        dmnRuntime = KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);

        final String namespace = "https://kiegroup.org/dmn/_44C75115-3486-46CD-B9FA-D8E7E7F65970";
        final String modelName = "complex";
        dmnModelUT = dmnRuntime.getModel(namespace, modelName);
    }
    
    @Test
    public void test1() {
        Map<String, ?> address = of("street", "71 Montview Drive", "city", "Toledo", "state", "OH");
        Map<String, ?> person = of("name", "Kerry Grant", "age", 18, "likesBroccoli", false, "address", address);
        
        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("InputData-1", person);

        LOG.info("Evaluating DMN model");
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModelUT, dmnContext);

        LOG.info("Checking results: {}", dmnResult);
        assertFalse(dmnResult.hasErrors());

        assertEquals(DecisionEvaluationStatus.SUCCEEDED, dmnResult.getDecisionResultByName("outValue").getEvaluationStatus());
        assertEquals(new BigDecimal(20), dmnResult.getDecisionResultByName("outValue").getResult());
    }

    @Test
    public void test2() {
        Map<String, ?> address = of("street", "234 Maple", "Los Angeles", "CA");
        Map<String, ?> person = of("name", "John Birch", "age", 140, "likesBroccoli", true, "address", address);

        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("InputData-1", person);

        LOG.info("Evaluating DMN model");
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModelUT, dmnContext);

        LOG.info("Checking results: {}", dmnResult);
        LOG.info("Checking results messages: {}", dmnResult.getMessages());
        assertTrue("expecting failing inputdata validation", dmnResult.hasErrors());
    }
}