package net.elysium.VillageBuilder.Commands;

import net.elysium.VillageBuilder.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ResourceAssignmentCommand implements CommandExecutor {
    private final Main plugin;

    public ResourceAssignmentCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (args.length < 3) {
            player.sendMessage("Використання: /assign-resources <назва_структури> <порядковий_номер_стадії> <список_ресурсів>");
            return false;
        }

        String structureName = args[0];
        int stageNumber;

        try {
            stageNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Порядковий номер стадії повинен бути числом.");
            return false;
        }

        String resourcePath = "structures." + structureName + ".stages." + stageNumber;
        if (plugin.getConfig().getConfigurationSection(resourcePath) == null) {
            player.sendMessage("Стадія або структура не знайдені.");
            return false;
        }

        String[] resourceInputs = Arrays.copyOfRange(args, 2, args.length);
        List<String> resources = Arrays.asList(resourceInputs);

        plugin.getConfig().set(resourcePath + ".resources", resources);
        plugin.saveConfig();

        player.sendMessage("Ресурси " + resources + " додані до стадії " + stageNumber + " структури '" + structureName + "'.");
        return true;
    }
}
