package net.elysium.VillageBuilder.Commands;

import net.elysium.VillageBuilder.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InitializeStructureCommand implements CommandExecutor {
    Main plugin;
    
    public InitializeStructureCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (args.length < 1) {
            player.sendMessage("Використання: /initialize-structure <назва_структури> [номер_стадії]");
            return false;
        }

        String structureName = args[0];
        String structurePath = "structures." + structureName;
        String structure = plugin.getConfig().getString(structurePath);
        if (structure == null) {
            player.sendMessage("Структура '" + structureName + "' не знайдена.");
            return false;
        }

        int stageNumber = 0;
        if (args.length >= 2) {
            try {
                stageNumber = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("Номер стадії повинен бути числом.");
                return false;
            }
        }

        String stagePath = structurePath + ".stages." + stageNumber;
        if (!plugin.getConfig().contains(stagePath)) {
            player.sendMessage("Стадія з номером " + stageNumber + " не знайдена.");
            return false;
        }

        Location stagePos1 = getLocationFromConfig(stagePath + ".pos1", player.getWorld());
        Location stagePos2 = getLocationFromConfig(stagePath + ".pos2", player.getWorld());

        if (stagePos1 == null || stagePos2 == null) {
            player.sendMessage("Некоректні координати для стадії.");
            return false;
        }

        Location structurePos1 = getLocationFromConfig(structurePath + ".pos1", player.getWorld());
        Location structurePos2 = getLocationFromConfig(structurePath + ".pos2", player.getWorld());

        if (structurePos1 == null || structurePos2 == null) {
            player.sendMessage("Некоректні координати для структури.");
            return false;
        }

        copyStageToStructure(stagePos1, stagePos2, structurePos1, player);

        player.sendMessage("Структура '" + structureName + "' успішно ініціалізована стадією " + stageNumber + ".");
        return true;
    }

    private Location getLocationFromConfig(String path, World world) {
        if (!plugin.getConfig().contains(path)) return null;
        int x = plugin.getConfig().getInt(path + ".x");
        int y = plugin.getConfig().getInt(path + ".y");
        int z = plugin.getConfig().getInt(path + ".z");
        return new Location(world, x, y, z);
    }

    private void copyStageToStructure(Location stagePos1, Location stagePos2, Location structurePos1, Player player) {
        int minX = Math.min(stagePos1.getBlockX(), stagePos2.getBlockX());
        int minY = Math.min(stagePos1.getBlockY(), stagePos2.getBlockY());
        int minZ = Math.min(stagePos1.getBlockZ(), stagePos2.getBlockZ());

        int maxX = Math.max(stagePos1.getBlockX(), stagePos2.getBlockX());
        int maxY = Math.max(stagePos1.getBlockY(), stagePos2.getBlockY());
        int maxZ = Math.max(stagePos1.getBlockZ(), stagePos2.getBlockZ());

        int structureXOffset = structurePos1.getBlockX() - minX;
        int structureYOffset = structurePos1.getBlockY() - minY;
        int structureZOffset = structurePos1.getBlockZ() - minZ;

        World world = stagePos1.getWorld();

        if (world == null) {
            player.sendMessage("Світ для копіювання не знайдено.");
            return;
        }

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location stageLocation = new Location(world, x, y, z);
                    Material blockMaterial = stageLocation.getBlock().getType();

                    int targetX = x + structureXOffset;
                    int targetY = y + structureYOffset;
                    int targetZ = z + structureZOffset;

                    Location targetLocation = new Location(world, targetX, targetY, targetZ);
                    targetLocation.getBlock().setType(blockMaterial);
                }
            }
        }
    }
}
