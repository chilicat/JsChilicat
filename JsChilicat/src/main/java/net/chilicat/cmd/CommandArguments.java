package net.chilicat.cmd;

import java.io.File;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Logger;

/**
 */
public class CommandArguments {
    private final Map<String, Option> options = new LinkedHashMap<String, Option>();
    private final Map<String, Object> values = new HashMap<String, Object>();
    private final StringBuffer errorBus = new StringBuffer();

    public Option option(String flag) {
        Option opt = options.get(flag);
        if (opt == null) {
            opt = new Option(flag);
            options.put(flag, opt);
        }
        return opt;
    }

    public boolean init(String... args) {
        Set<String> must = mustSet();

        boolean nextArgument = false;
        Option option = null;

        for (String str : args) {
            if (nextArgument && str.startsWith("-")) {
                errorBus.append(String.format("Argument for flag (%s) expected.\n", option.getFlag()));
                return false;
            }

            if (nextArgument) {
                nextArgument = false;
                if (option.fileList()) {
                    values.put(option.getFlag(), str.split(File.pathSeparator));
                } else {
                    values.put(option.getFlag(), str);
                }
            } else if (str.startsWith("-")) {
                String flag = str.substring(1);
                option = options.get(flag);

                if (option == null) {
                    errorBus.append(String.format("Uknown flag: %s\n", str));
                    continue;
                }

                must.remove(flag);
                nextArgument = option.hasArgument();

                if (!option.hasArgument()) {
                    values.put(option.getFlag(), true);
                }
            } else {
                errorBus.append(String.format("Flag expected"));
                return false;
            }
        }

        if (!must.isEmpty()) {
            errorBus.append("Mandatory flags are not defined: ");
            for (String flag : must) {
                Option opt = options.get(flag);
                errorBus.append(opt.hasArgument() ? String.format("-%s <arg> ", opt.getFlag()) : String.format("-%s ", opt.getFlag()));

            }
        }

        return must.isEmpty();
    }

    private Set<String> mustSet() {
        Set<String> must = new HashSet<String>();
        for (Option opt : options.values()) {
            if (opt.type() == Type.MUST) {
                must.add(opt.getFlag());
            }
        }
        return must;
    }

    public void printErrors(PrintStream out) {
        out.println(errorBus);
        Logger.getAnonymousLogger().severe(errorBus.toString());
    }

    public void printHelp(PrintStream out) {
        StringBuffer usage = new StringBuffer("Usage: <cmd> "), help = new StringBuffer();

        for (Option opts : options.values()) {
            if (opts.isHide()) {
                continue;
            }
            help.append(String.format("%-15s\t%-20s\t%s \n", "-" + opts.getFlag(), opts.help(), opts.desc()));

            String usageHelp = opts.hasArgument() ? String.format("-%s <arg> ", opts.getFlag()) : String.format("-%s ", opts.getFlag());
            usageHelp = opts.type() == Type.OPTIONAL ? String.format("[ %s] ", usageHelp) : usageHelp;

            usage.append(usageHelp);
        }
        out.println(usage);
        out.println(help);
    }

    public boolean getBoolean(String flag, boolean def) {
        Object value = values.get(flag);
        if (value == null) {
            return def;
        }
        return (Boolean) value;
    }

    public List<String> getStrings(String flag) {
        Object obj = values.get(flag);
        if (obj instanceof String) {
            return Collections.singletonList(obj.toString());
        } else {
            String[] strings = (String[]) obj;
            return strings == null ? Collections.<String>emptyList() : Arrays.asList(strings);
        }
    }

    public String getString(String flag, String defaultValue) {
        List<String> l = getStrings(flag);
        if (l.isEmpty()) {
            return defaultValue;
        }
        return l.get(0);
    }

    public int getInt(String flag, int defaultVal) {
        Object value = values.get(flag);
        if (value == null) {
            return defaultVal;
        }
        return Integer.valueOf(value.toString());
    }

    public void logOptions() {
        StringBuilder b = new StringBuilder();
        b.append("Options:");
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            b.append("\n");
            b.append(" ").append("-").append(entry.getKey()).append(" ");
            if (entry.getValue() instanceof String[]) {
                boolean first = true;
                for (String val : (String[]) entry.getValue()) {
                    if(!first) {
                        b.append(File.pathSeparator);
                    }
                    b.append(val);
                    first = false;
                }
            } else {
                b.append(entry.getValue());
            }


        }
        Logger.getAnonymousLogger().info(b.toString());
    }
}
