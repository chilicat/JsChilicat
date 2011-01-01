package net.chilicat.testenv.util;

import java.util.Arrays;

/**
 */
public final class MessageBusFactory {
    private MessageBusFactory() {
        throw new AssertionError();
    }

    public static MessageBus nullBus() {
        return Null;
    }

    public static MessageBus verbose() {
        return new VerboseLoggerMessageBus();
    }

    public static MessageBus composite(MessageBus... busList) {
        return new CompositeMessageBus(Arrays.asList(busList));
    }

    public static MessageBus sync(MessageBus bus) {
        return new SynMessageBus(bus);
    }

    public static MessageBus remote() {
        return new RemoteMessageBus(System.out);
    }

    public static MessageBus remote(Printer printer) {
        return new RemoteMessageBus(printer);
    }

    private static MessageBus Null = new AbstractMessageBus() {
        @Override
        public void print(String message) {
            // nothing to do.
        }
    };
}
