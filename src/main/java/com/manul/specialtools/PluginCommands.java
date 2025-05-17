package com.manul.specialtools;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PluginCommands implements CommandExecutor, TabCompleter {

    private final SpecialTools plugin;

    public PluginCommands(SpecialTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> helpLines = plugin.getLangList("help_message");

        if (args.length == 0) {
            sender.sendMessage(helpLines.toArray(new String[0]));
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "help":
                if (!sender.hasPermission("specialtools.command.help") && !(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(plugin.getLang("permission_error"));
                return true;
            }
                sender.sendMessage(helpLines.toArray(new String[0]));
                return true;
            case "give":
                return handleGive(sender, args);
            case "reload":
                return handleReload(sender);
            case "info":
                sender.sendMessage("");
                sender.sendMessage("");
                sender.sendMessage("§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-");
                sender.sendMessage("§c§lSpecial§f§lTools §f§lv§c§l1§f§l.§c§l0§r §fby §e§lmanulishere");
                sender.sendMessage("§f- §fCreated on §c16§f/§c05§f/§c25");
                sender.sendMessage("§f- §fFor §6§lPaperSpigot§r §c§l1§f§l.§c§l21§f§l+");
                sender.sendMessage("§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-§c-§f-");
                sender.sendMessage("");
                sender.sendMessage("");
                return true;
            case "pickaxe":
            case "shovel":
            case "axe":
                sender.sendMessage("");
                sender.sendMessage("f§l[§c§lSpecial§f§lTools§f§l]§r §cTry: §c/specialtools §6give§6 " + sub + " §c§l[§6§lPLAYER§c§l]");
                sender.sendMessage("");
                return true;
            default:
                sender.sendMessage(helpLines.toArray(new String[0]));
                return true;
        }
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("specialtools.command.give") && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(plugin.getLang("permission_error"));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage("");
            sender.sendMessage("§f§l[§c§lSpecial§f§lTools§f§l]§r §cTry: §c/specialtools §6give");
            sender.sendMessage("§c[§6pickaxe§c | §6shovel§c | §6axe§c] §c[§6player§c]");
            sender.sendMessage("");
            return true;
        }

        String item = args[1].toLowerCase();
        Player target = Bukkit.getPlayerExact(args[2]);
        if (target == null) {
            sender.sendMessage(plugin.getLang("noplayer_error"));
            return true;
        }

        ItemStack tool = null;
        String itemName = "";
        switch (item) {
            case "pickaxe":
                SpecialPickAxe pickAxe = new SpecialPickAxe(plugin);
                tool = pickAxe.createPickaxe();
                itemName = plugin.getLang("pickaxe_name");
                break;
            case "shovel":
                SpecialShovel shovel = new SpecialShovel(plugin);
                tool = shovel.createShovel();
                itemName = plugin.getLang("shovel_name");
                break;
            case "axe":
                SpecialAxe axe = new SpecialAxe(plugin);
                tool = axe.createAxe();
                itemName = plugin.getLang("axe_name");
                break;
            default:
                sender.sendMessage("");
                sender.sendMessage("§f§l[§c§lSpecial§f§lTools§f§l]§r §cAvailable Items: §c[§6pickaxe§c | §6shovel§c | §6axe§c]");
                sender.sendMessage("");
                return true;
        }

        target.getInventory().addItem(tool);
        sender.sendMessage(plugin.getLang("give_success").replace("%item%", itemName).replace("%player%", target.getName()));
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("specialtools.command.reload") && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(plugin.getLang("permission_error"));
            return true;
    }
            plugin.reloadPluginConfigAndListeners();
            sender.sendMessage(plugin.getLang("reload_success"));
            return true;
}
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> subs = List.of("give", "reload", "info", "help");
            for (String s : subs) {
                if (s.startsWith(args[0].toLowerCase())) completions.add(s);
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            List<String> items = List.of("pickaxe", "shovel", "axe");
            for (String s : items) {
                if (s.startsWith(args[1].toLowerCase())) completions.add(s);
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(p.getName());
                }
            }
        }
        return completions;
    }
}
