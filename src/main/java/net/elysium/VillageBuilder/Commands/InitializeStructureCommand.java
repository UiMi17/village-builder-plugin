package net.elysium.VillageBuilder.Commands;

import net.elysium.VillageBuilder.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class InitializeStructureCommand implements CommandExecutor {
    Main plugin;

    public InitializeStructureCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Використання: /initialize-structure <назва_структури> [номер_стадії]");
            return false;
        }

        String structureName = args[0];
        String structurePath = "structures." + structureName;

        if (!plugin.getConfig().contains(structurePath)) {
            sender.sendMessage("Структура '" + structureName + "' не знайдена.");
            return false;
        }

        int stageNumber = 0;
        if (args.length >= 2) {
            try {
                stageNumber = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Номер стадії повинен бути числом.");
                return false;
            }
        }

        String stagePath = structurePath + ".stages." + stageNumber;
        if (!plugin.getConfig().contains(stagePath)) {
            sender.sendMessage("Стадія з номером " + stageNumber + " не знайдена.");
            return false;
        }

        World world = Bukkit.getWorlds().get(0);
        if (world == null) {
            sender.sendMessage("Світ за замовчуванням не знайдено.");
            return false;
        }

        Location stagePos1 = getLocationFromConfig(stagePath + ".pos1", world);
        Location stagePos2 = getLocationFromConfig(stagePath + ".pos2", world);
        Location structurePos1 = getLocationFromConfig(structurePath + ".pos1", world);

        if (stagePos1 == null || stagePos2 == null || structurePos1 == null) {
            sender.sendMessage("Некоректні координати для стадії або структури.");
            return false;
        }

        ConfigurationSection structureSection = plugin.getConfig().getConfigurationSection("structures." + structureName);
        assert structureSection != null;
        int totalStagesCount = structureSection.getKeys(false).size() - 1;
        for (int i = 0; i < totalStagesCount; i++) {
            structureSection.set("stages." + i + ".isCurrentStage", false);
        }

        plugin.getConfig().set(stagePath + ".isCurrentStage", true);
        plugin.saveConfig();

        copyStageToStructure(stagePos1, stagePos2, structurePos1, sender, world);

        sender.sendMessage("Структура '" + structureName + "' успішно ініціалізована стадією " + stageNumber + ".");
        return true;
    }

    private Location getLocationFromConfig(String path, World world) {
        if (!plugin.getConfig().contains(path)) return null;
        int x = plugin.getConfig().getInt(path + ".x");
        int y = plugin.getConfig().getInt(path + ".y");
        int z = plugin.getConfig().getInt(path + ".z");
        return new Location(world, x, y, z);
    }

    private void copyStageToStructure(Location stagePos1, Location stagePos2, Location structurePos1, CommandSender sender, World world) {
        int minX = Math.min(stagePos1.getBlockX(), stagePos2.getBlockX());
        int minY = Math.min(stagePos1.getBlockY(), stagePos2.getBlockY());
        int minZ = Math.min(stagePos1.getBlockZ(), stagePos2.getBlockZ());

        int maxX = Math.max(stagePos1.getBlockX(), stagePos2.getBlockX());
        int maxY = Math.max(stagePos1.getBlockY(), stagePos2.getBlockY());
        int maxZ = Math.max(stagePos1.getBlockZ(), stagePos2.getBlockZ());

        int structureXOffset = structurePos1.getBlockX() - minX;
        int structureYOffset = structurePos1.getBlockY() - minY;
        int structureZOffset = structurePos1.getBlockZ() - minZ;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location stageLocation = new Location(world, x, y, z);
                    Block stageBlock = stageLocation.getBlock();

                    int targetX = x + structureXOffset;
                    int targetY = y + structureYOffset;
                    int targetZ = z + structureZOffset;

                    Location targetLocation = new Location(world, targetX, targetY, targetZ);
                    Block targetBlock = targetLocation.getBlock();

                    BlockData blockData = stageBlock.getBlockData();
                    targetBlock.setBlockData(blockData, false);
                }
            }
        }
    }
}
