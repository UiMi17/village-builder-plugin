package net.elysium.VillageBuilder.Commands.Services;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SelectionManager {

    private final Map<Player, Location[]> playerSelections = new HashMap<>();

    public void setSelection(Player player, int index, Location location) {
        playerSelections.putIfAbsent(player, new Location[2]);
        playerSelections.get(player)[index] = location;
    }

    public Location[] getSelection(Player player) {
        return playerSelections.get(player);
    }

    public void clearSelection(Player player) {
        playerSelections.remove(player);
    }
}
