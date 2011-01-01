package net.chilicat.testenv.coverage;

import net.chilicat.testenv.core.resource.Resource;
import org.mozilla.javascript.debug.DebuggableScript;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 */
class DefaultCoverage extends Coverage {
    private final Map<String, ScriptInfo> map =
            new HashMap<String, ScriptInfo>();

    private final Map<String, Resource> enabled = new HashMap<String, Resource>();

    @Override
    public Collection<LineReport> getLineReports() {
        Collection<LineReport> reports = new ArrayList<LineReport>();
        for (Map.Entry<String, ScriptInfo> e : map.entrySet()) {
            ScriptInfo scriptInfo = e.getValue();
            Resource resource = enabled.get(e.getKey());
            reports.add(new LineReport(resource, scriptInfo.getLines()));

        }
        return reports;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void enableCoverageFor(DebuggableScript script, Resource resource) {
        enabled.put(script.getSourceName(), resource);
        pushLines(script, get(script, true));
    }

    private void pushLines(DebuggableScript script, ScriptInfo scriptInfo) {
        int depth = calcDepth(script);

        boolean addFunctionLine = (!script.isTopLevel() && script.isFunction());
        for (int line : script.getLineNumbers()) {
            scriptInfo.addLine(new Line(line, depth, addFunctionLine));
            addFunctionLine = false;
        }
        for (int i = 0; i < script.getFunctionCount(); i++) {
            scriptInfo.increaseFunctionCount();
            pushLines(script.getFunction(i), scriptInfo);
        }
    }

    private int calcDepth(DebuggableScript script) {
        int depth = 0;
        DebuggableScript parent = script;
        while ((parent = parent.getParent()) != null) {
            depth++;
        }
        return depth;
    }

    public void visit(DebuggableScript script, int line) {
        if (enabled.containsKey(script.getSourceName())) {
            int depth = calcDepth(script);

            ScriptInfo info = get(script, true);
            Line lineObj = info.getLine(line, depth);
            lineObj.visit();
        }
    }

    private ScriptInfo get(DebuggableScript script, boolean create) {
        ScriptInfo info = map.get(script.getSourceName());
        if (info == null && create) {
            info = new ScriptInfo();
            map.put(script.getSourceName(), info);
        }
        return info;
    }


    private static class ScriptInfo {
        private final Map<Key, Line> lines = new HashMap<Key, Line>();
        private int functionCount = 0;

        public Collection<Line> getLines() {
            return lines.values();
        }

        public void increaseFunctionCount() {
            functionCount++;
        }

        public int getFunctionCount() {
            return functionCount;
        }

        public void addLine(Line line) {
            Key key = Key.get(line.getDepth(), line.getLineNumber());

            if (!lines.containsKey(key)) {
                lines.put(key, line);
            }
        }

        public Line getLine(int line, int depth) {
            return getLine(line, depth, true);
        }

        public Line getLine(int line, int depth, boolean create) {
            Key key = Key.get(depth, line);
            Line l = lines.get(key);

            if (l == null && create) {
                l = new Line(line, depth, false);
                lines.put(key, l);
                return l;
            }
            return l;
        }
    }


    static class Key {
        private final int depth;
        private final int line;

        public static Key get(int depth, int line) {
            return new Key(depth, line);
        }

        private Key(int depth, int line) {
            this.depth = depth;
            this.line = line;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (depth != key.depth) return false;
            if (line != key.line) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = depth;
            result = 31 * result + line;
            return result;
        }
    }
}
