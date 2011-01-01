package net.chilicat.testenv.coverage;

/**
 */
public final class Line {
    private final int lineNumber;
    private final boolean isFunction;
    private final int depth;
    private int visitors;

    public Line(int lineNumber, int depth, boolean isFunction) {
        this.lineNumber = lineNumber;
        this.depth = depth;
        this.isFunction = isFunction;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isFunction() {
        return isFunction;
    }

    public void visit() {
        visitors++;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public boolean hasVisitors() {
        return visitors > 0;
    }

    @Override
    public String toString() {
        return "Line{" +
                "lineNumber=" + lineNumber +
                ", visitors=" + visitors +
                '}';
    }

    public int getVisitorCount() {
        return visitors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        if (lineNumber != line.lineNumber) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return lineNumber;
    }
}
