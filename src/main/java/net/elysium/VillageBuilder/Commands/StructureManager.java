package net.elysium.VillageBuilder.Commands;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class StructureManager {
    private final File dataFile;
    private final YamlConfiguration config;
    private final SelectionManager selectionManager;
    private final PositionSelectionCommand positionSelectionCommand;

    public StructureManager(SelectionManager selectionManager, PositionSelectionCommand positionSelectionCommand) {
        this.selectionManager = selectionManager;
        this.positionSelectionCommand = positionSelectionCommand;
        this.dataFile = new File("plugins/VillageBuilder", "data.yml");
        this.config = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void addStructureStage(Player player, String stageName, String structureName) {
        Location[] selection = selectionManager.getSelection(player);
        if (selection[0] == null || selection[1] == null) {
            player.sendMessage("Спершу виділіть область за допомогою команд /pos 1 та /pos 2.");
            return;
        }

        String structurePath = "structures." + structureName;
        if (!Objects.requireNonNull(config.getString(structurePath)).contains(structurePath)) {
            player.sendMessage("Структура '" + structureName + "' не знайдена.");
            return;
        }

        int nextStageNumber = 0;
        String path = config.getString(structurePath + ".stages");
        if (path != null) {
            nextStageNumber = Objects.requireNonNull(config.getConfigurationSection(structurePath + ".stages")).getKeys(false).size();
        }

        String stagePath = structurePath + ".stages." + nextStageNumber;

        saveLocation(stagePath + ".pos1", selection[0]);
        saveLocation(stagePath + ".pos2", selection[1]);

        config.set(stagePath + ".name", stageName);

        saveData();
        selectionManager.clearSelection(player);
        positionSelectionCommand.cancelParticleTask(player);
        player.sendMessage("Стадія '" + stageName + "' додана до структури '" + structureName + "' з порядковим номером " + nextStageNumber + ".");
    }

    private void saveLocation(String path, Location location) {
        config.set(path + ".x", location.getBlockX());
        config.set(path + ".y", location.getBlockY());
        config.set(path + ".z", location.getBlockZ());
    }

    private void saveData() {
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
