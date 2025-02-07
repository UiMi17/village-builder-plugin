package net.elysium.VillageBuilder.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ResourceAssignmentCommand implements CommandExecutor {
    private final FileConfiguration config;
    private final File dataFile;

    public ResourceAssignmentCommand() {
        this.dataFile = new File("plugins/VillageBuilder", "data.yml");
        this.config = YamlConfiguration.loadConfiguration(dataFile);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (args.length < 3) {
            player.sendMessage("Використання: /assign-resources <назва_структури> <порядковий_номер_стадії> <список_ресурсів>");
            return false;
        }

        String structureName = args[0];
        int stageNumber;

        try {
            stageNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Порядковий номер стадії повинен бути числом.");
            return false;
        }

        String resourcePath = "structures." + structureName + ".stages." + stageNumber;
        if (!Objects.requireNonNull(config.getString(resourcePath)).contains(resourcePath)) {
            player.sendMessage("Стадія або структура не знайдені.");
            return false;
        }

        String[] resourceInputs = Arrays.copyOfRange(args, 2, args.length);
        List<String> resources = Arrays.asList(resourceInputs);

        config.set(resourcePath + ".resources", resources);
        saveData();

        player.sendMessage("Ресурси " + resources + " додані до стадії " + stageNumber + " структури '" + structureName + "'.");
        return true;
    }

    private void saveData() {
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
