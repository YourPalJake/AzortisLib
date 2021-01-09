/*
 * MIT License
 *
 * Copyright (c) 2021 Azortis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.azortis.azortislib.command;

import com.azortis.azortislib.command.builders.SubCommandBuilder;
import com.azortis.azortislib.command.executors.ICommandExecutor;
import com.azortis.azortislib.command.executors.ITabCompleter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Command {

    //Command information
    private final String name;
    private String description;
    private String usage;
    private List<String> aliases;
    private String permission;
    private Plugin plugin;

    //Execution classes
    private final ICommandExecutor executor;
    private ITabCompleter tabCompleter;
    private final org.bukkit.command.Command bukkitCommand;
    private Collection<SubCommand> subCommands;
    private Map<String, Alias> aliasesMap;

    /**
     * Create a command instance to be registered using {@link CommandInjector}
     *
     * @param name         the command name.
     * @param description  the command description.
     * @param usage        the command usage.
     * @param rawAliases   list of aliases can include ones that have functions inside of them.
     * @param permission   the command permission.
     * @param plugin       the plugin that the command belongs to.
     * @param executor     the executor class of the command.
     * @param tabCompleter the tabCompleter class of the command.
     * @param subCommands  a list of {@link SubCommandBuilder} to be build.
     */
    public Command(@NotNull String name, String description, String usage, List<String> rawAliases, String permission, Plugin plugin
            , @NotNull ICommandExecutor executor, ITabCompleter tabCompleter, Collection<SubCommandBuilder> subCommands) {
        this.name = name.toLowerCase();
        this.executor = executor;
        if (plugin == null) {
            this.bukkitCommand = new BukkitCommand(this.name, this);
        } else {
            this.bukkitCommand = new BukkitPluginCommand(name, this, plugin);
            this.plugin = plugin;
        }
        if (description != null) {
            this.description = description;
            bukkitCommand.setDescription(description);
            bukkitCommand.setUsage(usage);
        }
        if (usage != null) {
            this.usage = usage;
        }
        if (rawAliases != null) {
            List<String> processedAliases = new ArrayList<>();
            for (String rawAlias : rawAliases) {
                if (!rawAlias.contains("-f")) {
                    processedAliases.add(rawAlias.toLowerCase().trim());
                    break;
                    //I am going to keep this note here, for my future self to understand my current stupidity!
                    //This was a return one, and that's why I wasted a whole day finding it out!
                }
                if (this.aliasesMap == null) this.aliasesMap = new HashMap<>();
                Alias aliasFunction = new Alias(rawAlias);
                this.aliasesMap.put(aliasFunction.getAlias(), aliasFunction);
                processedAliases.add(aliasFunction.getAlias().toLowerCase());
            }
            this.aliases = processedAliases;
            bukkitCommand.setAliases(aliases);
        }
        if (permission != null) {
            this.permission = permission;
            bukkitCommand.setPermission(permission);
        }
        if (tabCompleter != null) this.tabCompleter = tabCompleter;
        if (subCommands != null) {
            this.subCommands = new ArrayList<>();
            subCommands.forEach(subCommand -> this.subCommands.add(subCommand.setParent(this).build()));
        }
    }

    /**
     * Called when the command is dispatched by bukkit.
     *
     * @param commandSender the command sender.
     * @param label         the label used.
     * @param args          the command arguments.
     * @return successful
     */
    private boolean executeCommand(CommandSender commandSender, String label, String[] args) {
        if (args.length >= 1 && subCommands != null) {
            for (SubCommand subCommand : subCommands) {
                if (args[0].equalsIgnoreCase(subCommand.getName()) || (subCommand.hasAliases() && subCommand.getAliases().contains(args[0].toLowerCase()))) {
                    List<String> argsList = new LinkedList<>(Arrays.asList(args));
                    argsList.remove(0);
                    String[] subArgs = argsList.toArray(new String[0]);
                    return subCommand.execute(commandSender, label, subArgs);
                }
            }
        }
        return executor.onCommand(commandSender, this, label, args);
    }

    /**
     * Get a single name
     *
     * @param alias the alias
     * @return the {@link Alias}
     */
    @Nullable
    public Alias getAlias(String alias) {
        return aliasesMap.get(alias);
    }

    /**
     * The {@link org.bukkit.command.Command} class if no plugin is specified.
     */
    private class BukkitCommand extends org.bukkit.command.Command {
        private final Command parent;

        BukkitCommand(String name, Command parent) {
            super(name);
            this.parent = parent;
        }

        public boolean execute(@NotNull CommandSender commandSender, @NotNull String label, String[] args) {
            return executeCommand(commandSender, label, args);
        }

        @Override
        public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, String[] args, Location location) throws IllegalArgumentException {
            if (parent.tabCompleter != null)
                return parent.tabCompleter.onTabComplete(commandSender, parent, alias, args, location);
            return new ArrayList<>();
        }

    }

    /**
     * Get the command name
     *
     * @return the command name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Get the command description
     *
     * @return the command description
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Get the command usage
     *
     * @return the command usage
     */
    @Nullable
    public String getUsage() {
        return usage;
    }

    /**
     * Get a the list of aliases
     *
     * @return the command aliases
     */
    @Nullable
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Get the command permission
     *
     * @return the command permission
     */
    @Nullable
    public String getPermission() {
        return permission;
    }

    /**
     * Get the plugin which the command belongs to
     *
     * @return the plugin
     */
    @Nullable
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Get the executor class
     *
     * @return the executor class
     */
    @NotNull
    public ICommandExecutor getExecutor() {
        return executor;
    }

    /**
     * Get the tab completer class
     *
     * @return the tab completer class
     */
    @Nullable
    public ITabCompleter getTabCompleter() {
        return tabCompleter;
    }

    /**
     * Get the {@link org.bukkit.command.Command} to be registered using the {@link CommandInjector}
     *
     * @return the bukkit command
     */
    @NotNull
    public org.bukkit.command.Command getBukkitCommand() {
        return bukkitCommand;
    }

    /**
     * Get the list of subCommands
     *
     * @return the list of subCommands
     */
    @Nullable
    public Collection<SubCommand> getSubCommands() {
        return subCommands;
    }

    /**
     * Get the {@link Alias} map
     *
     * @return the alias map
     */
    @Nullable
    public Map<String, Alias> getAliasesMap() {
        return aliasesMap;
    }

    /**
     * The {@link org.bukkit.command.Command} class is a plugin is specified.
     */
    private class BukkitPluginCommand extends org.bukkit.command.Command implements PluginIdentifiableCommand {
        private final Command parent;
        private final Plugin plugin;

        public BukkitPluginCommand(String name, Command parent, Plugin plugin) {
            super(name);
            this.parent = parent;
            this.plugin = plugin;
        }

        public boolean execute(@NotNull CommandSender commandSender, String label, String[] args) {
            return executeCommand(commandSender, label, args);
        }


        @Override
        public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, String[] args, Location location) throws IllegalArgumentException {
            if (parent.tabCompleter != null)
                return parent.tabCompleter.onTabComplete(commandSender, parent, alias, args, location);
            return new ArrayList<>();
        }

        @Override
        public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, String[] args) throws IllegalArgumentException {
            if (parent.tabCompleter != null)
                return parent.tabCompleter.onTabComplete(commandSender, parent, alias, args, null);
            return new ArrayList<>();
        }

        public @NotNull Plugin getPlugin() {
            return this.plugin;
        }

    }
}
