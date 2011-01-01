package net.chilicat.testenv.model;

import java.util.EventObject;

/**
 */
public class TestReportEvent extends EventObject {
    private final Element element;

    public TestReportEvent(TestReportModel model, Element element) {
        super(model);
        this.element = element;
    }

    @Override
    public TestReportModel getSource() {
        return (TestReportModel) super.getSource();
    }

    public Element getElement() {
        return element;
    }
}
