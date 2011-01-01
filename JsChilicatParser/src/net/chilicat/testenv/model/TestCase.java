package net.chilicat.testenv.model;

/**
 */
public class TestCase extends Element {
    private String errorMessage = "";

    public TestCase(String name) {
        addAttribute(Long.toString(System.currentTimeMillis()));
        addAttribute(name);
    }

    public TestCase() {
    }

    @Override
    public String toString() {
        return attributeAt(1); // name
    }

    public String getName() {
        return attributeAt(1);
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
