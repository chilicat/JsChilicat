package net.chilicat.testenv.core;

/**
 */
public enum TestUnitFramework {
    qunit("/lib/qunit.js", "/lib/qunit_adapter.js", "QUnit", "http://docs.jquery.com/QUnit");

    private final String framework;
    private final String adapter;
    private final String link;
    private String displayName;

    TestUnitFramework(String framework, String adapter, String displayName, String link) {
        this.framework = framework;
        this.adapter = adapter;
        this.link = link;
        this.displayName = displayName;
    }

    public String getFramework() {
        return framework;
    }

    public String getAdapter() {
        return adapter;
    }

    public String getLink() {
        return link;
    }

    public String getDisplayName() {
        return displayName;
    }
}
