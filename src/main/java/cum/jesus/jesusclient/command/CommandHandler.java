package cum.jesus.jesusclient.command;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.annotations.*;
import cum.jesus.jesusclient.command.arguments.*;
import cum.jesus.jesusclient.command.commands.DiscordCommand;
import cum.jesus.jesusclient.command.commands.HelpCommand;
import cum.jesus.jesusclient.command.commands.ModuleCommand;
import cum.jesus.jesusclient.command.commands.dev.TestCommand;
import cum.jesus.jesusclient.util.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CommandHandler {
    private static final String[] EMPTY_ARRAY = new String[] {""};
    private static final String DELIMITER = "\uD7FF";
    private static final String ENTRY_METHOD_NAME = "ENTRY" + DELIMITER + DELIMITER + "ENTRY";

    private final List<RegisteredCommand> commands = new ArrayList<>();
    private final HashMap<Class<?>, ArgumentParser<?>> argumentParsers = new HashMap<>();

    public CommandHandler() {
        addParser(new BooleanParser());
        addParser(new BooleanParser(), Boolean.TYPE);
        addParser(new DoubleParser());
        addParser(new DoubleParser(), Double.TYPE);
        addParser(new FloatParser());
        addParser(new FloatParser(), Float.TYPE);
        addParser(new IntegerParser());
        addParser(new IntegerParser(), Integer.TYPE);
        addParser(new StringParser());
    }

    public List<RegisteredCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public void addParser(ArgumentParser<?> parser, Class<?> klass) {
        argumentParsers.put(klass, parser);
    }

    public void addParser(ArgumentParser<?> parser) {
        addParser(parser, parser.typeClass);
    }

    public void addCommands() {
        registerCommand(new DiscordCommand());
        registerCommand(new ModuleCommand());

        registerCommand(new HelpCommand());
    }

    public void addDevCommands() {
        registerCommand(new TestCommand());
    }

    public void registerCommand(Object cmd) {
        commands.add(new RegisteredCommand(cmd));
    }

    public boolean execute(String string) {
        String raw = string.substring(JesusClient.instance.config.commandPrefix.getValue().length());
        String[] splitCommand = raw.split(" ");

        if (splitCommand.length == 0) return false;

        String commandName = splitCommand[0];
        RegisteredCommand command = commands.stream().filter(c -> matchCmdName(c, commandName)).findFirst().orElse(null);

        if (command == null) {
            ChatUtils.sendPrefixMessage(commandName + " is not a valid command. Run " + JesusClient.instance.config.commandPrefix.getValue() + "help for a list of commands");
            return false;
        }

        try {
            String[] args = new String[splitCommand.length - 1];
            System.arraycopy(splitCommand, 1, args, 0, splitCommand.length - 1);

            executeCommand(command, args);
        } catch (CommandException e) {
            ChatUtils.sendPrefixMessage(e.getMessage(), ChatColor.RED);
            e.printStackTrace();
        }

        return true;
    }

    public Collection<String> autoComplete(String current) {
        String raw = current.substring(JesusClient.instance.config.commandPrefix.getValue().length());
        String[] split = raw.split(" ");

        List<String> autoCompletions = new ArrayList<>();

        RegisteredCommand command = split.length >= 1 ? commands.stream().filter(c -> matchCmdName(c, split[0])).findFirst().orElse(null) : null;

        if (split.length >= 2 || command != null && current.endsWith(" ")) {
            if (command == null) return autoCompletions;

            String[] args = new String[split.length - 1];

            System.arraycopy(split, 1, args, 0, split.length - 1);

            List<String> autoCompleted = autoCompleteCommand(command, args);

            return autoCompleted == null ? new ArrayList<>() : autoCompleted;
        } else if (split.length == 1) {
            for (RegisteredCommand c : commands) autoCompletions.addAll(c.getNameAndAliases());

            return autoCompletions.stream().map(str -> JesusClient.instance.config.commandPrefix.getValue() + str).filter(str -> str.toLowerCase().startsWith(current.toLowerCase())).collect(Collectors.toList());
        }

        return autoCompletions;
    }

    private List<String> autoCompleteCommand(RegisteredCommand command, String[] args) {
        List<String> opts = new ArrayList<>();
        Pair<String[], InternalCommand> found = getCommand(command, args);

        try {
            if (found != null) {
                Parameter currentParam = found.second.method.getParameters()[found.first.length - 1];
                appendToOptions(opts, currentParam);
                opts.addAll(argumentParsers.get(currentParam.getType()).autoComplete(args[args.length - 1], currentParam));
            }

            opts.addAll(getApplicableOptsFor(command, args));
        } catch (Exception ignored) {
        }

        return opts;
    }

    private void executeCommand(RegisteredCommand command, String[] args) {
        String[] result = doCommand(command, args);
        if (result.length != 0 && result[0] != null) {
            for (String s : result) {
                ChatUtils.sendMessage(s);
            }
        }
    }

    private String[] doCommand(RegisteredCommand command, String[] args) {
        if (args.length == 0) {
            if (command.entry != null) return new String[] { command.entry.invoke(null) };
            else return command.helpCommand;
        } else if (args[0].equalsIgnoreCase("help")) {
            if (args.length == 1) {
                return command.helpCommand;
            } else {
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                Pair<String[], InternalCommand> found = getCommand(command, newArgs);

                return command.getAdvancedHelp(found == null ? null : found.second);
            }
        } else {
            Pair<String[], InternalCommand> found = getCommand(command, args);

            if (found != null) {
                return new String[] { found.second.invoke(found.first) };
            }
        }

        return new String[] { "Command not found. Do " + JesusClient.instance.config.commandPrefix.getValue() + command.meta.value() + " help for help" };
    }

    private Pair<String[], InternalCommand> getCommand(RegisteredCommand root, String[] args) {
        String argsIn = String.join(DELIMITER, args).toLowerCase();

        for (int i = args.length - 1; i >= 0; i--) {
            InternalCommand command = getCommand(root, argsIn);
            if (command != null) {
                String primaryPath = command.getPrimaryPath()
                        .replace(DELIMITER + ENTRY_METHOD_NAME, "")
                        .replace(ENTRY_METHOD_NAME, "");

                int skipArgs = 0;
                if (!primaryPath.isEmpty()) skipArgs++;

                for (char c : primaryPath.toCharArray()) {
                    if (c == DELIMITER.toCharArray()[0]) skipArgs++;
                }

                String[] newArgs = new String[args.length - skipArgs];
                System.arraycopy(args, skipArgs, newArgs, 0, args.length - skipArgs);

                return new Pair<>(newArgs, command);
            }

            argsIn = StringUtils.substringToLastIndexOf(argsIn, DELIMITER);
        }

        return null;
    }

    private InternalCommand getCommand(RegisteredCommand command, String in) {
        for (String[] strings : command.commands.values()) {
            for (String string : strings) {
                if (string.equalsIgnoreCase(in) || string.equalsIgnoreCase(in + DELIMITER + ENTRY_METHOD_NAME)) {
                    return command.commands.entrySet().stream()
                            .filter(it -> it.getValue() == strings)
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse(null);
                }
            }
        }

        String[] argsIn = in.toLowerCase().split(DELIMITER);
        if (getApplicableOptsFor(command, argsIn).isEmpty()) {
            Pair<String, InternalCommand> fallbackCommand = getFallback(command, in);
            if (fallbackCommand != null) {
                return fallbackCommand.second;
            }
        }

        return null;
    }

    private Collection<String> getApplicableOptsFor(RegisteredCommand command, String[] args) {
        Set<String> opts = new HashSet<>();
        String current = String.join(DELIMITER, args);

        for (String[] paths : command.commands.values()) {
            for (String path : paths) {
                if (path.endsWith(ENTRY_METHOD_NAME)) continue;
                if (!path.startsWith(current)) continue;

                String[] split = path.split(DELIMITER);

                if (args.length - 1 < split.length) {
                    String s = split[args.length - 1];
                    if (s.isEmpty()) continue;
                    opts.add(s);
                }
            }
        }

        opts.remove("entry");
        return opts;
    }

    private void appendToOptions(List<String> opts, Parameter currentParam) {
        Argument argument = currentParam.isAnnotationPresent(Argument.class)
                ? currentParam.getAnnotation(Argument.class)
                : null;
        String[] targets = argument != null && argument.autoComplete().length != 0 ? argument.autoComplete() : null;
        if (targets != null) {
            opts.addAll(Arrays.asList(targets));
        }
    }

    private static Pair<String, InternalCommand> getFallback(RegisteredCommand command, String in) {
        in = in.trim();

        if (in.isEmpty()) {
            InternalCommand cmd = command.commands.entrySet().stream()
                    .filter(e -> Arrays.asList(e.getValue()).contains(ENTRY_METHOD_NAME))
                    .map(Map.Entry::getKey)
                    .filter(it -> it.method.getParameterCount() == 0)
                    .findFirst()
                    .orElse(null);

            if (cmd == null) {
                return null;
            }

            return new Pair<>(ENTRY_METHOD_NAME, cmd);
        }

        String[] splitData = in.split(DELIMITER);
        for (int i = splitData.length; i >= 0; i--) {
            String[] split = Arrays.copyOfRange(splitData, 0, i);
            String path = String.join(DELIMITER, split).trim();

            List<InternalCommand> commands = new ArrayList<>();
            cmdloop:
            for (Map.Entry<InternalCommand, String[]> entry : command.commands.entrySet()) {
                InternalCommand potentialCommand = entry.getKey();
                String[] acceptedPaths = entry.getValue();

                for (String cmdPath : acceptedPaths) {
                    boolean matchesPath = cmdPath.equals(path);
                    if (path.isEmpty()) matchesPath = false;

                    boolean matchesMain = cmdPath.equals(path + (path.isEmpty() ? "" : DELIMITER) + ENTRY_METHOD_NAME.toLowerCase(Locale.ROOT));
                    if (matchesPath || matchesMain) {
                        commands.add(potentialCommand);
                        continue cmdloop;
                    }
                }
            }

            for (InternalCommand command1 : commands) {
                Method method = command1.method;

                if (method.getParameterCount() == 0) {
                    continue;
                }
                if (method.getParameterCount() == splitData.length) {
                    return new Pair<>(path, command1);
                } else if (method.getParameters()[method.getParameterCount() - 1].isAnnotationPresent(Variadic.class)) {
                    return new Pair<>(path, command1);
                }
            }
        }

        return null;
    }

    private static boolean matchCmdName(RegisteredCommand command, String name) {
        if (command.meta.value().equalsIgnoreCase(name)) return true;

        for (String alias : command.meta.aliases()) {
            if (alias.equalsIgnoreCase(name)) return true;
        }

        return false;
    }

    private static String[] computePaths(InternalCommand in) {
        List<String> out = new ArrayList<>();
        for (String path : in.paths) {
            for (String alias : in.aliases) {
                out.add((path + (path.isEmpty() ? "" : DELIMITER) + alias).toLowerCase());
            }
        }
        return out.toArray(new String[0]);
    }

    private static String[] computePaths(String[] paths, Class<?> cls) {
        List<String> out = new ArrayList<>();
        SubCommandGroup annotation = cls.getAnnotation(SubCommandGroup.class);
        for (String path : paths) {
            String prefix = path + (path.isEmpty() ? "" : DELIMITER);
            for (String alias : annotation.aliases()) {
                out.add((prefix + alias).toLowerCase());
            }
            out.add((prefix + annotation.value()).toLowerCase());
        }
        return out.toArray(new String[0]);
    }

    public class RegisteredCommand {
        final Map<InternalCommand, String[]> commands = new HashMap<>();
        final String[] helpCommand;
        public final Command meta;
        InternalCommand entry;

        RegisteredCommand(Object commandObj) {
            Class<?> klass = commandObj.getClass();

            if (klass.isAnnotationPresent(Command.class)) {
                meta = klass.getAnnotation(Command.class);

                for (Method method : klass.getDeclaredMethods()) {
                    create(EMPTY_ARRAY, commandObj, method);
                }

                for (Class<?> subcommandGroup : klass.getDeclaredClasses()) {
                    if (!subcommandGroup.isAnnotationPresent(SubCommandGroup.class)) continue;
                    walk(EMPTY_ARRAY, ReflectionUtils.instantiateInnerClass(subcommandGroup, commandObj));
                }

                if (meta.helpMessage().length == 0)
                    helpCommand = genHelpCommand();
                else
                    helpCommand = meta.helpMessage();
            } else {
                throw new IllegalArgumentException("Command class " + klass.getSimpleName() + " is not annotated with @Command");
            }
        }

        List<String> getNameAndAliases() {
            List<String> names = new ArrayList<>(meta.aliases().length + 1);

            names.add(meta.value());
            Collections.addAll(names, meta.aliases());

            return names;
        }

        void create(String[] parentPaths, Object parent, Method method) {
            if (parent.getClass().equals(Class.class)) return;
            if (!method.isAccessible()) method.setAccessible(true);
            if (!method.isAnnotationPresent(SubCommand.class)) {
                if (method.isAnnotationPresent(Entry.class)) {
                    if (entry == null) {
                        if (Arrays.equals(parentPaths, EMPTY_ARRAY) && method.getParameterCount() == 0) {
                            entry = new InternalCommand(parent, method, parentPaths);
                        } else {
                            Method[] methods = method.getDeclaringClass().getDeclaredMethods();
                            int entries = (int) Stream.of(methods).filter(m -> m.isAnnotationPresent(Entry.class)).count();

                            if (entries == 1) {
                                entry = new InternalCommand(parent, method, parentPaths);
                            }
                        }
                    }
                } else return;
            }

            InternalCommand internalCommand = new InternalCommand(parent, method, parentPaths);
            if (commands.keySet().stream().anyMatch(internalCommand::equals)) {
                throw new IllegalArgumentException("Command " + method.getName() + " is already registered");
            }

            commands.put(internalCommand, computePaths(internalCommand));
        }

        void walk(String[] paths, Object self) {
            Class<?> classIn = self.getClass();
            paths = computePaths(paths, classIn);

            for (Method method : classIn.getDeclaredMethods()) {
                create(paths, self, method);
            }

            for (Class<?> klass : classIn.getDeclaredClasses()) {
                if (!klass.isAnnotationPresent(SubCommandGroup.class)) continue;

                Object subcommand = ReflectionUtils.instantiateInnerClass(klass, self);
                walk(paths, subcommand);
            }
        }

        String[] genHelpCommand() {
            String name = meta.value();
            StringBuilder sb = new StringBuilder(200);

            sb.append(ChatColor.GOLD).append(ChatColor.BOLD).append("Help for '").append(JesusClient.instance.config.commandPrefix.getValue()).append(name).append("'").append(ChatColor.RESET).append(ChatColor.GOLD);

            if (!meta.description().isEmpty()) sb.append(" - ").append(meta.description());
            if (meta.aliases().length > 0) sb.append(":           ").append(Arrays.toString(meta.aliases()));
            sb.append("\n").append(ChatColor.GOLD);

            for(Iterator<InternalCommand> it = commands.keySet().stream().sorted().iterator(); it.hasNext();) {
                InternalCommand command = it.next();
                String path;

                if (command.getPrimaryPath().endsWith(ENTRY_METHOD_NAME)) {
                    Entry entry = command.method.isAnnotationPresent(Entry.class) ? command.method.getAnnotation(Entry.class) : null;

                    path = command.getPrimaryPath().substring(0, command.getPrimaryPath().length() - ENTRY_METHOD_NAME.length()).replaceAll(DELIMITER, " ").trim();
                    sb.append(JesusClient.instance.config.commandPrefix.getValue()).append(name).append(path.isEmpty() ? "" : " ").append(path).append(" ");

                    for (Parameter parameter : command.method.getParameters()) {
                        appendParameter(sb, parameter);
                    }

                    sb.append("- ").append(entry != null && !entry.description().isEmpty() ? entry.description() : "Main command").append("\n").append(ChatColor.GOLD);
                    continue;
                }

                path = command.getPrimaryPath().replaceAll(DELIMITER, " ");
                sb.append(JesusClient.instance.config.commandPrefix.getValue()).append(name).append(" ").append(path).append(" ");

                for (Parameter parameter : command.method.getParameters()) {
                    appendParameter(sb, parameter);
                }

                if (command.hasHelp) sb.append("- ").append(command.getHelp());
                sb.append("\n").append(ChatColor.GOLD);
            }

            return sb.toString().split("\n");
        }

        void appendParameter(StringBuilder sb, Parameter parameter) {
            String s = parameter.isAnnotationPresent(Argument.class) ?
                    parameter.getAnnotation(Argument.class).value() : parameter.getType().getSimpleName();

            sb.append("<").append(s);

            if (parameter.getType().isArray() || parameter.isAnnotationPresent(Variadic.class))
                sb.append("...");

            sb.append("> ");
        }

        String[] getAdvancedHelp(InternalCommand command) {
            if (command != null) {
                StringBuilder sb = new StringBuilder(200);
                
                sb.append(ChatColor.GOLD).append(ChatColor.BOLD).append("Advanced help for ").append(JesusClient.instance.config.commandPrefix.getValue()).append(meta.value()).append(" ").append(command.getPrimaryPath().replaceAll(DELIMITER, " "));
                sb.append(ChatColor.RESET).append(ChatColor.GOLD).append(": ").append("\n").append(ChatColor.GOLD);
                
                if (command.hasHelp) {
                    sb.append(ChatColor.BOLD).append("Description: ").append(ChatColor.RESET).append(ChatColor.GOLD).append(command.getHelp())
                            .append("\n").append(ChatColor.GOLD);
                }
                if (command.aliases.length > 0) {
                    sb.append("Aliases: ").append(String.join(", ", command.aliases)).append("\n").append(ChatColor.GOLD);
                }
                sb.append("Parameters:\n").append(ChatColor.GOLD);
                for (Parameter parameter : command.method.getParameters()) {
                    Argument argument = parameter.isAnnotationPresent(Argument.class) ? parameter.getAnnotation(Argument.class) : null;
                    String s = argument != null ? argument.value() : parameter.getType().getSimpleName();
                    sb.append("<").append(s);
                    if (parameter.getType().isArray() || parameter.isAnnotationPresent(Variadic.class)) {
                        sb.append("...");
                    }
                    sb.append(">");
                    String desc = argument != null && !argument.description().isEmpty() ? argument.description() : null;
                    sb.append(desc != null ? ": " + desc : "\n").append(ChatColor.GOLD);
                }
                return sb.toString().split("\n");
            } else return new String[]{ ChatColor.GOLD + "Could not find help for command. Try running " + JesusClient.instance.config.commandPrefix.getValue() + meta.value() + " help for more generic help" };
        }
    }

    private class InternalCommand implements Comparable<InternalCommand> {
        final Method method;
        final SubCommand meta;
        final String[] aliases, paths;
        final boolean hasHelp;
        final Object parent;

        InternalCommand(Object parent, Method methodIn, String[] paths) {
            this.parent = parent;
            if (!methodIn.isAccessible()) methodIn.setAccessible(true);

            this.method = methodIn;
            this.meta = methodIn.isAnnotationPresent(SubCommand.class) ? methodIn.getAnnotation(SubCommand.class) : null;
            this.hasHelp = meta != null && !meta.description().isEmpty();

            this.aliases = new String[meta != null ? meta.aliases().length + 1 : 1];
            if (meta != null) {
                aliases[0] = methodIn.getName();
                System.arraycopy(meta.aliases(), 0, aliases, 1, meta.aliases().length);
            } else {
                aliases[0] = ENTRY_METHOD_NAME;
            }
            this.paths = paths;

            int i = 0;
            for (Parameter parameter : method.getParameters()) {
                if (!argumentParsers.containsKey(parameter.getType())) {
                    throw new IllegalArgumentException("Method " + method.getName() + " has a parameter of type " + parameter.getType().getSimpleName() + " which does not have a valid parser");
                }
                if (parameter.isAnnotationPresent(Variadic.class) && i != method.getParameters().length - 1) {
                    throw new IllegalArgumentException("Method " + method.getName() + " has a variadic parameter " + parameter.getName());
                }
                i++;
            }
        }

        String getName() {
            return aliases[0];
        }

        String getPrimaryPath() {
            return paths[0] + (paths[0].isEmpty() ? "" : DELIMITER) + aliases[0];
        }

        String getHelp() {
            if (hasHelp)
                return meta.description();
            else
                return null;
        }

        String invoke(String[] args) {
            try {
                if (args == null) {
                    method.invoke(parent);
                    return null;
                }

                if ((args.length != method.getParameterCount()) && (method.getParameterCount() == 0 || !method.getParameters()[method.getParameterCount() - 1].isAnnotationPresent(Variadic.class))) {
                    return ChatColor.RED + "Incorrect number of parameters, expected " + method.getParameterCount() + ", but got " + args.length;
                }

                return invokeWith(method, args);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                return ChatColor.RED + "Error while invoking method " + getName() + ". Check your logs for more details";
            } catch (CommandException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        String invokeWith(Method method, String[] args) throws InvocationTargetException, IllegalAccessException {
            Object[] methodArgs = new Object[method.getParameterCount()];
            Parameter[] parameters = method.getParameters();
            int i = 0;

            for (Parameter parameter : parameters) {
                try {
                    if (i == args.length - 1 && parameter.isAnnotationPresent(Variadic.class)) {
                        methodArgs[i] = Arrays.stream(args).skip(i).collect(Collectors.joining(" "));
                    } else {
                        methodArgs[i] = argumentParsers.get(parameter.getType()).parse(args[i]);
                    }
                } catch (NumberFormatException e) {
                    return ChatColor.RED + "Error while parsing parameter '" + args[i] + "': Parameter should be a number!";
                } catch (Exception e) {
                    e.printStackTrace();
                    return ChatColor.RED + "Error while parsing parameter '" + args[i] + "': " + e.getMessage();
                }

                i++;
            }

            method.invoke(parent, methodArgs);
            return null;
        }

        @Override
        public int compareTo(InternalCommand cmd) {
            return this.getPrimaryPath().compareTo(cmd.getPrimaryPath());
        }
    }
}
