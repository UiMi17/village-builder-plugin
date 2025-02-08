package net.elysium.VillageBuilder.Commands;

import net.elysium.VillageBuilder.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AddResourcesCommand implements CommandExecutor {

    private final Main plugin;

    public AddResourcesCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length != 4) {
            sender.sendMessage("Використання: /add-resources <назва_структури> <стадія> <назва_ресурсу> <ім'я_гравця>");
            return false;
        }

        String structureName = args[0];
        String stageId = args[1];
        String resourceName = args[2].toUpperCase();
        String playerName = args[3];

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage("Гравця з іменем " + playerName + " не знайдено.");
            return false;
        }

        Material resourceMaterial = Material.matchMaterial(resourceName);
        if (resourceMaterial == null) {
            sender.sendMessage("Ресурс " + resourceName + " не знайдено.");
            return false;
        }

        int requiredAmount = plugin.getConfig().getInt("structures." + structureName + ".stages." + stageId + ".resources." + resourceName, -1);
        if (requiredAmount == -1) {
            sender.sendMessage("Ресурс " + resourceName + " не заданий для стадії " + stageId + " структури " + structureName + ".");
            return false;
        }

        int playerResourceCount = countPlayerItems(player, resourceMaterial);
        if (playerResourceCount == 0) {
            sender.sendMessage("У гравця немає ресурсів типу " + resourceName + ".");
            return false;
        }

        int amountToSubtract = Math.min(playerResourceCount, requiredAmount);

        removePlayerItems(player, resourceMaterial, amountToSubtract);

        int remainingAmount = requiredAmount - amountToSubtract;
        plugin.getConfig().set("structures." + structureName + ".stages." + stageId + ".resources." + resourceName, Math.max(remainingAmount, 0));
        plugin.saveConfig();

        sender.sendMessage("Успішно додано " + amountToSubtract + " " + resourceName + " для структури " + structureName + " (стадія " + stageId + ").");

        if (checkAllResourcesCompleted(structureName, stageId)) {
            sender.sendMessage("§aВсі ресурси для стадії " + stageId + " завершені!");

            int nextStageId = Integer.parseInt(stageId) + 1;
            if (plugin.getConfig().contains("structures." + structureName + ".stages." + nextStageId)) {
                sender.sendMessage("§aПерехід до наступної стадії: " + nextStageId);

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "initialize-structure " + structureName + " " + nextStageId);
            } else {
                sender.sendMessage("§cНаступна стадія для структури " + structureName + " не знайдена.");
            }
        }

        return true;
    }

    private boolean checkAllResourcesCompleted(String structureName, String stageId) {
        var resourceSection = plugin.getConfig().getConfigurationSection("structures." + structureName + ".stages." + stageId + ".resources");
        if (resourceSection == null) return false;

        for (String resource : resourceSection.getKeys(false)) {
            int remainingAmount = plugin.getConfig().getInt("structures." + structureName + ".stages." + stageId + ".resources." + resource);
            if (remainingAmount > 0) {
                return false;
            }
        }
        return true;
    }

    private int countPlayerItems(Player player, Material material) {
        int count = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() == material) {
                count += itemStack.getAmount();
            }
        }
        return count;
    }

    private void removePlayerItems(Player player, Material material, int amount) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() == material) {
                int stackAmount = itemStack.getAmount();
                if (stackAmount <= amount) {
                    player.getInventory().remove(itemStack);
                    amount -= stackAmount;
                } else {
                    itemStack.setAmount(stackAmount - amount);
                    break;
                }
            }
        }
    }
}
