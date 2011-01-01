package net.chilicat.testenv.core;

import net.chilicat.testenv.coverage.Coverage;
import net.chilicat.testenv.util.MessageBus;

/**
 */
public interface TestExecutor {

    public void setup(TestConfig config, MessageBus messageBus);

    void execute(TestConfig config, TestSuit suit);

    public void dispose(TestConfig config);

    public void setCoverage(Coverage coverage);

    public Coverage getCoverage();
}
