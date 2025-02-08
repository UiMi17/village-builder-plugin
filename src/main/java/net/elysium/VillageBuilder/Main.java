package net.elysium.VillageBuilder;

import net.elysium.VillageBuilder.Commands.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Level;

public final class Main extends JavaPlugin {
    SelectionManager selectionManager = new SelectionManager();
    PositionSelectionCommand positionSelectionCommand = new PositionSelectionCommand(selectionManager);
    StructureManager structureManager = new StructureManager(selectionManager, positionSelectionCommand, this);
    private FileConfiguration config = null;
    private File configFile = null;

    @Override
    public void onEnable(){


        registerCommands();
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(getDataFolder(), "data.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        Reader defConfigStream = new InputStreamReader(Objects.requireNonNull(this.getResource("data.yml")), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        config.setDefaults(defConfig);
    }

    public @NotNull FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("pos")).setExecutor(positionSelectionCommand);
        Objects.requireNonNull(getCommand("create-structure")).setExecutor(new CreateStructureCommand(selectionManager, positionSelectionCommand, this));
        Objects.requireNonNull(getCommand("assign-stage")).setExecutor(new AssignStageCommand(structureManager));
        Objects.requireNonNull(getCommand("assign-resources")).setExecutor(new AssignResourcesCommand(this));
        Objects.requireNonNull(getCommand("initialize-structure")).setExecutor(new InitializeStructureCommand(this));
    }
}
