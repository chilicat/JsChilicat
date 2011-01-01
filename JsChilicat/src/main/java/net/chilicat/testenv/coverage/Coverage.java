package net.chilicat.testenv.coverage;

import net.chilicat.testenv.core.resource.Resource;
import net.chilicat.testenv.rhino.LineVisitor;
import org.mozilla.javascript.debug.DebuggableScript;

import java.util.Collection;
import java.util.Collections;

/**
 */
public abstract class Coverage implements LineVisitor {

    private static Coverage NULL = new NullCoverage();

    public static Coverage createCoverage() {
        return new DefaultCoverage();
    }

    public static Coverage nullCoverage() {
        return NULL;
    }

    Coverage() {
    }

    public abstract boolean isEnabled();

    public abstract void visit(DebuggableScript script, int line);

    public abstract void enableCoverageFor(DebuggableScript script, Resource resource);

    public Collection<LineReport> getLineReports() {
        return Collections.emptyList();
    }

    private static class NullCoverage extends Coverage {
        @Override
        public void enableCoverageFor(DebuggableScript script, Resource resource) {

        }

        @Override
        public void visit(DebuggableScript script, int line) {

        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    }
}
