package net.chilicat.testenv.coverage;

import net.chilicat.testenv.core.resource.Resource;

import java.util.*;

/**
 */
public final class LineReport {
    private final List<Line> lines;
    private final LineComparator sorter = new LineComparator();
    private Resource resource;

    private Line currentLine;

    LineReport(Resource resource, Collection<Line> lines) {
        this.resource = resource;
        this.lines = new ArrayList<Line>(lines);
        Collections.sort(this.lines, sorter);
    }

    public double getCoverage() {
        int visit = 0;
        int notVisit = 0;
        int total = lines.size();

        for (Line line : lines) {
            if (line.hasVisitors()) {
                visit++;
            } else {
                notVisit++;
            }
        }

        return (100.0 / total) * visit;
    }

    public Resource getResource() {
        return resource;
    }

    public boolean isEnabled(int lineNumber) {
        setCurrentLineTo(lineNumber);
        return currentLine != null;
    }

    public int getVisitors(int lineNumber) {
        setCurrentLineTo(lineNumber);
        return currentLine != null ? currentLine.getVisitorCount() : 0;

    }

    private void setCurrentLineTo(int lineNumber) {
        if (currentLine == null || currentLine.getLineNumber() != lineNumber) {
            int index = Collections.binarySearch(lines, lineNumber, sorter);
            currentLine = index < 0 ? null : lines.get(index);
        }
    }

    public Collection<Integer> getNotVisitedLines() {
        List<Integer> list = new ArrayList<Integer>();
        for (Line l : lines) {
            if (!l.hasVisitors()) {
                list.add(l.getLineNumber());
            }
        }
        return Collections.unmodifiableCollection(list);
    }

    private static class LineComparator implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            return toInt(o1).compareTo(toInt(o2));
        }

        private Integer toInt(Object o) {
            if (o instanceof Line) {
                return ((Line) o).getLineNumber();
            }
            return (Integer) o;
        }
    }
}
