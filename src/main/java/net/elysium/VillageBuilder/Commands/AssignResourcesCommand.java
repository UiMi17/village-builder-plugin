package net.elysium.VillageBuilder.Commands;

import net.elysium.VillageBuilder.Main;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AssignResourcesCommand implements CommandExecutor {
    private final Main plugin;

    public AssignResourcesCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Тільки гравці можуть використовувати цю команду.");
            return true;
        }

        if (args.length != 4) {
            sender.sendMessage("Використання: /assign-resources <назва_структури> <стадія> <тип_ресурсу> <кількість>");
            return true;
        }

        String structureName = args[0];
        String stageId = args[1];
        String resourceType = args[2].toUpperCase();
        int resourceLimit;

        try {
            resourceLimit = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Кількість має бути числом.");
            return true;
        }

        Material material = Material.getMaterial(resourceType);
        if (material == null) {
            sender.sendMessage("Недійсний тип ресурсу: " + resourceType);
            return true;
        }

        if (!plugin.getConfig().isConfigurationSection("structures." + structureName + ".stages." + stageId)) {
            sender.sendMessage("Структура або стадія не знайдені.");
            return true;
        }

        String resourcePath = "structures." + structureName + ".stages." + stageId + ".resources." + resourceType;
        plugin.getConfig().set(resourcePath, resourceLimit);
        plugin.saveConfig();

        sender.sendMessage("Ресурс " + resourceType + " з лімітом " + resourceLimit + " успішно додано до стадії " + stageId);
        return true;
    }
}
