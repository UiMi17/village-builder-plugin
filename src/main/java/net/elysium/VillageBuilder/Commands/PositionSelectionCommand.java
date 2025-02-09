package net.elysium.VillageBuilder.Commands;

import net.elysium.VillageBuilder.Commands.Services.SelectionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PositionSelectionCommand implements CommandExecutor {
    private final SelectionManager selectionManager;
    private final Map<Player, BukkitTask> particleTasks = new HashMap<>();

    public PositionSelectionCommand(SelectionManager selectionManager) {
        this.selectionManager = selectionManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (args.length == 0) {
            player.sendMessage("Використання: /pos [індекс_позиції]");
            return false;
        }

        if (args[0].equalsIgnoreCase("cancel")) {
            cancelParticleTask(player);
            selectionManager.clearSelection(player);
            return false;
        }

        int positionIndex = args[0].equalsIgnoreCase("1") ? 0 : 1;
        Location location = player.getLocation();
        selectionManager.setSelection(player, positionIndex, location);

        player.sendMessage("Позиція " + (positionIndex + 1) + " встановлена!");

        Location[] selection = selectionManager.getSelection(player);
        if (selection[0] != null && selection[1] != null) {
            startParticleOutline(player, selection[0], selection[1]);
        }

        return true;
    }

    private void startParticleOutline(Player player, Location pos1, Location pos2) {
        cancelParticleTask(player);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("VillageBuilder")),
                () -> drawSelectionOutline(player, pos1, pos2),
                0L, 10L
        );

        particleTasks.put(player, task);
    }

    private void drawSelectionOutline(Player player, Location pos1, Location pos2) {
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                boolean isBoundary = (x == minX || x == maxX || z == minZ || z == maxZ);
                if (isBoundary) {
                    for (int y = maxY; y >= minY; y--) {
                        Location loc = new Location(player.getWorld(), x + 0.5, y + 0.5, z + 0.5);
                        player.getWorld().spawnParticle(Particle.FLAME, loc, 1, 0, 0, 0, 0);
                    }
                }
            }
        }
    }



    public void cancelParticleTask(Player player) {
        if (particleTasks.containsKey(player)) {
            particleTasks.get(player).cancel();
            particleTasks.remove(player);
        }
    }
}
