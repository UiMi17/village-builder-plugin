package net.elysium.VillageBuilder.Commands;

import net.elysium.VillageBuilder.Main;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class CreateStructureCommand implements CommandExecutor {
    private final SelectionManager selectionManager;
    private final PositionSelectionCommand positionSelectionCommand;
    private final Main plugin;

    public CreateStructureCommand(SelectionManager selectionManager, PositionSelectionCommand positionSelectionCommand, Main plugin) {
        this.selectionManager = selectionManager;
        this.positionSelectionCommand = positionSelectionCommand;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Ця команда доступна лише для гравців!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("Правильний формат: /createstructure [назва_структури]");
            return true;
        }

        String structureName = args[0];
        Location[] positions = selectionManager.getSelection(player);
        if (positions == null || positions[0] == null || positions[1] == null) {
            player.sendMessage("Спершу виділіть область командою /pos 1 і /pos 2.");
            return true;
        }

        saveStructureData(player, structureName, positions[0], positions[1]);
        player.sendMessage("Структура '" + structureName + "' успішно збережена!");
        return true;
    }

    private void saveStructureData(Player player, String structureName, Location pos1, Location pos2) {
        ConfigurationSection structuresSection = plugin.getConfig().createSection("structures." + structureName);

        structuresSection.set("pos1.x", pos1.getBlockX());
        structuresSection.set("pos1.y", pos1.getBlockY());
        structuresSection.set("pos1.z", pos1.getBlockZ());
        structuresSection.set("pos2.x", pos2.getBlockX());
        structuresSection.set("pos2.y", pos2.getBlockY());
        structuresSection.set("pos2.z", pos2.getBlockZ());

        plugin.saveConfig();

        selectionManager.clearSelection(player);
        positionSelectionCommand.cancelParticleTask(player);
    }
}
