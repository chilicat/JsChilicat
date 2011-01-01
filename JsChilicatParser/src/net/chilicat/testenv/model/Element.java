package net.chilicat.testenv.model;

import java.util.ArrayList;
import java.util.List;

/**
 */
public abstract class Element {


    enum State {
        WAITING,
        RUNNING,
        FINISHED;

    }

    private boolean terminated = true;

    private final List<String> attributes = new ArrayList<String>();

    private final List<LogEntry> logs = new ArrayList<LogEntry>();

    private volatile State state = State.WAITING;

    private Element parent;

    private long startedAt = 0;

    private long duration = 0;

    public Element getParent() {
        return parent;
    }

    public String getName() {
        return attributeAt(1);
    }

    public void setParent(Element parent) {
        this.parent = parent;
    }

    public boolean isTerminated() {
        return terminated;
    }

    void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }

    void started() {
        assert state == State.WAITING : "element must be in init state: " + this;
        state = State.RUNNING;
        startedAt = System.currentTimeMillis();
    }

    public boolean passed() {
        return passed;
    }

    public boolean running() {
        return state == State.RUNNING;
    }

    public boolean done() {
        return state == State.FINISHED;
    }

    public State getState() {
        return state;
    }

    private boolean passed = false;

    void ended(boolean b) {
        assert state == State.RUNNING : "element not started: " + this + " State: " + state;
        state = State.FINISHED;

        duration = System.currentTimeMillis() - startedAt;
        passed = b;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "terminated=" + terminated +
                ", attributes=" + attributes +
                ", logs=" + logs +
                '}';
    }

    public List<LogEntry> logs() {
        return new ArrayList<LogEntry>(logs);
    }

    public void addLog(LogEntry lastElement) {
        logs.add(lastElement);
    }

    void addAttribute(String str) {
        if (isTerminated()) {
            attributes.add(str);
        } else {
            String last = lastAttribute();
            attributes.set(attributes.size() - 1, last + str);
        }
    }

    int attributeCount() {
        return attributes.size();
    }

    String lastAttribute() {
        if (attributes.isEmpty()) {
            return null;
        }
        return attributes.get(attributes.size() - 1);
    }

    public String attributeAt(int i) {
        if (attributes.size() <= i) {
            return "";
        }
        return attributes.get(i);
    }
}
