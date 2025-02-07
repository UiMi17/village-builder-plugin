package net.elysium.VillageBuilder.Commands;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class CreateStructureCommand implements CommandExecutor {
    private final SelectionManager selectionManager;
    private final PositionSelectionCommand positionSelectionCommand;
    private final File dataFile;
    private final YamlConfiguration dataConfig;

    public CreateStructureCommand(SelectionManager selectionManager, PositionSelectionCommand positionSelectionCommand, File pluginDataFolder) {
        this.selectionManager = selectionManager;
        this.positionSelectionCommand = positionSelectionCommand;
        this.dataFile = new File(pluginDataFolder, "data.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
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
        dataConfig.set("structures." + structureName + ".pos1.x", pos1.getBlockX());
        dataConfig.set("structures." + structureName + ".pos1.y", pos1.getBlockY());
        dataConfig.set("structures." + structureName + ".pos1.z", pos1.getBlockZ());
        dataConfig.set("structures." + structureName + ".pos2.x", pos2.getBlockX());
        dataConfig.set("structures." + structureName + ".pos2.y", pos2.getBlockY());
        dataConfig.set("structures." + structureName + ".pos2.z", pos2.getBlockZ());

        try {
            dataConfig.save(dataFile);
            selectionManager.clearSelection(player);
            positionSelectionCommand.cancelParticleTask(player);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
