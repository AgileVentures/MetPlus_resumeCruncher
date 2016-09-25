package org.metplus.curriculum.test;

import org.junit.rules.ExternalResource;

/**
 * Created by joao on 8/18/16.
 */
public class BeforeAfterRule extends ExternalResource {
    private BeforeAfterInterface testObject;
    public BeforeAfterRule(BeforeAfterInterface testObject) {
        this.testObject = testObject;
    }

    @Override
    protected void after() {
        this.testObject.after();
    };

    @Override
    protected void before() throws Throwable {
        this.testObject.before();
    };
};