package net.chilicat.testenv.utils;

import java.util.ArrayList;
import java.util.List;

/**
 */
public final class RemoteMessageParser {
    static enum Key {
        SCRIPT_START,
        SCRIPT_DONE,
        SUIT_DONE,
        SUIT_START,
        MODULE_ADD,
        MODULE_START,
        MODULE_DONE,
        TEST_ADD,
        TEST_START,
        TEST_PASS,
        TEST_FAILED,
        LOG,
        UNKNOWN
    }

    private final MessageBus messageBus;
    private final Tracker model = new Tracker();

    public RemoteMessageParser(MessageBus messageBus) {
        if (messageBus == null) {
            throw new NullPointerException("messageBus");
        }
        this.messageBus = messageBus;
    }

    public synchronized void parse(String line) {
        if (line == null) {
            return;
        }
        line = line.trim();
        if (line.length() == 0) {
            return;
        }

        if (!model.isTerminated()) {
            int stop = findClosing(line, 0);
            String token = line.substring(0, stop);

            String str = model.lastAttribute();
            model.addAttribute(token);

            if (stop != str.length()) {
                model.terminate();
            }

            String remainder = line.substring(stop);
            parseAttribute(remainder);
            return;
        }

        Key key = toKey(line);
        switch (key) {
            case UNKNOWN: {
                messageBus.print(line);
                break;
            }
            default: {
                model.newElement(key);
                parseAttribute(line);
            }
        }
    }


    private void parseAttribute(String str) {
        int index = 0;
        do {
            index = str.indexOf("'", index + 1);
            if (index > -1) {
                int stop = findClosing(str, index + 1);
                model.addAttribute(str.substring(index + 1, stop));

                if (stop != str.length()) {
                    model.terminate();
                    break;
                }
                index = stop + 1;
            }
        } while (index > -1);
    }

    private int findClosing(String str, int offset) {
        for (int i = offset; i < str.length(); i++) {
            if (str.charAt(i) == '\'' && str.charAt(i - 1) != '\\') {
                return i;
            }
        }
        return str.length();
    }

    private Key toKey(String line) {
        for (Key k : Key.values()) {
            if (line.startsWith(k.toString() + ": ")) {
                return k;
            }
        }
        return Key.UNKNOWN;
    }

    class Tracker {

        private Element currentElement;

        /**
         * Figures out if current processed element terminated. If not than following line must be added to this current element.
         *
         * @return
         */
        public boolean isTerminated() {
            return currentElement == null;
        }

        public void newElement(Key key) {
            currentElement = new Element(key);
        }

        public void addAttribute(String s) {
            currentElement.attributes.add(s);
        }

        public void terminate() {
            switch (currentElement.key) {
                case MODULE_ADD:
                    messageBus.moduleAdded(currentElement.getValue());
                    break;
                case MODULE_START:
                    messageBus.moduleStart(currentElement.getValue());
                    break;
                case MODULE_DONE:
                    messageBus.moduleDone(currentElement.getValue());
                    break;
                case TEST_ADD:
                    messageBus.testAdded(currentElement.getValue());
                    break;
                case TEST_START:
                    messageBus.testStarted(currentElement.getValue());
                    break;
                case TEST_PASS:
                    messageBus.testPassed(currentElement.getValue());
                    break;
                case TEST_FAILED:
                    messageBus.testFailed(currentElement.getValue(), currentElement.getSecondValue());
                    break;
                case LOG:
                    messageBus.log(currentElement.getValue());
                    break;
                case SCRIPT_START:
                    messageBus.testScriptStart(currentElement.getValue());
                    break;
                case SCRIPT_DONE:
                    messageBus.testScriptDone(currentElement.getValue());
                    break;
                case SUIT_DONE:
                    messageBus.testScriptDone(currentElement.getValue());
                    break;
                case SUIT_START:
                    messageBus.testSuitStart(currentElement.getValue());
                    break;
                case UNKNOWN:
                    break;
            }

            currentElement = null;
        }

        public String lastAttribute() {
            return currentElement.attributes.get(currentElement.attributes.size() - 1);
        }
    }

    class Element {
        Key key;
        List<String> attributes = new ArrayList<String>();

        Element(Key key) {
            this.key = key;
        }

        public String getValue() {
            return attributes.get(0);
        }

        public String getSecondValue() {
            if (attributes.size() > 1) {
                return attributes.get(1);
            }
            return "";
        }
    }
}
