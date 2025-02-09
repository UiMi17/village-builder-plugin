package net.elysium.VillageBuilder.Commands.Services;

import net.elysium.VillageBuilder.Commands.PositionSelectionCommand;
import net.elysium.VillageBuilder.Main;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class StructureManager {
    private final Main plugin;
    private final SelectionManager selectionManager;
    private final PositionSelectionCommand positionSelectionCommand;

    public StructureManager(SelectionManager selectionManager, PositionSelectionCommand positionSelectionCommand, Main plugin) {
        this.selectionManager = selectionManager;
        this.positionSelectionCommand = positionSelectionCommand;
        this.plugin = plugin;
    }

    public void addStructureStage(Player player, String stageName, String structureName) {
        Location[] selection = selectionManager.getSelection(player);
        if (selection[0] == null || selection[1] == null) {
            player.sendMessage("Спершу виділіть область за допомогою команд /pos 1 та /pos 2.");
            return;
        }

            if (!plugin.getConfig().isConfigurationSection("structures." + structureName)) {
                player.sendMessage("Структура '" + structureName + "' не знайдена.");
                return;
            }

            ConfigurationSection structureSection = plugin.getConfig().getConfigurationSection("structures." + structureName);

        assert structureSection != null;
        ConfigurationSection stagesSection = structureSection.getConfigurationSection("stages");
            if (stagesSection == null) {
                stagesSection = structureSection.createSection("stages");
            }

            int nextStageNumber = stagesSection.getKeys(false).size();
            String stagePath = "structures." + structureName + ".stages." + nextStageNumber;

            saveLocation(stagePath + ".pos1", selection[0]);
            saveLocation(stagePath + ".pos2", selection[1]);
            plugin.getConfig().set(stagePath + ".isCurrentStage", false);
            plugin.getConfig().set(stagePath + ".name", stageName);

            plugin.saveConfig();

            selectionManager.clearSelection(player);
            positionSelectionCommand.cancelParticleTask(player);

            player.sendMessage("Стадія '" + stageName + "' додана до структури '" + structureName + "' з порядковим номером " + nextStageNumber + ".");
    }

    private void saveLocation(String path, Location location) {
        plugin.getConfig().set(path + ".x", location.getBlockX());
        plugin.getConfig().set(path + ".y", location.getBlockY());
        plugin.getConfig().set(path + ".z", location.getBlockZ());
    }
}
