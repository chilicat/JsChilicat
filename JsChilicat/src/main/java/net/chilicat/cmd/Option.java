package net.chilicat.cmd;

/**
 */
public class Option {
    private final String flag;
    private Type type;
    private String name;
    private String help;
    private boolean hasArgument;
    private boolean fileList;
    private boolean hide;

    Option(String flag) {
        this.flag = flag;
    }

    public boolean hasArgument() {
        return hasArgument;
    }

    public Option hasArgument(boolean hasArgument) {
        this.hasArgument = hasArgument;
        return this;
    }

    public String getFlag() {
        return flag;
    }

    public Type type() {
        return type;
    }

    public Option type(Type type) {
        this.type = type;
        return this;
    }

    public String desc() {
        return name;
    }

    public Option desc(String name) {
        this.name = name;
        return this;
    }

    public String help() {
        return help;
    }

    public Option help(String help) {
        this.help = help;
        return this;
    }

    public Option fileList(boolean fileList) {
        this.fileList = fileList;
        return this;
    }

    public boolean fileList() {
        return fileList;
    }

    public void hide(boolean b) {
        hide = b;
    }

    public boolean isHide() {
        return hide;
    }
}
