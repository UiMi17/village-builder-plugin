package net.elysium.VillageBuilder.Commands;

import net.elysium.VillageBuilder.Main;
import net.elysium.VillageBuilder.Utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

public class InitializeScoreboardCommand implements CommandExecutor {
    private final Main plugin;
    private BukkitTask scoreboardUpdateTask;

    public InitializeScoreboardCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "Цю команду можна виконувати лише як гравець.");
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Використання: /scoreboard <назва_структури|cancel>");
            return false;
        }

        if (args[0].equalsIgnoreCase("cancel")) {
            cancelScoreboard(player);
            return true;
        }

        String structureName = args[0];
        String structurePath = "structures." + structureName;

        if (!plugin.getConfig().contains(structurePath)) {
            player.sendMessage(ChatColor.RED + "Структура '" + structureName + "' не знайдена.");
            return false;
        }

        int currentStage = findCurrentStage(structurePath);
        if (currentStage == -1) {
            player.sendMessage(ChatColor.RED + "Не знайдено поточної стадії для структури.");
            return false;
        }

        String stagePath = structurePath + ".stages." + currentStage;
        String stageName = plugin.getConfig().getString(stagePath + ".name", "Невідомо");
        ConfigurationSection resourceSection = plugin.getConfig().getConfigurationSection(stagePath + ".resources");

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("progressionScoreboard", "dummy", ChatColor.BOLD + "⚒ " + ChatColor.GOLD + "Village Builder" + ChatColor.RESET + " ⚒");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        updateScoreboard(player, structureName, stageName, resourceSection);

        player.setScoreboard(board);
        player.sendMessage(ChatColor.GREEN + "Scoreboard успішно створено для структури '" + structureName + "'.");

        startScoreboardUpdateTimer(player, structureName, currentStage, resourceSection);

        return true;
    }

    private int findCurrentStage(String structurePath) {
        ConfigurationSection structureStageSection = plugin.getConfig().getConfigurationSection(structurePath + ".stages");
        assert structureStageSection != null;
        for (String key : structureStageSection.getKeys(false)) {
            if (plugin.getConfig().getBoolean(structurePath + ".stages." + key + ".isCurrentStage", false)) {
                return Integer.parseInt(key);
            }
        }
        return -1;
    }

    private void updateScoreboard(Player player, String structureName, String stageName, ConfigurationSection resourceSection) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective newObjective = board.registerNewObjective("progressionScoreboard", "dummy", ChatColor.BOLD + "⚒ " + ChatColor.GOLD + "Village Builder" + ChatColor.RESET + " ⚒");
        newObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        newObjective.getScore(ChatColor.DARK_GRAY + "---------------------- ").setScore(6);
        newObjective.getScore(ChatColor.YELLOW + "Структура: " + ChatColor.AQUA + structureName).setScore(5);
        newObjective.getScore(ChatColor.YELLOW + "Стадія: " + ChatColor.AQUA + stageName).setScore(4);

        int scoreIndex = 3;
        newObjective.getScore(ChatColor.GOLD + "Ресурси:").setScore(scoreIndex--);

        if (resourceSection != null && !resourceSection.getKeys(false).isEmpty()) {
            for (String resourceName : resourceSection.getKeys(false)) {
                int remainingAmount = resourceSection.getInt(resourceName);

                String resourceDisplay;
                if (remainingAmount == 0) {
                    resourceDisplay = ChatColor.GRAY + "➤ " + ChatColor.WHITE + StringUtils.formatResourceName(resourceName) + ": " + ChatColor.GREEN + "✔";
                } else {
                    resourceDisplay = ChatColor.GRAY + "➤ " + ChatColor.WHITE + StringUtils.formatResourceName(resourceName) + ": " + ChatColor.RED + remainingAmount;
                }

                newObjective.getScore(resourceDisplay).setScore(scoreIndex--);
            }
        }

        assert resourceSection != null;
        int lastLineIndex = resourceSection.getKeys(false).size();
        newObjective.getScore(ChatColor.DARK_GRAY + "----------------------").setScore(Integer.parseInt("-" + lastLineIndex));

        player.setScoreboard(board);
    }

    private void startScoreboardUpdateTimer(Player player, String structureName, int currentStage, ConfigurationSection resourceSection) {
        scoreboardUpdateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateScoreboard(player, structureName, plugin.getConfig().getString("structures." + structureName + ".stages." + currentStage + ".name"), resourceSection);
            }
        }.runTaskTimer(plugin, 0, 300);
    }

    private void cancelScoreboard(Player player) {
        if (scoreboardUpdateTask != null) {
            scoreboardUpdateTask.cancel();
        }
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        player.sendMessage(ChatColor.RED + "Scoreboard видалено.");
    }
}
